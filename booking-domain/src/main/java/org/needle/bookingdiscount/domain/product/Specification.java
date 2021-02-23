package org.needle.bookingdiscount.domain.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_specification")
public class Specification extends BaseEntity {
	
	@Column(name="name", length=60)
	private String name;
	
	@Column(name="sort_order", columnDefinition="tinyint(3) unsigned default 0")
	private Integer sortOrder;
	
	@Column(name="memo", length=200)
	private String memo;
	
	public Specification() {}
	
	public Specification(Long id) {
		this.id = id;
	}
	
}
