package org.needle.bookingdiscount.server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.cart.Cart;
import org.needle.bookingdiscount.domain.cart.Cart.JsonCart;
import org.needle.bookingdiscount.domain.freight.FreightTemplate;
import org.needle.bookingdiscount.domain.freight.FreightTemplateDetail;
import org.needle.bookingdiscount.domain.freight.FreightTemplateGroup;
import org.needle.bookingdiscount.domain.member.Address;
import org.needle.bookingdiscount.domain.member.Address.JsonAddress;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.cart.CartRepository;
import org.needle.bookingdiscount.server.repository.freight.FreightTemplateDetailRepository;
import org.needle.bookingdiscount.server.repository.freight.FreightTemplateGroupRepository;
import org.needle.bookingdiscount.server.repository.freight.FreightTemplateRepository;
import org.needle.bookingdiscount.server.repository.member.AddressRepository;
import org.needle.bookingdiscount.server.repository.order.OrderGoodsRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsRepository;
import org.needle.bookingdiscount.server.repository.product.GoodsSpecificationRepository;
import org.needle.bookingdiscount.server.repository.product.ProductRepository;
import org.needle.bookingdiscount.server.repository.support.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class CartService {
	
	@Autowired
	private CartRepository cartRepository;
		
	@Autowired
	private GoodsRepository goodsRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private GoodsSpecificationRepository goodsSpecificationRepository;
	
	@Autowired
	private OrderGoodsRepository orderGoodsRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private FreightTemplateRepository freightTemplateRepository;
	
	@Autowired
	private FreightTemplateDetailRepository freightTemplateDetailRepository;
	
	@Autowired
	private RegionRepository regionRepository;
	
	@Autowired
	private FreightTemplateGroupRepository freightTemplateGroupRepository;
		
	private ModelMap getCart(Long userId, int type) {
		List<Cart> cartList = new ArrayList<Cart>();
        if(type == 0) {
        	cartList = cartRepository.findUserCarts(userId, false, false);
        }
        else {
        	cartList = cartRepository.findUserCarts(userId, true, false);
        }
        
        // 获取购物车统计信息
        int goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0);
        int checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0);
        int numberChange = 0;
        
        for(Cart cartItem : cartList) {
        	Product product = cartItem.getProduct();
        	cartItem.setGoodsNumber(product.getGoodsNumber());   // 设置产品库存
        	if(Boolean.TRUE.equals(product.getIsDelete())) {
        		Cart cart = cartRepository.getByUserAndProduct(userId, product.getId());
        		cart.setIsDelete(true);
        		cartRepository.save(cart);
        	}
        	else {
        		BigDecimal retailPrice = product.getRetailPrice();
        		int productNum = product.getGoodsNumber();
                if(productNum <= 0 || Boolean.FALSE.equals(product.getIsOnSale())) {
                	Cart cart = cartRepository.getByUserAndProductAndChecked(userId, product.getId(), 1);
                	if(cart != null) {
                		cart.setChecked(0);
                		cartRepository.save(cart);
                	}
                	cartItem.setNumber(0);
                } 
                else if(productNum > 0 && productNum < cartItem.getNumber()) {
                    cartItem.setNumber(productNum);
                    numberChange = 1;
                } 
                else if(productNum > 0 && cartItem.getNumber() == 0) {
                    cartItem.setNumber(1);
                    numberChange = 1;
                }
                goodsCount += cartItem.getNumber();
                goodsAmount = goodsAmount.add(retailPrice.multiply(new BigDecimal(cartItem.getNumber())));
                cartItem.setRetailPrice(retailPrice);
                
                if(cartItem.getChecked() > 0 && productNum > 0) {
                	checkedGoodsCount += cartItem.getNumber();
                	checkedGoodsAmount = checkedGoodsAmount.add(retailPrice.multiply(new BigDecimal(cartItem.getNumber())));
                }
                
                Goods goods = cartItem.getGoods();
                cartItem.setListPicUrl(goods.getListPicUrl());
                cartItem.setWeightCount(cartItem.getNumber() * cartItem.getGoodsWeight());
                
                
                cartItem.setAddPrice(retailPrice);
                cartRepository.save(cartItem);
                
//                Cart cart = cartRepository.getByUserAndProduct(userId, cartItem.getProduct().getId());
//                if(cart != null) {
//                	cart.setNumber(cartItem.getNumber());
//                	cart.setAddPrice(retailPrice);
//                	cartRepository.save(cart);
//                }
            }
        }
        
        Map<String,Object> cartTotalMap = new HashMap<String,Object>();
        cartTotalMap.put("goodsCount", goodsCount);
        cartTotalMap.put("goodsAmount", goodsAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        cartTotalMap.put("checkedGoodsCount", checkedGoodsCount);
        cartTotalMap.put("checkedGoodsAmount", checkedGoodsAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        cartTotalMap.put("user_id", userId);
        cartTotalMap.put("numberChange", numberChange);
        
        ModelMap model = new ModelMap();
        model.addAttribute("cartList", cartList.stream().map(c -> new JsonCart(c)).collect(Collectors.toList()));
        model.addAttribute("cartTotal", cartTotalMap);
        return model;
	}
	
	public ModelMap getIndexData(Long userId) {
        return getCart(userId, 0);
	}
	
	// 0：正常加入购物车，1:立即购买，2:再来一单
	public ModelMap add(Long userId, Long goodsId, Long productId, int number, int addType) {
        int currentTime = new Long(new Date().getTime() / 1000).intValue();
        
        Optional<Goods> goodsInfoOpt = goodsRepository.findById(goodsId);
        if(!goodsInfoOpt.isPresent() || Boolean.FALSE.equals(goodsInfoOpt.get().getIsOnSale())) {
        	throw new ServiceException("400", "商品已下架");
        }
        
        Goods goodsInfo = goodsInfoOpt.get();
        // 取得规格的信息,判断规格库存
        Optional<Product> productInfoOpt = productRepository.findById(productId);
        if(!productInfoOpt.isPresent() || productInfoOpt.get().getGoodsNumber() < number) {
        	throw new ServiceException("400", "库存不足");
        }
        
        MemberUser user = new MemberUser(userId);
        Product productInfo = productInfoOpt.get();
        // 判断购物车中是否存在此规格商品
        BigDecimal retailPrice = productInfo.getRetailPrice();
        if (addType == 1) {
        	cartRepository.findByUserAndIsDelete(userId, false).forEach(cart -> {
        		cart.setChecked(0);
        		cartRepository.save(cart);
        	});
        	
            List<String> goodsSepcifitionValue = new ArrayList<String>();
            String goodsSpecificationIds = productInfo.getGoodsSpecificationIds();
            if(StringUtils.hasText(goodsSpecificationIds)) {
            	List<Long> ids = new ArrayList<Long>();
            	String[] stringIds = goodsSpecificationIds.split("_");
            	for(int i = 0; i < stringIds.length; i++) {
            		String stringId = stringIds[i];
            		if(StringUtils.hasText(stringId)) {
            			ids.add(Long.valueOf(stringId));
            		}
            	}
            	List<GoodsSpecification> gsList = goodsSpecificationRepository.findByGoodsAndIsDeleteAndIdIn(productInfo.getGoods().getId(), false, ids);
            	goodsSepcifitionValue = gsList.stream().map(gs -> gs.getValue()).collect(Collectors.toList());
            }
            
            // 添加到购物车
            Cart cart = new Cart();
            cart.setGoods(productInfo.getGoods());
            cart.setProduct(productInfo);
            cart.setGoodsSn(productInfo.getGoodsSn());
            cart.setGoodsName(goodsInfo.getName());
            cart.setGoodsAka(productInfo.getGoodsName());
            cart.setGoodsWeight(productInfo.getGoodsWeight());
            cart.setFreightTemplateId(goodsInfo.getFreightTemplateId());
            cart.setListPicUrl(goodsInfo.getListPicUrl());
            cart.setNumber(number);
            cart.setUser(user);
            cart.setRetailPrice(retailPrice);
            cart.setAddPrice(retailPrice);
            cart.setGoodsSpecificationNameValue(goodsSepcifitionValue.stream().collect(Collectors.joining(";")));
            cart.setGoodsSpecificationIds(productInfo.getGoodsSpecificationIds());
            cart.setChecked(1);
            cart.setAddTime(currentTime);
            cart.setIsFast(true);
            cartRepository.save(cart);
            
            return this.getCart(userId, 1);
        } 
        else {
        	Cart cartInfo = cartRepository.getByUserAndProduct(userId, productId);
        	if(cartInfo == null || Boolean.TRUE.equals(cartInfo.getIsDelete())) {
                // 添加操作
                // 添加规格名和值
                List<String> goodsSepcifitionValue = new ArrayList<String>();
                String goodsSpecificationIds = productInfo.getGoodsSpecificationIds();
                if(StringUtils.hasText(goodsSpecificationIds)) {
                	List<Long> ids = new ArrayList<Long>();
                	String[] stringIds = goodsSpecificationIds.split("_");
                	for(int i = 0; i < stringIds.length; i++) {
                		String stringId = stringIds[i];
                		if(StringUtils.hasText(stringId)) {
                			ids.add(Long.valueOf(stringId));
                		}
                	}
                	List<GoodsSpecification> gsList = goodsSpecificationRepository.findByGoodsAndIsDeleteAndIdIn(productInfo.getGoods().getId(), false, ids);
                	goodsSepcifitionValue = gsList.stream().map(gs -> gs.getValue()).collect(Collectors.toList());
                }
                
                // 添加到购物车
                Cart cart = new Cart();
                cart.setGoods(productInfo.getGoods());
                cart.setProduct(productInfo);
                cart.setGoodsSn(productInfo.getGoodsSn());
                cart.setGoodsName(goodsInfo.getName());
                cart.setGoodsAka(productInfo.getGoodsName());
                cart.setGoodsWeight(productInfo.getGoodsWeight());
                cart.setFreightTemplateId(goodsInfo.getFreightTemplateId());
                cart.setListPicUrl(goodsInfo.getListPicUrl());
                cart.setNumber(number);
                cart.setUser(user);
                cart.setRetailPrice(retailPrice);
                cart.setAddPrice(retailPrice);
                cart.setGoodsSpecificationNameValue(goodsSepcifitionValue.stream().collect(Collectors.joining(";")));
                cart.setGoodsSpecificationIds(productInfo.getGoodsSpecificationIds());
                cart.setChecked(1);
                cart.setAddTime(currentTime);
                cart.setIsDelete(false);
                cartRepository.save(cart);
            }
        	else {
                // 如果已经存在购物车中，则数量增加
                if(productInfo.getGoodsNumber() < (number + cartInfo.getNumber())) {
                	throw new ServiceException("400", "库存不足");
                }
                cartInfo.setRetailPrice(retailPrice);
                cartInfo.setNumber(cartInfo.getNumber() + 1);
                cartRepository.save(cartInfo);
            }
            return this.getCart(userId, 0);
        }
	}
	
	public ModelMap update(Long userId, Long productId, Long id, int number) {
        // 取得规格的信息,判断规格库存
		Optional<Product> productInfoOpt = productRepository.findById(productId);
		
		if(!productInfoOpt.isPresent() || Boolean.TRUE.equals(productInfoOpt.get().getIsDelete()) || productInfoOpt.get().getGoodsNumber() < number) {
			throw new ServiceException("400", "库存不足");
		}
		
		Cart cartInfo = cartRepository.findById(id).get();
		if(cartInfo.getProduct().getId() == productId) {
			cartInfo.setNumber(number);
			cartRepository.save(cartInfo);
		}
		
		return getCart(userId, 0);
	}
	
	public ModelMap delete(Long userId, Long productIds) {
		Long productId = productIds;
        if(productId == null) {
        	throw new ServiceException("删除出错");
        }
        
        Cart cart = cartRepository.getByUserAndProduct(userId, productId);
        if(cart != null) {
        	cart.setIsDelete(true);
        	cartRepository.save(cart);
        }
        
        return this.getCart(userId, 0);
	}
	
	public ModelMap check(Long userId, String productIds, Boolean isChecked) {
		if(!StringUtils.hasText(productIds)) {
        	throw new ServiceException("删除出错");
        }
		String productId = productIds.toString();
        String[] productIdArray = productId.split(",");
        List<Long> ids = new ArrayList<Long>();
        for(int i = 0; i < productIdArray.length; i++) {
        	if(StringUtils.hasText(productIdArray[i])) {
        		ids.add(Long.valueOf(productIdArray[i]));
        	}
        }
        cartRepository.findByUserAndProductIn(userId, ids).forEach(cart -> {
        	cart.setChecked(Boolean.TRUE.equals(isChecked) ? 1 : 0);
        	cartRepository.save(cart);
        });
        return this.getCart(userId, 0);
    }
	
	@SuppressWarnings("unchecked")
	public ModelMap goodsCount(Long userId) {
		ModelMap cartData = this.getCart(userId, 0);
		
		List<Cart> cartList = cartRepository.findByUserAndIsDeleteAndIsFast(userId, false, true);
		cartList.forEach(cart -> {
			cart.setIsDelete(true);
			cartRepository.save(cart);
		});
		Map<String,Object> cartTotal = (Map<String, Object>) cartData.get("cartTotal");
		
        ModelMap model = new ModelMap();
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("goodsCount", cartTotal.get("goodsCount"));
        model.addAttribute("cartTotal", dataMap);
        return model;
	}
	
	public void addAgain(Long userId, Long goodsId, Long productId, int number) {
        int currentTime = new Long(new Date().getTime() / 1000).intValue();
        
        Optional<Goods> goodsInfoOpt = goodsRepository.findById(goodsId);
        if(!goodsInfoOpt.isPresent() || Boolean.FALSE.equals(goodsInfoOpt.get().getIsOnSale())) {
        	throw new ServiceException("400", "商品已下架");
        }
        // 取得规格的信息,判断规格库存
        
        Optional<Product> productInfoOpt = productRepository.findById(productId);
        if (!productInfoOpt.isPresent() || productInfoOpt.get().getGoodsNumber() < number) {
            throw new ServiceException("400", "库存不足");
        }
        
        MemberUser user = new MemberUser();
        user.setId(userId);
        
        Product productInfo = productInfoOpt.get();
        // 判断购物车中是否存在此规格商品
        Cart cartInfo = cartRepository.getByUserAndProduct(userId, productId);
        BigDecimal retailPrice = productInfo.getRetailPrice();
        if(cartInfo == null) {
            // 添加操作
            // 添加规格名和值
            List<String> goodsSepcifitionValue = new ArrayList<String>();
            String goodsSpecificationIds = productInfo.getGoodsSpecificationIds();
            if(StringUtils.hasText(goodsSpecificationIds)) {
            	List<Long> ids = new ArrayList<Long>();
                String[] sids = goodsSpecificationIds.split("_");
                for(int i = 0; i < sids.length; i++) {
                	if(StringUtils.hasText(sids[i])) {
                		ids.add(Long.valueOf(sids[i]));
                	}
                }
                List<GoodsSpecification> gsList = goodsSpecificationRepository.findByGoodsAndIsDeleteAndIdIn(productInfo.getGoods().getId(), false, ids);
                goodsSepcifitionValue = gsList.stream().map(gs -> gs.getValue()).collect(Collectors.toList());
            }
            
            Goods goodsInfo = productInfo.getGoods();
            // 添加到购物车
            Cart cart = new Cart();
            cart.setGoods(productInfo.getGoods());
            cart.setProduct(productInfo);
            cart.setGoodsSn(productInfo.getGoodsSn());
            cart.setGoodsName(goodsInfo.getName());
            cart.setGoodsAka(productInfo.getGoodsName());
            cart.setGoodsWeight(productInfo.getGoodsWeight());
            cart.setFreightTemplateId(goodsInfo.getFreightTemplateId());
            cart.setListPicUrl(goodsInfo.getListPicUrl());
            cart.setNumber(number);
            cart.setUser(user);
            cart.setRetailPrice(retailPrice);
            cart.setAddPrice(retailPrice);
            cart.setGoodsSpecificationNameValue(goodsSepcifitionValue.stream().collect(Collectors.joining(";")));
            cart.setGoodsSpecificationIds(productInfo.getGoodsSpecificationIds());
            cart.setChecked(1);
            cart.setAddTime(currentTime);
            cart.setIsDelete(false);
            cartRepository.save(cart);
        } 
        else {
            // 如果已经存在购物车中，则数量增加
            if(productInfo.getGoodsNumber() < (number + cartInfo.getNumber())) {
            	throw new ServiceException("400", "库存不足");
            }
            
            cartInfo.setRetailPrice(retailPrice);
            cartInfo.setChecked(1);
            cartInfo.setNumber(number);
            cartRepository.save(cartInfo);
        }
    }
	
	private ModelMap getAgainCart(Long userId, String orderFrom) {
		cartRepository.findByUserAndIsDelete(userId, false).forEach(cart -> {
			cart.setChecked(0);
			cartRepository.save(cart);
		});
		
		List<OrderGoods> againGoods = orderGoodsRepository.findByOrder(Long.valueOf(orderFrom));
		againGoods.forEach(item -> {
			this.addAgain(userId, item.getGoods().getId(), item.getProduct().getId(), item.getNumber());
		});
		
        // 获取购物车统计信息
        int goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0);
        int checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0);
        
        List<Cart> cartList = cartRepository.findByUserAndIsDeleteAndIsFast(userId, false, false);
        for(Cart cartItem : cartList) {
        	goodsCount += cartItem.getNumber();
            goodsAmount = goodsAmount.add(new BigDecimal(cartItem.getNumber()).multiply(cartItem.getRetailPrice()));
            
            if(cartItem.getChecked() > 0) {
            	checkedGoodsCount += cartItem.getNumber();
            	checkedGoodsAmount = checkedGoodsAmount.add(cartItem.getRetailPrice().multiply(new BigDecimal(cartItem.getNumber())));
            }
            
            // 查找商品的图片
            Goods goodsInfo = cartItem.getGoods();
            int num = goodsInfo.getGoodsNumber();
            if (num <= 0) {
            	Cart cart = cartRepository.getByUserAndProductAndChecked(userId, cartItem.getProduct().getId(), 1);
            	if(cart != null) {
            		cart.setChecked(0);
            		cartRepository.save(cart);
            	}
            }
            cartItem.setListPicUrl(goodsInfo.getListPicUrl());
            cartItem.setGoodsNumber(goodsInfo.getGoodsNumber());
            cartItem.setWeightCount(cartItem.getNumber() * cartItem.getGoodsWeight());
        }
        
        Map<String,Object> cartTotal = new LinkedHashMap<String,Object>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        cartTotal.put("user_id", userId);
        
        ModelMap model = new ModelMap();
        model.addAttribute("cartList", cartList);
        model.addAttribute("cartTotal", cartTotal);
        return model;
    }
	
	@SuppressWarnings("unchecked")
	public ModelMap checkout(Long userId, String orderFrom, int type, String addressId, int addType) {
        int goodsCount = 0;                          // 购物车的数量
        BigDecimal goodsMoney = new BigDecimal(0);   // 购物车的总价
        BigDecimal freightPrice = new BigDecimal(0);
        int outStock = 0;
        ModelMap cartData = new ModelMap();
        
        // 获取要购买的商品
        if(type == 0) {
            if (addType == 0) {
                cartData = this.getCart(userId, 0);
            } 
            else if (addType == 1) {
                cartData = this.getCart(userId, 1);
            } 
            else if (addType == 2) {
                cartData = this.getAgainCart(userId, orderFrom);
            }
        }
        
        List<JsonCart> cartList = (List<JsonCart>) cartData.get("cartList");
        List<JsonCart> checkedGoodsList = cartList.stream().filter(cart -> cart.getChecked() == 1).collect(Collectors.toList());
        
        for(JsonCart item : checkedGoodsList) {
            goodsCount = goodsCount + item.getNumber();
            goodsMoney = goodsMoney.add(new BigDecimal(item.getNumber()).multiply(item.getRetail_price()));
            
            if(item.getGoods_number() <= 0 || Boolean.FALSE.equals(item.getIs_on_sale())) {
                outStock = outStock + 1;
            }
        }
        
        if(addType == 2) {
        	List<OrderGoods> againGoods = orderGoodsRepository.findByOrder(Long.valueOf(orderFrom));
            int againGoodsCount = 0;
            for(OrderGoods item : againGoods) {
                againGoodsCount = againGoodsCount + item.getNumber();
            }
            if(goodsCount != againGoodsCount) {
                outStock = 1;
            }
        }
        
        // 选择的收货地址
        List<Address> checkedAddressList = new ArrayList<Address>();
        if(!StringUtils.hasText(addressId) || "0".equals(addressId)) {
        	checkedAddressList = addressRepository.findByUserAndIsDefaultAndIsDelete(userId, true, false);
        } 
        else {
        	Optional<Address> addressOpt = addressRepository.findById(Long.valueOf(addressId));
        	if(addressOpt.isPresent() && Boolean.FALSE.equals(addressOpt.get().getIsDelete())) {
        		checkedAddressList.add(addressOpt.get());
        	}
        }
        
        Address checkedAddress = null;
        if(checkedAddressList.size() > 0) {
        	checkedAddress = checkedAddressList.get(0);
            // 运费开始
            // 先将促销规则中符合满件包邮或者满金额包邮的规则找到；
            // 先看看是不是属于偏远地区。
            Long provinceId = checkedAddress.getProvinceId();
            // 得到数组了，然后去判断这两个商品符不符合要求
            // 先用这个goods数组去遍历
            List<JsonCart> cartGoods = checkedGoodsList;
            
            List<FreightTemplate> freightTempArray = freightTemplateRepository.findByIsDelete(false);
            List<Map<String,Object>> freightData = new ArrayList<Map<String,Object>>();
            
            for(FreightTemplate item : freightTempArray) {
            	Map<String,Object> dataMap = new LinkedHashMap<String,Object>();
            	dataMap.put("id", item.getId());
            	dataMap.put("number", 0);
            	dataMap.put("money", new BigDecimal(0));
            	dataMap.put("goods_weight", 0);
            	dataMap.put("freight_type", item.getFreightType());
            	freightData.add(dataMap);
            }
            
            // 按件计算和按重量计算的区别是：按件，只要算goods_number就可以了，按重量要goods_number*goods_weight
            for(Map<String,Object> item : freightData) {
                for(JsonCart cartItem : cartGoods) {
                    if(item.get("id") == cartItem.getFreight_template_id()) {
                        // 这个在判断，购物车中的商品是否属于这个运费模版，如果是，则加一，但是，这里要先判断下，这个商品是否符合满件包邮或满金额包邮，如果是包邮的，那么要去掉
                    	item.put("number", cartItem.getNumber() + (int) item.get("number"));
                    	item.put("money", cartItem.getRetail_price().multiply(new BigDecimal(cartItem.getNumber())));
                    	double goodsWeight = Double.valueOf(item.get("goods_weight").toString());
                    	item.put("goods_weight", goodsWeight + cartItem.getNumber() * cartItem.getGoods_weight());
                    }
                }
            }
            
            checkedAddress.setProvinceName(regionRepository.findById(checkedAddress.getProvinceId()).get().getName());
            checkedAddress.setCityName(regionRepository.findById(checkedAddress.getCityId()).get().getName());
            checkedAddress.setDistrictName(regionRepository.findById(checkedAddress.getDistrictId()).get().getName());
            checkedAddress.setFullRegion(checkedAddress.getProvinceName() + checkedAddress.getCityName() + checkedAddress.getDistrictName());
            
            for(Map<String,Object> item : freightData) {
            	int number = (int) item.get("number");
            	double goods_weight = Double.valueOf(item.containsKey("goods_weight") ? item.get("goods_weight").toString() : "0");
            	BigDecimal money = (BigDecimal) item.get("money");
            	
                if(number == 0) {
                    continue;
                }
                
                List<FreightTemplateDetail> exList = freightTemplateDetailRepository.findByTemplateAndArea(new Long(item.get("id").toString()), provinceId.intValue());
                BigDecimal freight_price = new BigDecimal(0);
                
                if(!exList.isEmpty()) {
                	// console.log('第一层：非默认邮费算法');
                	FreightTemplateDetail ex = exList.get(0);
                	FreightTemplateGroup groupData = ex.getGroup();
                	
                    // 不为空，说明有模板，那么应用模板，先去判断是否符合指定的包邮条件，不满足，那么根据type 是按件还是按重量
                	int free_by_number = groupData.getFreeByNumber();
                	BigDecimal free_by_money = groupData.getFreeByMoney();
                	
                    // 4种情况，1、free_by_number > 0  2,free_by_money > 0  3,free_by_number free_by_money > 0,4都等于0
                	
                	FreightTemplate templateInfo = freightTemplateRepository.findById(new Long(item.get("id").toString())).get();
                    int freight_type = templateInfo.getFreightType();
                    if (freight_type == 0) {
                        if (number > groupData.getStart()) { // 说明大于首件了
                        	freight_price =  new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee())
                            		.add(new BigDecimal(number - 1).multiply(groupData.getAddFee())); // todo 如果续件是2怎么办？？？
                        } 
                        else {
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee());
                        }
                    } 
                    else if (freight_type == 1) {
                        if (goods_weight > groupData.getStart()) {  // 说明大于首件了
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee())
                                	.add(new BigDecimal(goods_weight - 1).multiply(groupData.getAddFee())); // todo 如果续件是2怎么办？？？
                        } 
                        else {
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee());
                        }
                    }
                    if(free_by_number > 0) {
                        if (number >= free_by_number) {
                        	freight_price = new BigDecimal(0);
                        }
                    }
                    if(free_by_money.doubleValue() > 0.01) {
                        if(money.doubleValue() >= free_by_money.doubleValue()) {
                        	freight_price = new BigDecimal(0);
                        }
                    }
                } 
                else {
                	Long templateId = (Long) item.get("id");
                	
                	// console.log('第二层：使用默认的邮费算法');
                	List<FreightTemplateGroup> groupDataList = freightTemplateGroupRepository.findByTemplateAndArea(templateId, "0");
                	
                	FreightTemplateGroup groupData = groupDataList.get(0);
                    int free_by_number = groupData.getFreeByNumber();
                    BigDecimal free_by_money = groupData.getFreeByMoney();
                    
                    FreightTemplate templateInfo = freightTemplateRepository.findById(templateId).get();
                    int freight_type = templateInfo.getFreightType();
                    if (freight_type == 0) {
                        if (number > groupData.getStart()) { // 说明大于首件了
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee())
                                	.add(new BigDecimal(number - 1).multiply(groupData.getAddFee())); // todo 如果续件是2怎么办？？？
                        } 
                        else {
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee());
                        }
                    } 
                    else if(freight_type == 1) {
                        if (goods_weight > groupData.getStart()) { // 说明大于首件了
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee())
                                 	.add(new BigDecimal(goods_weight - 1).multiply(groupData.getAddFee())); // todo 如果续件是2怎么办？？？
                        } 
                        else {
                        	freight_price = new BigDecimal(groupData.getStart()).multiply(groupData.getStartFee());
                        }
                    }
                    if(free_by_number > 0) {
                        if (number >= free_by_number) {
                        	freight_price = new BigDecimal(0);
                        }
                    }
                    if(free_by_money.doubleValue() > 0.01) {
                        if (money.doubleValue() >= free_by_money.doubleValue()) {
                        	freight_price = new BigDecimal(0);
                        }
                    }
                }
				freightPrice = freightPrice.doubleValue() > freight_price.doubleValue() ? freightPrice : freight_price;
                // 会得到 几个数组，然后用省id去遍历在哪个数组
            }
        }
        else {
            checkedAddress = null;
        }
        
        Map<String,Object> cartTotal = (Map<String, Object>) cartData.get("cartTotal");
        // 计算订单的费用
        BigDecimal goodsTotalPrice = (BigDecimal) cartTotal.get("checkedGoodsAmount");
        
        // 获取是否有可用红包
        BigDecimal money = (BigDecimal) cartTotal.get("checkedGoodsAmount");
        BigDecimal orderTotalPrice = new BigDecimal(0.0);
        
        orderTotalPrice = money.add(freightPrice);  // 订单的总价
        BigDecimal actualPrice = orderTotalPrice;   // 减去其它支付的金额后，要实际支付的金额
        int numberChange = (int) cartTotal.get("numberChange");
        
        ModelMap model = new ModelMap();
        model.addAttribute("checkedAddress", checkedAddress == null ? 0 : new JsonAddress(checkedAddress));
        model.addAttribute("freightPrice", freightPrice);
        model.addAttribute("checkedGoodsList", checkedGoodsList);
        model.addAttribute("goodsTotalPrice", goodsTotalPrice);
        model.addAttribute("orderTotalPrice", orderTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        model.addAttribute("actualPrice", actualPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        model.addAttribute("goodsCount", goodsCount);
        model.addAttribute("outStock", outStock);
        model.addAttribute("numberChange", numberChange);
        return model;
    }
    
	
}
