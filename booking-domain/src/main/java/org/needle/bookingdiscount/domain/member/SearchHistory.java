package org.needle.bookingdiscount.domain.member;

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
@Table(name="needle_search_history")
public class SearchHistory extends BaseEntity {

	@Column(name="keyword", columnDefinition="char(50)")
	private String keyword;
	
	@Column(name="`from`", columnDefinition="varchar(45)")
	private String from;

	@Column(name="add_time", columnDefinition="int(11)")
	private Integer addTime;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
}
