package org.needle.bookingdiscount.domain.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_keywords")
public class Keywords extends BaseEntity {
	
	@Column(name="keyword", length=90)
	private String keyword;
	
	@Column(name="is_hot", columnDefinition="tinyint(1) unsigned default 0")
	private Boolean isHot;
	
	@Column(name="is_default", columnDefinition="tinyint(1) unsigned default 0")
	private Boolean isDefault;
	
	@Column(name="is_show", columnDefinition="tinyint(1) unsigned default 1")
	private Boolean isShow;
	
	@Column(name="sort_order")
	private Integer sortOrder;
	
	@Column(name="scheme_url", length=255)
	private String scheme_url;
	
	@Column(name="type", columnDefinition="int(11) unsigned default 0")
	private Integer type;
	
	@Data
	public static class JsonKeywords {
		private Long id;
		private String keyword;
		private Boolean is_hot;
		private Boolean is_default;
		private Boolean is_show;
		private Integer sort_order;
		private String scheme_url;
		private Integer type;
		public JsonKeywords(Keywords keywords) {
			this.id = keywords.getId();
			this.keyword = keywords.getKeyword();
			this.is_hot = keywords.getIsHot();
			this.is_default = keywords.getIsDefault();
			this.is_show = keywords.getIsShow();
			this.sort_order = keywords.getSortOrder();
			this.scheme_url = keywords.getScheme_url();
			this.type = keywords.getType();
		}
	}
	
}
