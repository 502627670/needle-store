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
@Table(name="needle_show_settings")
public class ShowSettings extends BaseEntity {
	
	@Column(name="banner", columnDefinition="tinyint(1) unsigned default 0")
	private Integer banner = 0;
	
	@Column(name="channel", columnDefinition="tinyint(1) default 0")
	private Integer channel = 0;
	
	@Column(name="index_banner_img", columnDefinition="tinyint(1) default 0")
	private Integer indexBannerImg = 0;
	
	@Column(name="notice", columnDefinition="tinyint(1)")
	private Integer notice = 0;
	
	@Data
	public static class JsonShowSettings {
		private Long id;
		private Integer banner;
		private Integer channel;
		private Integer index_banner_img;
		private Integer notice;
		
		public JsonShowSettings(ShowSettings showSettings) {
			this.id = showSettings.getId();
			this.banner = showSettings.getBanner();
			this.channel = showSettings.getBanner();
			this.index_banner_img = showSettings.getIndexBannerImg();
			this.notice = showSettings.getNotice();
		}
	}
	
}
