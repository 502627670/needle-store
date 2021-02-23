package org.needle.bookingdiscount.domain.cart;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Product;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.Getter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_cart")
public class Cart extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@Column(name="goods_sn", length=60)
	private String goodsSn;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id")
	private Product product;
	
	@Column(name="goods_name", length=120)
	private String goodsName;
	
	@Column(name="goods_aka", length=120)
	private String goodsAka;
	
	@Column(name="goods_weight")
	private Double goodsWeight;
	
	@Column(name="add_price", columnDefinition="decimal(10,2)")
	private BigDecimal addPrice;
	
	@Column(name="retail_price", columnDefinition="decimal(10,2)")
	private BigDecimal retailPrice;
	
	@Column(name="number", columnDefinition="smallint(5) unsigned default 0")
	private Integer number;
	
	@Column(name="goods_specifition_name_value", columnDefinition="TEXT")
	private String goodsSpecificationNameValue;
	
	@Column(name="goods_specifition_ids", length=60)
	private String goodsSpecificationIds;
	
	@Column(name="checked", columnDefinition="tinyint(1) unsigned")
	private Integer checked;
	
	@Column(name="list_pic_url", length=255)
	private String listPicUrl;
	
	@Column(name="freight_template_id")
	private Long freightTemplateId;
	
	@Column(name="is_on_sale", columnDefinition="tinyint(1) default 1")
	private Boolean isOnSale = Boolean.TRUE;
	
	@Column(name="add_time", columnDefinition="int(11) default 0")
	private Integer addTime;
	
	@Column(name="is_fast", columnDefinition="tinyint(1) default 0")
	private Boolean isFast = Boolean.FALSE;
	
	@Column(name="is_delete", columnDefinition="tinyint(2) unsigned")
	private Boolean isDelete = Boolean.FALSE;
	
	@Transient
	private Double weightCount;
	
	@Transient
	private int goodsNumber;
	
	@Getter
	@Setter
	public static class JsonCart {
		private Long id;
		private Long user_id;
		private Long goods_id;
		private String goods_sn;
		private Long product_id;
		private String goods_name;
		private String goods_aka;
		private Double goods_weight;
		private BigDecimal add_price;
		private BigDecimal retail_price;
		private Integer number;
		private String goods_specifition_name_value;
		private String goods_specifition_ids;
		private Integer checked;
		private String list_pic_url;
		private Long freight_template_id;
		private Boolean is_on_sale;
		private Integer add_time;
		private Boolean is_fast;
		private Boolean is_delete;
		private Double weight_count;
		private int goods_number;
		
		public JsonCart(Cart cart) {
			this.id = cart.getId();
			this.user_id = cart.getUser() == null ? null : cart.getUser().getId();
			this.goods_id = cart.getGoods() == null ? null :cart.getGoods().getId();
			this.goods_sn = cart.getGoodsSn();
			this.product_id = cart.getProduct() == null ? null : cart.getProduct().getId();
			this.goods_name = cart.getGoodsName();
			this.goods_aka = cart.getGoodsAka();
			this.goods_weight = cart.getGoodsWeight();
			this.add_price = cart.getAddPrice();
			this.retail_price = cart.getRetailPrice();
			this.number = cart.getNumber();
			this.goods_specifition_name_value = cart.getGoodsSpecificationNameValue();
			this.goods_specifition_ids = cart.getGoodsSpecificationIds();
			this.checked = cart.getChecked();
			this.list_pic_url = cart.getListPicUrl();
			this.freight_template_id = cart.getFreightTemplateId();
			this.is_on_sale = cart.getIsOnSale();
			this.add_time = cart.getAddTime();
			this.is_fast = cart.getIsFast();
			this.is_delete = cart.getIsDelete();
			this.weight_count = cart.getWeightCount();
			this.goods_number = cart.getGoodsNumber();
		}
	}
	
}

