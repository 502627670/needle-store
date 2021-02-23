package org.needle.bookingdiscount.server.api;

import java.util.Base64;
import java.util.Map;

import org.needle.bookingdiscount.server.data.ResponseMessage;
import org.needle.bookingdiscount.wechat.WxMaConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

@Slf4j
@Controller
@RequestMapping("/qrcode")
public class QrcodeController {
	
	@RequestMapping("/getBase64")
	@ResponseBody
	public Object getBase64(@RequestBody Map<String,Object> data) {
		String appid = (String) data.get("appid");
		Long goodsId = Long.valueOf(data.get("goodsId").toString());
		final WxMaService wxService = WxMaConfiguration.getMaService(appid);
		String scene = goodsId.toString();
		String page = "pages/goods/goods";
		int width = 200;
		
		WxMaCodeLineColor lineColor = new WxMaCodeLineColor("0", "0", "0");
		byte[] bytes = new byte[0];
		try {
			bytes = wxService.getQrcodeService().createWxaCodeUnlimitBytes(scene, page, width, true, lineColor, true);
		} 
		catch (WxErrorException e) {
			log.error("getBase64(..) => createWxaCodeUnlimitBytes失败:{}", e.getMessage());
		}
		String base64 = Base64.getEncoder().encodeToString(bytes);
		log.debug("base64:{}", base64);
		return ResponseMessage.success(base64);
	}
	
}
