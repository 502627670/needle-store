package org.needle.bookingdiscount.domain.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_region")
public class Region extends BaseEntity {
	
	@Column(name="name", length=120)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	private Region parent;
	
	@Column(name="type", columnDefinition="int default 2")
	private Integer type;
	
	@Column(name="agency_id", columnDefinition="smallint(5) unsigned default 0")
	private Long agencyId;
	
	@Column(name="area", columnDefinition="smallint(5) unsigned default 0")
	private Integer area;
	
	@Column(name="area_code", length=10)
	private String areaCode;
	
	@Column(name="far_area", columnDefinition="int(2) unsigned default 0")
	private Integer farArea;
	
	@Column(name="is_delete", columnDefinition="tinyint default 0")
	private Boolean isDelete;
	
	@Data
	public static class JsonRegion {
		private Long id;
		private Long parent_id;
		private String name;
		private Integer type;
		private Long agency_id;
		private Integer area;
		private String area_code;
		private Integer far_area;
		
		public JsonRegion(Region region) {
			this.id = region.getId();
			this.parent_id = region.getParent() == null ? null : region.getParent().getId();
			this.name = region.getName();
			this.type = region.getType();
			this.agency_id = region.getAgencyId();
			this.area = region.getArea();
			this.area_code = region.getAreaCode();
			this.far_area = region.getFarArea();
		}
		
	}
	
}
