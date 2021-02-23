package org.needle.bookingdiscount.domain.freight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_shipper")
public class Shipper extends BaseEntity {
		
	@Column(name="name", length=20)
	private String name;
	
	@Column(name="code", length=10)
	private String code;
	
	@Column(name="sort_order")
	private Integer sortOrder;
	
	@Column(name="monthCode")
	private String monthCode;
	
	@Column(name="customerName", length=100)
	private String customerName;
	
	@Column(name="enabled", columnDefinition="tinyint(1) default 0")
	private Boolean enabled;
	
}
