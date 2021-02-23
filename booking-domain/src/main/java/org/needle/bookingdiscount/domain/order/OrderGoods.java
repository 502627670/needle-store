package org.needle.bookingdiscount.domain.order;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Product;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_order_goods")
public class OrderGoods extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="order_id")
	private Order order;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@Column(name="goods_name", length=120)
	private String goodsName;
	
	@Column(name="goods_aka", length=120)
	private String goodsAka;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;
	
	@Column(name="number", columnDefinition="smallint(5) unsigned")
	private Integer number;
	
	@Column(name="retail_price")
	private BigDecimal retailPrice;
	
	@Column(name="goods_specifition_name_value", columnDefinition="TEXT")
	private String goodsSpecificationNameValue;
	
	@Column(name="goods_specifition_ids", length=255)
	private String goodsSpecificationIds;
	
	@Column(name="list_pic_url", length=255)
	private String listPicUrl;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	@Data
	public static class JsonOrderGoods {
		private Long id;
		private Long order_id;
		private Long goods_id;
		private String goods_name;
		private String goods_aka;
		private Long product_id;
		private Integer number;
		private BigDecimal retail_price;
		private String goods_specifition_name_value;
		private String goods_specifition_ids;
		private String list_pic_url;
		private Long user_id;
		private Boolean is_delete;
		
		private String specificationName;
		
		public JsonOrderGoods(OrderGoods orderGoods) {
			this.id = orderGoods.getId();
			this.order_id = orderGoods.getOrder().getId();
			this.goods_id = orderGoods.getGoods().getId();
			this.goods_name = orderGoods.getGoodsName();
			this.goods_aka = orderGoods.getGoodsAka();
			this.product_id = orderGoods.getProduct().getId();
			this.number = orderGoods.getNumber();
			this.retail_price = orderGoods.getRetailPrice();
			this.goods_specifition_name_value = orderGoods.getGoodsSpecificationNameValue();
			this.goods_specifition_ids = orderGoods.getGoodsSpecificationIds();
			this.list_pic_url = orderGoods.getListPicUrl();
			this.user_id = orderGoods.getUser().getId();
			this.is_delete = orderGoods.getIsDelete();
		}
		
		
	}
	
}
