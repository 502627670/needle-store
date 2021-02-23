package org.needle.bookingdiscount.domain.freight;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_freight_template")
public class FreightTemplate extends BaseEntity {
	
	@Column(name="name", length=120)
	private String name;
	
	@Column(name="package_price", columnDefinition="decimal(5,2) unsigned default 0.00")
	private BigDecimal packagePrice;
	
	// 按件0；按重1；
	@Column(name="freight_type", columnDefinition="tinyint(1)")
	private Integer freightType;
	
	@Column(name="is_delete", columnDefinition="tinyint(1)")
	private Boolean isDelete;
	
}
