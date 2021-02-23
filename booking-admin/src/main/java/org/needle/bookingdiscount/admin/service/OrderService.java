package org.needle.bookingdiscount.admin.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.admin.repository.GoodsSpecificationRepository;
import org.needle.bookingdiscount.admin.repository.OrderGoodsRepository;
import org.needle.bookingdiscount.admin.repository.OrderRepository;
import org.needle.bookingdiscount.domain.order.Order;
import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.needle.bookingdiscount.domain.order.OrderGoods.JsonOrderGoods;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private OrderGoodsRepository orderGoodsRepository;
	
	@Autowired
	private GoodsSpecificationRepository goodsSpecificationRepository;
	
	public Order getById(Long orderId) {
		return orderRepository.findById(orderId).get();
	}
	
	public GoodsSpecification getGoodsSpecificationById(Long goodsSpecificationId) {
		Optional<GoodsSpecification> gsOpt = goodsSpecificationRepository.findById(goodsSpecificationId);
		return gsOpt.isPresent() ? gsOpt.get() : null;
	}
	
	public List<JsonOrderGoods> findGoodsByOrder(Long orderId) {
		List<OrderGoods> orderGoods = orderGoodsRepository.findByOrder(orderId);
		return orderGoods.stream().map(og -> new JsonOrderGoods(og)).collect(Collectors.toList());
	}
	
	
	
}
