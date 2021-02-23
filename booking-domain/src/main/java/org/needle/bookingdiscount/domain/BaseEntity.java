package org.needle.bookingdiscount.domain;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
//	@CreatedBy
//	@Column(name="created_by", length=30)
//	private String createdBy;
//	
//	@CreatedDate
//	@Column(name="created_date")
//	private Date createdDate;
//	
//	@LastModifiedBy
//	@Column(name="last_modified_by", length=30)
//	private String lastModifiedBy;
//	
//	@LastModifiedDate
//	@Column(name="last_modified_date")
//	private Date lastModifiedDate;
//	
//	@Column(name="group_id")
//	private Long groupId;
	
}
