package org.needle.bookingdiscount.domain.freight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.support.Region;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_settings")
public class Settings extends BaseEntity {
	
	@Column(name="autoDelivery", columnDefinition="tinyint(1) NOT NULL DEFAULT '0'")
	private Integer autoDelivery;
	
	@Column(name="name")
	private String name;
	
	@Column(name="tel")
	private String tel;
	
	@Column(name="provinceName")
	private String provinceName;
	
	@Column(name="cityName", length=20)
	private String cityName;
	
	@Column(name="expAreaName", length=20)
	private String expAreaName;
	
	@Column(name="address", length=20)
	private String address;
	
	@Column(name="discovery_img_height")
	private Integer discoveryImgHeight;
	
	@Column(name="discovery_img", length=255)
	private String discoveryImg;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="province_id")
	private Region province;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="city_id")
	private Region city;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="district_id")
	private Region district;
	
	@Column(name="countdown", columnDefinition="int(11) default 0")
	private Integer countdown;
	
	@Column(name="reset", columnDefinition="tinyint(1) default 0")
	private Integer reset;
	
}
