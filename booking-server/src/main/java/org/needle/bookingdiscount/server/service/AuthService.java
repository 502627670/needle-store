package org.needle.bookingdiscount.server.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.member.MemberUser.JsonMemberUser;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.member.MemberUserRepository;
import org.needle.bookingdiscount.utils.Base64Utils;
import org.needle.bookingdiscount.wechat.WxMaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

@Slf4j
@Service
@Transactional
public class AuthService {
	
	@Autowired
	private MemberUserRepository userRepository;
	
	public ModelMap login(WxUserInfo fullUserInfo, String appid, String code, String clientIp) {
		int currentTime = (int) (new Date().getTime() / 1000);
		
		final WxMaService wxService = WxMaConfiguration.getMaService(appid);
		WxMaJscode2SessionResult sessionData = null;
        try {
        	sessionData = wxService.getUserService().getSessionInfo(code);
            log.info("sessionKey={}, openid={}", sessionData.getSessionKey(), sessionData.getOpenid());
        }
        catch(WxErrorException e) {
            throw new ServiceException("登录失败");
        }
		
		String sessionKey = sessionData.getSessionKey();
		// String openid = sessionData.getOpenid();
		// 用户信息校验
        if(!wxService.getUserService().checkUserInfo(sessionKey, fullUserInfo.rawData, fullUserInfo.signature)) {
            throw new ServiceException("用户身份识别失败");
        }
        
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, fullUserInfo.encryptedData, fullUserInfo.iv);
        String nickName = Base64Utils.encode(userInfo.getNickName());

        // 根据openid查找用户是否已经注册
		List<MemberUser> userList = userRepository.findByWeixinOpenid(sessionData.getOpenid());
		int is_new = 0;
		MemberUser user = new MemberUser();
		if(userList.isEmpty()) {
			user.setUsername("微信用户");
			user.setPassword(userInfo.getOpenId());
			user.setRegisterTime(currentTime);
			user.setRegisterIp(clientIp);
			user.setLastLoginTime(currentTime);
			user.setLastLoginIp(clientIp);
			user.setMobile("");
			user.setWeixinOpenid(userInfo.getOpenId());
			user.setAvatar(userInfo.getAvatarUrl());
			user.setGender(Integer.valueOf(userInfo.getGender()));   // 性别 0：未知、1：男、2：女
			user.setNickname(nickName);
			user.setCountry(userInfo.getCountry());
			user.setProvince(userInfo.getProvince());
			user.setCity(userInfo.getCity());
			user.setBirthday(0);
			user.setName("微信用户");
			user.setNameMobile(0);
			userRepository.save(user);
			
			is_new = 1;
		}
		else {
			user = userList.get(0);
			user.setLastLoginTime(currentTime);
			user.setLastLoginIp(clientIp);
			user.setAvatar(userInfo.getAvatarUrl());
			user.setNickname(nickName);
			user.setCountry(userInfo.getCountry());
			user.setProvince(userInfo.getProvince());
			user.setCity(userInfo.getCity());
			userRepository.save(user);
		}
		
		JsonMemberUser memberUser = new JsonMemberUser(user);
		memberUser.setNickname(Base64Utils.decode(memberUser.getNickname()));
		
		ModelMap model = new ModelMap();
		model.addAttribute("token", sessionKey);
		model.addAttribute("userInfo", memberUser);
		model.addAttribute("is_new", is_new);
		
		return model;
	}
	
	@Data
	public static class WxUserInfo {
		String errMsg;
		HashMap<String,String> clientInfo = new HashMap<String,String>();
		HashMap<String,String> userInfo = new HashMap<String,String>();
		// {"nickName":"zhao","gender":1,"language":"zh_CN","city":"","province":"Shanghai","country":"China","avatarUrl":"https://thirdwx.qlogo.cn/mmopen/vi_32/FQqRPpJ6x9MT6VnzCOIciaJibjzxL5eQz8tXPPqgct8zOM49icMrxToaN6qUgu6VdgwkUbA0zCv3EntoaKbiacMOHA/132"}
		String rawData;
		String signature;
		String encryptedData;
		String iv;
	}
}
