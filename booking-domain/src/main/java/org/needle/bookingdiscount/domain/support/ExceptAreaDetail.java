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
@Table(name="needle_except_area_detail")
public class ExceptAreaDetail extends BaseEntity {
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="except_area_id")
	private ExceptArea exceptArea;
	
	@Column(name="area", columnDefinition="int(5) default 0")
	private Integer area;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
}
