package org.needle.bookingdiscount.domain.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_goods_gallery")
public class GoodsGallery extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@Column(name="img_url", length=200)
	private String imgUrl;
	
	@Column(name="sort_order", columnDefinition="int(11) unsigned default 5")
	private Integer sortOrder;
	
	@Column(name="img_desc", length=200)
	private String imgDesc;
	
	@Column(name="is_delete", columnDefinition="tinyint default 0")
	private Boolean isDelete;
	
	@Getter
	@Setter
	public static class JsonGoodsGallery {
		private Long id;
		private Long goods_id;
		private String img_url;
		private String img_desc;
		private Integer sort_order;
		private Boolean is_delete;
		
		public JsonGoodsGallery(GoodsGallery goodsGallery) {
			this.id = goodsGallery.getId();
			this.goods_id = goodsGallery.getGoods() == null ? null : goodsGallery.getGoods().getId();
			this.img_url = goodsGallery.getImgUrl();
			this.img_desc = goodsGallery.getImgDesc();
			this.sort_order = goodsGallery.getSortOrder();
			this.is_delete = goodsGallery.getIsDelete();
		}
	}
	
}
