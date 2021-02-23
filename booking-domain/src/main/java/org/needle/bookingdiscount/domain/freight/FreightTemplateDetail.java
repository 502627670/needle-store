package org.needle.bookingdiscount.domain.freight;

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
@Table(name="needle_freight_template_detail")
public class FreightTemplateDetail extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="template_id")
	private FreightTemplate template;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="group_id")
	private FreightTemplateGroup group;
	
	@Column(name="area")
	private Integer area;

	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
}
