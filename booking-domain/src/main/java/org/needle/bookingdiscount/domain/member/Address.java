package org.needle.bookingdiscount.domain.member;

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
@Table(name="needle_address")
public class Address extends BaseEntity {
	
	@Column(name="name", length=50)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
	@Column(name="country_id")
	private Long countryId;
	
	@Column(name="province_id")
	private Long provinceId;
	
	@Column(name="city_id")
	private Long cityId;
	
	@Column(name="district_id")
	private Long districtId;
	
	@Column(name="address", length=120)
	private String address;
	
	@Column(name="mobile", length=60)
	private String mobile;
	
	@Column(name="is_default", columnDefinition="tinyint(1) unsigned default 0")
	private Boolean isDefault;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	@Transient
	private String provinceName;
	
	@Transient
	private String cityName;
	
	@Transient
	private String districtName;
	
	@Transient
	private String fullRegion;
	
	
	@Data
	public static class JsonAddress {
		private Long id;
		private String name;
		private Long user_id;
		private Long country_id;
		private Long province_id;
		private Long city_id;
		private Long district_id;
		private String address;
		private String mobile;
		private Boolean is_default;
		private Boolean is_delete;
		private String province_name;
		private String city_name;
		private String district_name;
		private String full_region;
		
		public JsonAddress() {}
		
		public JsonAddress(Address address) {
			this.id = address.getId();
			this.name = address.getName();
			this.user_id = address.getUser() == null ? null : address.getUser().getId();
			this.country_id = address.getCountryId();
			this.province_id = address.getProvinceId();
			this.city_id = address.getCityId();
			this.district_id = address.getDistrictId();
			this.address = address.getAddress();
			this.mobile = address.getMobile();
			this.is_default = address.getIsDefault();
			this.is_delete = address.getIsDelete();
			this.province_name = address.getProvinceName();
			this.city_name = address.getCityName();
			this.district_name = address.getDistrictName();
			this.full_region = address.getFullRegion();
		}
	}
}
