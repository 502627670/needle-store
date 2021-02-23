package org.needle.bookingdiscount.server.service;

import java.util.List;
import java.util.Optional;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.Order;
import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.order.OrderGoodsRepository;
import org.needle.bookingdiscount.server.repository.order.OrderRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.needle.bookingdiscount.server.repository.product.ProductRepository;
import org.needle.bookingdiscount.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class PayService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderGoodsRepository orderGoodsRepository;
	
	@Autowired
	private GoodsRepository goodsRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private WxPayService wxService;
	
	public Object preWeixinPay(String appid, Long orderId, String clientIp) {
		Optional<Order> orderInfoOpt = orderRepository.findById(orderId);
		if(!orderInfoOpt.isPresent()) {
        	throw new ServiceException(400, "订单已取消");
        }
		Order orderInfo = orderInfoOpt.get();
		List<OrderGoods> orderGoods = orderGoodsRepository.findByOrder(orderId);
		
		// 再次确认库存和价格
		int checkPrice = 0;
		int checkStock = 0;
		
        for(OrderGoods item : orderGoods) {
        	Product product = item.getProduct();
            if(item.getNumber() > product.getGoodsNumber()) {
                checkStock++;
            }
            if(Math.abs(item.getRetailPrice().doubleValue() - product.getRetailPrice().doubleValue()) > 0.01) {
                checkPrice++;
            }
        }
        if(checkStock > 0) {
        	throw new ServiceException(400, "库存不足，请重新下单");
        }
        if (checkPrice > 0) {
        	throw new ServiceException(400, "价格发生变化，请重新下单");
        }
        if(orderInfo.getPayStatus() != 0) {
        	throw new ServiceException(400, "订单已支付，请不要重复操作");
        }
        
        MemberUser user = orderInfo.getUser();
        String openid = user.getWeixinOpenid();
        if(!StringUtils.hasText(openid)) {
        	throw new ServiceException(400, "微信支付失败");
        }
        
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setAppid(appid);
        request.setOpenid(openid);
        request.setBody("【订单号】" + orderInfo.getOrderSn());
        request.setOutTradeNo(orderInfo.getOrderSn());
        request.setTotalFee((int) (orderInfo.getActualPrice().doubleValue() * 100));
        request.setSpbillCreateIp(clientIp);
        request.setTradeType("JSAPI");
        request.setNotifyUrl(wxService.getConfig().getNotifyUrl());
        try {
			return wxService.createOrder(request);
		} 
        catch (WxPayException e) {
        	log.error("订单{}微信支付失败：{}", orderInfo.getOrderSn(), e.getMessage(), e);
			throw new ServiceException(400, "微信支付失败", e);
		}
	}
	
	public String notify(String xml) {
		log.info("notify(..) => weixin_pay_notify_xml={}", xml);
		WxPayOrderNotifyResult notifyResult;
		try {
			notifyResult = this.wxService.parseOrderNotifyResult(xml);
		} 
		catch (WxPayException e) {
			log.error("notify(..) => 解析支付回调报文失败", e);
			return "FAIL";
		}
        if(notifyResult == null) {
        	return "FAIL";
        }
        String orderSn = notifyResult.getOutTradeNo();
        Order orderInfo = orderRepository.getByOrderSn(orderSn);
        if(orderInfo == null) {
        	return "FAIL";
        }
        
        log.debug("notify(..) => notify order: orderSn={}, payStatus={}, actualPrice={}", 
        		orderSn, orderInfo.getPayStatus(), orderInfo.getActualPrice());
        
        boolean bool = orderInfo.getPayStatus() == 2 ? false : true;
        if(bool == true) {
        	if(orderInfo.getOrderType() == 0) {
                //普通订单和秒杀订单
        		orderInfo.setPayStatus(2);
        		orderInfo.setOrderStatus(300);
        		orderInfo.setPayId(notifyResult.getTransactionId());
        		orderInfo.setPayTime((int) (DateUtils.parseDate(notifyResult.getTimeEnd(), "yyyyMMddHHmmss").getTime()/1000));
        		orderRepository.save(orderInfo);
        		afterPay(orderInfo);
            }
        } 
        else {
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[订单已支付]]></return_msg></xml>";
        }
        return "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>";
        // return "SUCCESS";
    }
	
	private void afterPay(Order orderInfo) {
		if(orderInfo.getOrderType() == 0) {
			List<OrderGoods> orderGoodsList = orderGoodsRepository.findByOrder(orderInfo.getId());
            for(OrderGoods cartItem : orderGoodsList) {
            	Goods goods = cartItem.getGoods();
            	Product product = cartItem.getProduct();
            	int number = cartItem.getNumber();
            	goodsRepository.decrement(number, goods.getId());
            	goodsRepository.incrementSellVolume(number, goods.getId());
            	productRepository.decrement(number, product.getId());
            }
        }
	}
	
}
