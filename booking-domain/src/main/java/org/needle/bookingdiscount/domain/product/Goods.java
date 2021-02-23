package org.needle.bookingdiscount.domain.product;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.freight.FreightTemplate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_goods")
public class Goods extends BaseEntity {
	
	// 分类
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="category_id")
	private Category category;
	
	@Column(name="https_pic_url", length=200)
	private String httpsPicUrl;
	
	@Column(name="list_pic_url", length=200)
	private String listPicUrl;
	
	@Column(name="name", length=120)
	private String name;
	
	@Column(name="goods_brief", length=200)
	private String goodsBrief;
	
	@Column(name="keywords", length=200)
	private String keywords;
	
	@Column(name="goods_number")
	private Integer goodsNumber;
	
	@Column(name="goods_unit", length=45)
	private String goodsUnit;
	
	@Column(name="sell_volume")
	private Integer sellVolume;
	
	@Column(name="is_index", columnDefinition="tinyint default 0")
	private Boolean isIndex;
	
	@Column(name="is_new", columnDefinition="tinyint default 0")
	private Boolean isNew;
	
	@Column(name="retail_price", length=100)
	private String retailPrice;
	
	@Column(name="min_retail_price", columnDefinition="decimal(10,2)")
	private BigDecimal minRetailPrice;
	
	@Column(name="cost_price", length=100)
	private String costPrice;
	
	@Column(name="min_cost_price")
	private BigDecimal minCostPrice;
	
	@Column(name="freight_template_id")
	private Long freightTemplateId;
	
	@Column(name="freight_type", columnDefinition="tinyint default 0")
	private Boolean freightType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="freight_template")
	private FreightTemplate freightTemplate;
	
	@Column(name="sort_order")
	private Integer sortOrder;
	
	@Column(name="is_on_sale", columnDefinition="tinyint default 1")
	private Boolean isOnSale;
	
	@Column(name="is_delete", columnDefinition="tinyint default 0")
	private Boolean isDelete;
	
	@Column(name="goods_desc", columnDefinition="text")
	private String goodsDesc;
	
	// 型号和价格
	@Column(name="specification_id1")
	private Long specificationId1;
	
	public Long getFreightTemplateId() {
		if(this.freightTemplateId != null) {
			return this.freightTemplateId;
		}
		return this.freightTemplate == null ? null : this.freightTemplate.getId();
	}
	
	@Getter
	@Setter
	public static class JsonGoods {
		private Long id;
		private Long category_id;
		private String https_pic_url;
		private String list_pic_url;
		private String name;
		private String goods_brief;
		private String keywords;
		private Integer goods_number;
		private String goods_unit;
		private Integer sell_volume;
		private Boolean is_index;
		private Boolean is_new;
		private String retail_price;
		private BigDecimal min_retail_price;
		private String cost_price;
		private BigDecimal min_cost_price;
		private Long freight_template_id;
		private Boolean freight_type;
		private Integer sort_order;
		private Boolean is_on_sale;
		private Boolean is_delete;
		private String goods_desc;
		
		public JsonGoods(Goods goods) {
			this.id = goods.getId();
			this.category_id = goods.getCategory() == null ? null : goods.getCategory().getId();
			this.https_pic_url = goods.getHttpsPicUrl();
			this.list_pic_url = goods.getListPicUrl();
			this.name = goods.getName();
			this.goods_brief = goods.getGoodsBrief();
			this.keywords = goods.getKeywords();
			this.goods_number = goods.getGoodsNumber();
			this.goods_unit = goods.getGoodsUnit();
			this.sell_volume = goods.getSellVolume();
			this.is_index = goods.getIsIndex();
			this.is_new = goods.getIsNew();
			this.retail_price = goods.getRetailPrice();
			this.min_retail_price = goods.getMinRetailPrice();
			this.cost_price = goods.getCostPrice();
			this.min_cost_price = goods.getMinCostPrice();
			this.freight_template_id = goods.getFreightTemplateId();
			this.freight_type = goods.getFreightType();
			this.sort_order = goods.getSortOrder();
			this.is_on_sale = goods.getIsOnSale();
			this.is_delete = goods.getIsDelete();
			this.goods_desc = goods.getGoodsDesc();
		}
		
	}
	
}
