package org.needle.bookingdiscount.domain.product;

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
@Table(name="needle_goods_specification")
public class GoodsSpecification extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specification_id")
	private Specification specification;
	
	@Column(name="value", length=50)
	private String value;
	
	@Column(name="pic_url", length=200)
	private String picUrl;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	@Transient
	private int goodsNumber;
	
	@Data
	public static class JsonGoodsSpecification {
		private Long id;
		private Long goods_id;
		private Long specification_id;
		private String value;
		private String pic_url;
		private Boolean is_delete;
		private int goods_number;
		
		public JsonGoodsSpecification(GoodsSpecification goodsSpecification) {
			this.id = goodsSpecification.getId();
			this.goods_id = goodsSpecification.getGoods() == null ? null : goodsSpecification.getGoods().getId();
			
			try {
				Specification specification = goodsSpecification.getSpecification();
				if(specification != null) {
					this.specification_id = specification.getId();
				}
			}
			catch(Exception e) {}
			
			this.value = goodsSpecification.getValue();
			this.pic_url = goodsSpecification.getPicUrl();
			this.is_delete = goodsSpecification.getIsDelete();
			this.goods_number = goodsSpecification.getGoodsNumber();
		}
	}
	
}
