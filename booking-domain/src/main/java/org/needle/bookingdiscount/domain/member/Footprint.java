package org.needle.bookingdiscount.domain.member;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.Goods.JsonGoods;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_footprint")
public class Footprint extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="goods_id")
	private Goods goods;
	
	@Column(name="add_time", columnDefinition="int(11) default 0")
	private Integer addTime;
	
	@Data
	public static class JsonFootprint {
		private Long id;
		private Long user_id;
		private Long goods_id;
		private String add_time;
		
		private JsonGoods goods;
		
		public JsonFootprint(Footprint footprint) {
			this.id = footprint.getId();
			this.user_id = footprint.getUser().getId();
			this.goods_id = footprint.getGoods().getId();
			this.add_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(footprint.getAddTime() * 1000));
			
		}
	}
	
}
