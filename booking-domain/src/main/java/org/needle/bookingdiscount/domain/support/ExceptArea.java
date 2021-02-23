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
@Table(name="needle_except_area")
public class ExceptArea extends BaseEntity {
	
	@Column(name="content", length=255)
	private String content;
	
	@Column(name="area", length=3000)
	private String area;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
}
