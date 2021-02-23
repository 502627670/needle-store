package org.needle.bookingdiscount.domain.freight;

import java.math.BigDecimal;

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
@Table(name="needle_freight_template_group")
public class FreightTemplateGroup extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="template_id")
	private FreightTemplate template;
	
	@Column(name="is_default", columnDefinition="tinyint(1)")
	private Boolean isDefault;
	
	@Column(name="area", length=3000)
	private String area;
	
	@Column(name="start", columnDefinition="int(3)")
	private Integer start;
	
	@Column(name="start_fee", columnDefinition="decimal(10,2)")
	private BigDecimal startFee;
	
	@Column(name="add", columnDefinition="int(3)")
	private Integer add;
	
	@Column(name="add_fee", columnDefinition="decimal(10,2)")
	private BigDecimal addFee;
	
	@Column(name="free_by_number", columnDefinition="int(3)")
	private Integer freeByNumber;
	
	@Column(name="free_by_money", columnDefinition="decimal(10,2)")
	private BigDecimal freeByMoney;
	
	@Column(name="is_delete", columnDefinition="tinyint(1)")
	private Boolean isDelete;
	
}
