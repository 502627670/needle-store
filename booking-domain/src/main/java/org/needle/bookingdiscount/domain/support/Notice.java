package org.needle.bookingdiscount.domain.support;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_notice")
public class Notice extends BaseEntity {
	
	@Column(name="content", length=255)
	private String content;
	
	@Column(name="end_time", columnDefinition="int(11)")
	private Integer endTime;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) default 0")
	private Boolean isDelete;
	
	@Getter
	@Setter
	public static class JsonNotice {
		private Long id;
		private String content;
		private Integer end_time;
		private Boolean is_delete;
		
		public JsonNotice(Notice notice) {
			this.id = notice.getId();
			this.content = notice.getContent();
			this.end_time = notice.getEndTime();
			this.is_delete = notice.getIsDelete();
		}
	}
	
}
