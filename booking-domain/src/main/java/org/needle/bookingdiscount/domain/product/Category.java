package org.needle.bookingdiscount.domain.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_category")
public class Category extends BaseEntity {
	
	@Column(name="parent_id")
	private Long parentId;
	
	@Column(name="name", length=100)
	private String name;
	
	@Column(name="keywords", length=200)
	private String keywords;
	
	@Column(name="img_url", length=200)
	private String imgUrl;
	
	@Column(name="p_height")
	private Integer pHeight;
	
	@Column(name="icon_url", length=200)
	private String iconUrl;
	
	@Column(name="sort_order")
	private Integer sortOrder;
	
	@Column(name="show_index", columnDefinition="tinyint default 0")
	private Boolean showIndex;
	
	@Column(name="is_show", columnDefinition="tinyint default 1")
	private Boolean isShow;
	
	@Column(name="is_category", columnDefinition="tinyint default 0")
	private Boolean isCategory;
	
	@Column(name="is_channel", columnDefinition="tinyint default 0")
	private Boolean isChannel;
	
	@Column(name="level", length=200)
	private String level;
	
	@Column(name="front_name", length=200)
	private String frontName;
	
	@Column(name="front_desc", length=200)
	private String frontDesc;
	
	@Getter
	@Setter
	public static class JsonCategory {
		private Long id;
		private Long parent_id;
		private String name;
		private String keywords;
		private String img_url;
		private Integer p_height;
		private String icon_url;
		private Integer sort_order;
		private Boolean show_index;
		private Boolean is_show;
		private Boolean is_category;
		private Boolean is_channel;
		private String level;
		private String front_name;
		private String front_desc;
		
		public JsonCategory(Category category) {
			this.id = category.getId();
			this.parent_id = category.getParentId();
			this.name = category.getName();
			this.keywords = category.getKeywords();
			this.img_url = category.getImgUrl();
			this.p_height = category.getPHeight();
			this.icon_url = category.getIconUrl();
			this.sort_order = category.getSortOrder();
			this.show_index = category.getShowIndex();
			this.is_category = category.getIsCategory();
			this.is_channel = category.getIsChannel();
			this.level = category.getLevel();
			this.front_name = category.getFrontName();
			this.front_desc = category.getFrontDesc();
		}
	}
}
