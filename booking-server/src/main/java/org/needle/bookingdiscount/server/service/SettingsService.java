package org.needle.bookingdiscount.server.service;

import java.util.Optional;
import java.util.regex.Pattern;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.member.MemberUser.JsonMemberUser;
import org.needle.bookingdiscount.domain.support.ShowSettings;
import org.needle.bookingdiscount.domain.support.ShowSettings.JsonShowSettings;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.member.MemberUserRepository;
import org.needle.bookingdiscount.server.repository.support.ShowSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class SettingsService {
	
	private Pattern mobilePattern = Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1})|(16[0-9]{1})|(19[0-9]{1}))+\\d{8})$");
	
	@Autowired
	private ShowSettingsRepository showSettingsRepository;
	
	@Autowired
	private MemberUserRepository memberUserRepository;
	
	public JsonShowSettings showSettings() {
		Optional<ShowSettings> ssOpt = showSettingsRepository.findById(1L);
		ShowSettings info = ssOpt.isPresent() ? ssOpt.get() : new ShowSettings();
		return new JsonShowSettings(info);
	}
	
	public JsonMemberUser save(Long userId, String name, String mobile) {
        if(mobile.length() < 11) {
        	throw new ServiceException(200, "手机号长度不对");
        } 
        else if(mobilePattern.matcher(mobile).find()) {
        	throw new ServiceException(300, "手机号不对");
        }
        if(StringUtils.hasText(name) || StringUtils.hasText(mobile)) {
            throw new ServiceException(100, "姓名或手机不能为空");
        }
        
        MemberUser user = memberUserRepository.findById(userId).get();
        user.setName(name);
        user.setMobile(mobile);
        user.setNameMobile(1);
        memberUserRepository.save(user);
        
        return new JsonMemberUser(user);
	}
	
	public JsonMemberUser userDetail(Long userId) {
		MemberUser user = memberUserRepository.findById(userId).get();
		return new JsonMemberUser(user);
	}
	
}
