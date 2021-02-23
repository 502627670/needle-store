package org.needle.bookingdiscount.domain.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.product.Goods;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_ad")
public class Ad extends BaseEntity {
	
	@Column(name="link_type", columnDefinition="tinyint(1) default 0")
	private Integer linkType;
	
	@Column(name="link", length=255)
	private String link;
	
	@Column(name="image_url", length=500)
	private String imageUrl;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@Column(name="end_time")
	private Integer endTime;
	
	@Column(name="sort_order", columnDefinition="tinyint(2) default 0")
	private Integer sortOrder;
	
	@Column(name="enabled", columnDefinition="tinyint(1) unsigned default 0")
	private Boolean enabled;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	public Long getGoods_id() {
		return goods == null ? null : goods.getId();
	}
	
	@Getter
	@Setter
	public static class JsonAd {
		private Long id;
		private Integer link_type;
		private String link;
		private String image_url;
		private Long goods_id;
		private Integer end_time;
		private Integer sort_order;
		private Boolean enabled;
		private Boolean is_delete;
		
		public JsonAd(Ad ad) {
			this.id = ad.getId();
			this.link_type = ad.getLinkType();
			this.link = ad.getLink();
			this.image_url = ad.getImageUrl();
			this.goods_id = ad.getGoods_id();
			this.end_time = ad.getEndTime();
			this.sort_order = ad.getSortOrder();
			this.enabled = ad.getEnabled();
			this.is_delete = ad.getIsDelete();
		}
		
	}
}
