package org.needle.bookingdiscount.server.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.cart.Cart;
import org.needle.bookingdiscount.domain.cart.Cart.JsonCart;
import org.needle.bookingdiscount.domain.member.Address;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.Order;
import org.needle.bookingdiscount.domain.order.Order.HandleOption;
import org.needle.bookingdiscount.domain.order.Order.JsonOrder;
import org.needle.bookingdiscount.domain.order.Order.TextCode;
import org.needle.bookingdiscount.domain.order.OrderExpress;
import org.needle.bookingdiscount.domain.order.OrderExpress.JsonOrderExpress;
import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.needle.bookingdiscount.domain.order.OrderGoods.JsonOrderGoods;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.cart.CartRepository;
import org.needle.bookingdiscount.server.repository.member.AddressRepository;
import org.needle.bookingdiscount.server.repository.member.MemberUserRepository;
import org.needle.bookingdiscount.server.repository.order.OrderExpressRepository;
import org.needle.bookingdiscount.server.repository.order.OrderGoodsRepository;
import org.needle.bookingdiscount.server.repository.order.OrderRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.needle.bookingdiscount.server.repository.product.ProductRepository;
import org.needle.bookingdiscount.server.repository.support.RegionRepository;
import org.needle.bookingdiscount.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;

@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CartRepository cartRepository;
		
	@Autowired
	private MemberUserRepository memberUserRepository;
	
	@Autowired
	private GoodsRepository goodsRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderGoodsRepository orderGoodsRepository;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired
	private OrderExpressRepository orderExpressRepository;
	
	@Value("${aliexpress.sfLastNo}")
	private String sfLastNo;
	
	@Value("${aliexpress.appcode}")
	private String appcode;
	
	public static void main(String[] args) {
		System.out.println(String.format("%08d", 1));
		System.out.println(new OrderService().generateOrderNumber(1L));
		System.out.println(new Date().getTime() / 1000);
		System.out.println(new Long(new Date().getTime() / 1000).intValue());
		System.out.println((int) (new Date().getTime() / 1000));
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(1613141392 * 1000L)));
		
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
		System.out.println(new Random().nextInt(100));
	}
	
	private String generateOrderNumber(Long id) {
		String yyyyMMdd = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Long seq = id % 99999999;
		return new StringBuilder()
				.append(yyyyMMdd.charAt(3))
				.append(yyyyMMdd.charAt(2))
				.append(yyyyMMdd.substring(0, 2))
				.append(yyyyMMdd.substring(4))
				.append(new Random().nextInt(100))
				.append(String.format("%09d", seq)).toString();
	}
	
	public ModelMap submit(Long userId, Long addressId, BigDecimal freightPrice, 
			int offlinePay, String postscript) {
		if(addressId == null) {
			throw new ServiceException("请选择收货 地址");
		}
		Optional<Address> addressOpt = addressRepository.findById(addressId);
		if(!addressOpt.isPresent()) {
			throw new ServiceException("收货地址不存在 ");
		}
		Address checkedAddress = addressOpt.get();
		
		// 获取要购买的商品
		List<Cart> checkedGoodsList = cartRepository.findByUserAndCheckedAndIsDelete(userId, 1, false);
        if(checkedGoodsList.isEmpty()) {
        	throw new ServiceException("请选择商品 ");
        }
        
        int checkPrice = 0;
        int checkStock = 0;
        for (Cart item : checkedGoodsList) {
        	Product product = item.getProduct();
            if(item.getNumber() > product.getGoodsNumber()) {
                checkStock++;
            }
            if(Math.abs(item.getRetailPrice().subtract(item.getAddPrice()).doubleValue()) > 0.01) {
            	checkPrice++;
            }
        }
        if(checkStock > 0) {
        	throw new ServiceException(400, "库存不足，请重新下单 ");
        }
        if(checkPrice > 0) {
            throw new ServiceException(400, "价格发生变化，请重新下单 ");
        }
        
        // 获取订单使用的红包
        // 如果有用红包，则将红包的数量减少，当减到0时，将该条红包删除
        // 统计商品总价
        BigDecimal goodsTotalPrice = new BigDecimal(0.00);
        for (Cart cartItem : checkedGoodsList) {
        	goodsTotalPrice = goodsTotalPrice.add(new BigDecimal(cartItem.getNumber()).multiply(cartItem.getRetailPrice()));
        }
        // 订单价格计算
        BigDecimal orderTotalPrice = goodsTotalPrice.add(freightPrice); // 订单的总价
        BigDecimal actualPrice = orderTotalPrice.subtract(new BigDecimal(0.00)); // 减去其它支付的金额后，要实际支付的金额 比如满减等优惠
        int currentTime = new Long(new Date().getTime() / 1000).intValue();
        
        String print_info = "";
        for(int i = 0; i < checkedGoodsList.size(); i++) {
        	Cart item = checkedGoodsList.get(i);
        	print_info = print_info + (i + 1) + '、' + item.getGoodsAka() + "【" + item.getNumber() + "】 ";
        }
        
        MemberUser user = memberUserRepository.findById(userId).get();
        Order orderInfo = new Order();
        orderInfo.setUser(user);
        orderInfo.setOrderSn(System.currentTimeMillis() + "");
        orderInfo.setConsignee(checkedAddress.getName());
        orderInfo.setMobile(checkedAddress.getMobile());
        orderInfo.setCountry(1L);
        orderInfo.setProvince(checkedAddress.getProvinceId());
        orderInfo.setCity(checkedAddress.getCityId());
        orderInfo.setDistrict(checkedAddress.getDistrictId());
        orderInfo.setAddress(checkedAddress.getAddress());
        orderInfo.setOrderStatus(101);
        orderInfo.setFreightPrice(freightPrice.intValue());
        orderInfo.setPostscript(Base64Utils.encodeToString(postscript.getBytes()));
        orderInfo.setAddTime(currentTime);
        orderInfo.setConfirmTime(0);
        orderInfo.setPayTime(0);
        orderInfo.setShippingTime(0);
        orderInfo.setDealdoneTime(0);
        orderInfo.setGoodsPrice(goodsTotalPrice);
        orderInfo.setOrderPrice(orderTotalPrice);
        orderInfo.setActualPrice(actualPrice);
        orderInfo.setChangePrice(actualPrice);
        orderInfo.setPrintInfo(print_info);
        orderInfo.setOfflinePay(offlinePay);
        orderInfo.setExpressValue(new BigDecimal(0.0));
        orderInfo.setPayId("0");
        orderInfo.setPayName("");
        orderInfo.setPayStatus(0);
        orderInfo.setPrintStatus(0);
        orderInfo.setOrderType(0);
        orderInfo.setRemark("");
        orderInfo.setShippingFee(new BigDecimal(0));
        orderInfo.setShippingStatus(0);
        orderInfo.setIsDelete(false);
        orderRepository.save(orderInfo);
        
        orderInfo.setOrderSn(generateOrderNumber(orderInfo.getId()));
        orderRepository.save(orderInfo);
        
        // 将商品信息录入数据库
        for(Cart goodsItem : checkedGoodsList) {
        	OrderGoods orderGoods = new OrderGoods();
        	orderGoods.setUser(user);
        	orderGoods.setOrder(orderInfo);
        	orderGoods.setGoods(goodsItem.getGoods());
        	orderGoods.setProduct(goodsItem.getProduct());
        	orderGoods.setGoodsName(goodsItem.getGoodsName());
        	orderGoods.setGoodsAka(goodsItem.getGoodsAka());
        	orderGoods.setListPicUrl(goodsItem.getListPicUrl());
        	orderGoods.setRetailPrice(goodsItem.getRetailPrice());
        	orderGoods.setNumber(goodsItem.getNumber());
        	orderGoods.setGoodsSpecificationNameValue(goodsItem.getGoodsSpecificationNameValue());
        	orderGoods.setGoodsSpecificationIds(goodsItem.getGoodsSpecificationIds());
        	orderGoods.setIsDelete(false);
        	orderGoodsRepository.save(orderGoods);
        }
        
        cartRepository.findByUserAndCheckedAndIsDelete(userId, 1, false).forEach(cart -> {
        	cart.setIsDelete(true);
        	cartRepository.save(cart);
        });
        
        return new ModelMap().addAttribute("orderInfo", new JsonOrder(orderInfo));
	}
	
	public List<JsonOrder> list(Long userId, int showType, int page, int size) {
        List<Integer> status = getOrderStatus(showType);
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orderList = orderRepository.findByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(userId, false, 7, status, pageable);
        
        List<JsonOrder> newOrderList = new ArrayList<JsonOrder>();
        for(Order order : orderList) {
        	JsonOrder item = new JsonOrder(order);
        	List<OrderGoods> orderGoods =  orderGoodsRepository.findByOrder(order.getId());
        	item.setGoodsList(orderGoods.stream().map(og -> new JsonOrderGoods(og)).collect(Collectors.toList()));
        	item.setGoodsCount(0);
        	item.getGoodsList().forEach(goodsOrder -> {
        		item.setGoodsCount(item.getGoodsCount() + goodsOrder.getNumber());
        	});
        	item.setAdd_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getAddTime() * 1000L)));
        	item.setOrder_status_text(getOrderStatusText(order));
        	item.setHandleOption(getOrderHandleOption(order));
        	
        	newOrderList.add(item);
        }
        return newOrderList;
	}
	
	public ModelMap detail(Long orderId, Long userId) {
		int currentTime = (int) (new Date().getTime() / 1000);
		
		Optional<Order> orderInfoOpt = orderRepository.findById(orderId);
		if(!orderInfoOpt.isPresent()) {
			throw new ServiceException("订单不存在");
		}
		Order order = orderInfoOpt.get();
		if(order.getUser().getId().longValue() != userId.longValue()) {
        	throw new ServiceException("没有查看权限");
        }
		
		JsonOrder orderInfo = new JsonOrder(order);
		orderInfo.setProvince_name(regionRepository.findById(order.getProvince()).get().getName());
		orderInfo.setCity_name(regionRepository.findById(order.getCity()).get().getName());
		orderInfo.setDistrict_name(regionRepository.findById(order.getDistrict()).get().getName());
		orderInfo.setFull_region(orderInfo.getProvince_name() + orderInfo.getCity_name() + orderInfo.getDistrict_name());
		orderInfo.setPostscript(org.needle.bookingdiscount.utils.Base64Utils.decode(order.getPostscript()));
		
		List<OrderGoods> orderGoods = orderGoodsRepository.findByOrder(order.getId());
        int goodsCount = 0;
        for(OrderGoods gitem : orderGoods) {
            goodsCount += gitem.getNumber();
        }
        
        orderInfo.setOrder_status_text(getOrderStatusText(order));   // 订单状态的处理
        if(order.getConfirmTime() == null || order.getConfirmTime() <= 0) {
        	orderInfo.setConfirm_time("");
        }
        else {
        	orderInfo.setConfirm_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getConfirmTime() * 1000L)));
        }
        
        if(order.getDealdoneTime() == null || order.getDealdoneTime() <= 0) {
        	orderInfo.setDealdone_time("");
        }
        else {
        	orderInfo.setDealdone_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getDealdoneTime() * 1000L)));
        }
        
        if(order.getPayTime() == null || order.getPayTime() <= 0) {
        	orderInfo.setPay_time("");
        }
        else {
        	orderInfo.setPay_time((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getDealdoneTime() * 1000L))));
        }
        
        if(order.getShippingTime() == null || order.getShippingTime() <= 0) {
        	orderInfo.setShipping_time("");
        }
        else {
        	orderInfo.setShipping_time((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getShippingTime() * 1000L))));
        	orderInfo.setConfirm_remainTime(order.getShippingTime() + 10 * 24 * 60 * 60);
        }
        
        if(order.getOrderStatus() == 101 || order.getOrderStatus() == 801) {
        	orderInfo.setFinal_pay_time(order.getAddTime() + 24 * 60 * 60);
        	if(orderInfo.getFinal_pay_time() < currentTime) {
        		//超过时间不支付，更新订单状态为取消
        		order.setOrderStatus(102);
        		orderRepository.save(order);
        	}
        }
        
        orderInfo.setAdd_time((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getAddTime() * 1000L))));
        
        // 订单可操作的选择,删除，支付，收货，评论，退换货
        HandleOption handleOption = getOrderHandleOption(order);
        TextCode textCode = getOrderTextCode(order);
       
        ModelMap model = new ModelMap();
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("orderGoods", orderGoods.stream().map(og -> new JsonOrderGoods(og)).collect(Collectors.toList()));
        model.addAttribute("handleOption", handleOption);
        model.addAttribute("textCode", textCode);
        model.addAttribute("goodsCount", goodsCount);
        return model;
	}
	
	public JsonOrder delete(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId).get();
        if(order.getUser().getId() != userId) {
        	throw new ServiceException("没有删除权限");
        }
        // 检测是否能够取消
        HandleOption handleOption = getOrderHandleOption(order);
        if(!handleOption.delete) {
        	throw new ServiceException("订单不能删除");
        }
        order.setIsDelete(true);
        orderRepository.save(order);
        return new JsonOrder(order);
	}
	
	public JsonOrder cancel(Long orderId, Long userId) {
		Order order = orderRepository.findById(orderId).get();
		if(order.getUser().getId().longValue() != userId.longValue()) {
        	throw new ServiceException("没有取消权限");
        }
        // 检测是否能够取消
        HandleOption handleOption = getOrderHandleOption(order);
        if (!handleOption.cancel) {
        	throw new ServiceException("订单不能取消");
        }
        
        List<OrderGoods> goodsInfo = orderGoodsRepository.findByOrder(orderId);
        //取消订单，还原库存
        for (OrderGoods item : goodsInfo) {
            int number = item.getNumber();
            goodsRepository.increment(number, item.getGoods().getId());
            productRepository.increment(number, item.getProduct().getId());
        }
        
        // 设置订单已取消状态
        order.setOrderStatus(102);
        orderRepository.save(order);
        
        return new JsonOrder(order);
	}
	
	public JsonOrder confirm(Long orderId, Long userId) {
		Order order = orderRepository.findById(orderId).get();
		if(order.getUser().getId().longValue() != userId.longValue()) {
        	throw new ServiceException("没有确认权限");
        }
		// 检测是否能够取消
		HandleOption handleOption = getOrderHandleOption(order);
        if (!handleOption.confirm) {
        	throw new ServiceException("订单不能确认");
        }
        // 设置订单已取消状态
        int currentTime = (int) (new Date().getTime() / 1000);
        order.setOrderStatus(401);
        order.setConfirmTime(currentTime);
        orderRepository.save(order);
        return new JsonOrder(order);
	}
	
	public ModelMap count(int showType, Long userId) {
        List<Integer> status = getOrderStatus(showType);
        long allCount = orderRepository.countByUserAndIsDeleteAndOrderStatus(userId, false, status);
        return new ModelMap().addAttribute("allCount", allCount);
	}
	
	public ModelMap orderCount(Long userId) {
		long toPay = orderRepository.countByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(userId, false, 7, Arrays.asList(101, 801));
        long toDelivery = orderRepository.countByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(userId, false, 7, Arrays.asList(300));
        long toReceive = orderRepository.countByUserAndIsDeleteAndOrderTypeLessThanAndOrderStatusIn(userId, false, 7, Arrays.asList(301));
        
        ModelMap model = new ModelMap();
        model.addAttribute("toPay", toPay);
        model.addAttribute("toDelivery", toDelivery);
        model.addAttribute("toReceive", toReceive);
        return model;
	}
	
	public JsonOrderExpress express(Long orderId) {
		int currentTime = (int) (new Date().getTime() / 1000);
		List<OrderExpress> orderExpressList = orderExpressRepository.findByOrder(orderId);
        if(orderExpressList.isEmpty()) {
        	throw new ServiceException(400, "暂无物流信息");
        }
		
        OrderExpress info = orderExpressList.get(0);
        OrderExpress expressInfo = info;
        // 如果is_finish == 1；或者 updateTime 小于 10分钟，
        int updateTime = info.getUpdateTime();
        int com = (currentTime - updateTime) / 60;
        boolean is_finish = info.getIsFinish() == null ? false : info.getIsFinish();
        
        if(is_finish) {
        	return new JsonOrderExpress(expressInfo);
        } 
        else if (updateTime != 0 && com < 20) {
        	return new JsonOrderExpress(expressInfo);
        } 
        else {
            String shipperCode = expressInfo.getShipperCode();
            String expressNo = expressInfo.getLogisticCode();
            String code = shipperCode.substring(0, 2);
            String shipperName = "";
            if(code == "SF") {
                shipperName = "SFEXPRESS";
                expressNo = expressNo + ':' + sfLastNo;
            } 
            else {
                shipperName = shipperCode;
            }
            
            ExpressInfo lastExpressInfo = getExpressInfo(shipperName, expressNo);
            int deliverystatus = lastExpressInfo.deliverystatus;
            int newUpdateTime = lastExpressInfo.updateTime;
            newUpdateTime = (int) (new Date(newUpdateTime).getTime() / 1000);
            
            String deliverystatusString = getDeliverystatus(deliverystatus);
            Boolean issign = lastExpressInfo.issign;
            List<?> list = lastExpressInfo.list;
            String traces = JsonUtils.toJSON(list);
            
            info.setExpressStatus(deliverystatusString);
            info.setIsFinish(issign);
            info.setTraces(traces);
            info.setUpdateTime(updateTime);
            orderExpressRepository.save(info);
            return new JsonOrderExpress(expressInfo);
        }
	}
	
	private String getDeliverystatus(int status) {
        if (status == 0) {
            return "快递收件(揽件)";
        } 
        else if (status == 1) {
            return "在途中";
        } 
        else if (status == 2) {
            return "正在派件";
        } 
        else if (status == 3) {
            return "已签收";
        } 
        else if (status == 4) {
            return "派送失败(无法联系到收件人或客户要求择日派送，地址不详或手机号不清)";
        } 
        else if (status == 5) {
            return "疑难件(收件人拒绝签收，地址有误或不能送达派送区域，收费等原因无法正常派送)";
        } 
        else if (status == 6) {
            return "退件签收";
        }
        return "未知状态";
    }
	
	class ExpressInfo {
		int deliverystatus;
		int updateTime;
		Boolean issign;
		List<?> list = new ArrayList<Object>();
	}
	
	private ExpressInfo getExpressInfo(String shipperName, String expressNo) {
//        String appCode = "APPCODE " + appcode;
//        String options = {
//            method: 'GET',
//            url: 'http://wuliu.market.alicloudapi.com/kdi?no=' + expressNo + '&type=' + shipperName,
//            headers: {
//                "Content-Type": "application/json; charset=utf-8",
//                "Authorization": appCode
//            }
//        };
//        let sessionData = yield rp(options);
//        sessionData = JSON.parse(sessionData);
//        return sessionData.result;
        return new ExpressInfo();
    }
	
	public List<?> orderGoods(Long orderId, Long userId) {
        if (orderId != null &&  orderId > 0) {
        	List<OrderGoods> orderGoodsList = orderGoodsRepository.findByOrder(orderId);
            List<JsonOrderGoods> newOrderGoodsList = new ArrayList<JsonOrderGoods>();
            for(OrderGoods gitem : orderGoodsList) {
            	JsonOrderGoods jsonOrderGoods = new JsonOrderGoods(gitem);
            	newOrderGoodsList.add(jsonOrderGoods);
            }
            return newOrderGoodsList;
        }
        else {
        	List<Cart> cartList = cartRepository.findByUserAndCheckedAndIsFastAndIsDelete(userId, 1, true, false);
            return cartList.stream().map(c -> new JsonCart(c)).collect(Collectors.toList());
        }
	}
	
	private List<Integer> getOrderStatus(int showType) {
        return Order.getOrderStatus(showType);
    }
	
	private String getOrderStatusText(Order order) {
		return Order.getOrderStatusText(order);
	}
	
	private HandleOption getOrderHandleOption(Order orderInfo) {
		HandleOption handleOption = new HandleOption();
        // 订单流程：下单成功－》支付订单－》发货－》收货－》评论
        // 订单相关状态字段设计，采用单个字段表示全部的订单状态
        // 1xx表示订单取消和删除等状态：  101订单创建成功等待付款、102订单已取消、103订单已取消(自动)
        // 2xx表示订单支付状态：        201订单已付款，等待发货、202订单取消，退款中、203已退款
        // 3xx表示订单物流相关状态：     301订单已发货，302用户确认收货、303系统自动收货
        // 4xx表示订单完成的状态：      401已收货已评价
        // 5xx表示订单退换货相关的状态：  501已收货，退款退货 TODO
        // 如果订单已经取消或是已完成，则可删除和再次购买
        // if (status == 101) "未付款";
        // if (status == 102) "已取消";
        // if (status == 103) "已取消(系统)";
        // if (status == 201) "已付款";
        // if (status == 301) "已发货";
        // if (status == 302) "已收货";
        // if (status == 303) "已收货(系统)";
        //  TODO 设置一个定时器，自动将有些订单设为完成
        // 订单刚创建，可以取消订单，可以继续支付
        if(orderInfo.getOrderStatus() == 101 || orderInfo.getOrderStatus() == 801) {
            handleOption.cancel = true;
            handleOption.pay = true;
        }
        // 如果订单被取消
        if(orderInfo.getOrderStatus() == 102 || orderInfo.getOrderStatus() == 103) {
            handleOption.delete = true;
        }
        // 如果订单已付款，没有发货，则可退款操作
        if(orderInfo.getOrderStatus() == 201) {
        	
        }
        // handleOption.return = true;
        // 如果订单申请退款中，没有相关操作
        if(orderInfo.getOrderStatus() == 202) {
            handleOption.cancel_refund = true;
        }
        if(orderInfo.getOrderStatus() == 300) {}
        // 如果订单已经退款，则可删除
        if(orderInfo.getOrderStatus() == 203) {
            handleOption.delete = true;
        }
        // 如果订单已经发货，没有收货，则可收货操作,
        // 此时不能取消订单
        if(orderInfo.getOrderStatus() == 301) {
            handleOption.confirm = true;
        }
        if(orderInfo.getOrderStatus() == 401) {
            handleOption.delete = true;
        }
        return handleOption;
    }
	
	private TextCode getOrderTextCode(Order orderInfo) {
		TextCode textCode = new TextCode();
        if(orderInfo.getOrderStatus() == 101) {
            textCode.pay = true;
            textCode.countdown = true;
        }
        if(orderInfo.getOrderStatus() == 102 || orderInfo.getOrderStatus() == 103) {
            textCode.close = true;
        }
        if(orderInfo.getOrderStatus() == 201 || orderInfo.getOrderStatus() == 300) {
            textCode.delivery = true;
        }
        if(orderInfo.getOrderStatus() == 301) {
            textCode.receive = true;
        }
        if(orderInfo.getOrderStatus() == 401) {
            textCode.success = true;
        }
        return textCode;
	}
	
	
//	OrderSubmit: ApiRootUrl + 'order/submit', // 提交订单
//    OrderList: ApiRootUrl + 'order/list', //订单列表
//    OrderDetail: ApiRootUrl + 'order/detail', //订单详情
//    OrderDelete: ApiRootUrl + 'order/delete', //订单删除
//    OrderCancel: ApiRootUrl + 'order/cancel', //取消订单
//    OrderConfirm: ApiRootUrl + 'order/confirm', //物流详情
//    OrderCount: ApiRootUrl + 'order/count', // 获取订单数
//    OrderCountInfo: ApiRootUrl + 'order/orderCount', // 我的页面获取订单数状态
//    OrderExpressInfo: ApiRootUrl + 'order/express', //物流信息
//    OrderGoods: ApiRootUrl + 'order/orderGoods', // 获取checkout页面的商品列表
	
}
