package org.needle.bookingdiscount.domain.product;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_product")
public class Product extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	// 型号/规格
	@Column(name="goods_specification_ids", length=50)
	private String goodsSpecificationIds;
	
	// 商品SKU
	@Column(name="goods_sn", length=60)
	private String goodsSn;
	
	// 快递单上的简称
	@Column(name="goods_name", length=120)
	private String goodsName;
	
	// 等于GoodsSpecification.value
	@Column(name="value", length=50)
	private String value;
	
	// 成本
	@Column(name="cost", columnDefinition="decimal(10,2)")
	private BigDecimal cost;
	
	// 零售
	@Column(name="retail_price", columnDefinition="decimal(10,2) unsigned")
	private BigDecimal retailPrice;
	
	// 重量
	@Column(name="goods_weight", columnDefinition="double(6,2)")
	private Double goodsWeight;
	
	// 库存
	@Column(name="goods_number")
	private Integer goodsNumber;
	
	@Column(name="has_change", columnDefinition="tinyint default 0")
	private Boolean hasChange;
	
	@Column(name="is_on_sale", columnDefinition="tinyint(1) default 1")
	private Boolean isOnSale;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	
	
	@Transient
	private Long goodsId;
	
	@Data
	public static class JsonProduct {
		private Long id;
		private Long goods_id;
		private String goods_specification_ids;
		private String goods_sn;
		private String goods_name;
		private Integer goods_number;
		private BigDecimal retail_price;
		private BigDecimal cost;
		private Double goods_weight;
		private Boolean has_change;
		private Boolean is_on_sale;
		private Boolean is_delete;
		
		public JsonProduct(Product p) {
			this.id = p.getId();
			this.goods_id = p.getGoods() == null ? null : p.getGoods().getId();
			this.goods_specification_ids = p.getGoodsSpecificationIds();
			this.goods_sn = p.getGoodsSn();
			this.goods_name = p.getGoodsName();
			this.goods_number = p.getGoodsNumber();
			this.retail_price = p.getRetailPrice();
			this.cost = p.getCost();
			this.goods_weight = p.getGoodsWeight();
			this.has_change = p.getHasChange();
			this.is_on_sale = p.getIsOnSale();
			this.is_delete = p.getIsDelete();
		}
	}
}
