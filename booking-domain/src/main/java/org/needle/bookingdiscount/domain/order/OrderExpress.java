package org.needle.bookingdiscount.domain.order;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.freight.Shipper;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_order_express")
public class OrderExpress extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="order_id")
	private Order order;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="shipper_id")
	private Shipper shipper;
	
	@Column(name="shipper_name", length=120)
	private String shipperName;
	
	@Column(name="shipper_code", length=60)
	private String shipperCode;
	
	@Column(name="logistic_code", length=40)
	private String logisticCode;
	
	@Column(name="traces", length=2000)
	private String traces;
	
	@Column(name="is_finish", columnDefinition="tinyint(1) default 0")
	private Boolean isFinish;
	
	@Column(name="request_count", columnDefinition="int(11)")
	private Integer requestCount;
	
	@Column(name="request_time", columnDefinition="int(11)")
	private Integer requestTime;
	
	@Column(name="add_time", columnDefinition="int(11)")
	private Integer addTime;
	
	@Column(name="update_time", columnDefinition="int(11)")
	private Integer updateTime;
	
	@Column(name="express_type", columnDefinition="tinyint(1)")
	private Integer expressType;
	
	@Column(name="region_code", length=10)
	private String regionCode;
	
	@Column(name="express_status", length=200)
	private String expressStatus;
	
	@Data
	public static class JsonOrderExpress {
		private Long id;
		private Long order_id;
		private Long shipper_id;
		private String shipper_name;
		private String shipper_code;
		private String logistic_code;
		private String traces;
		private Boolean is_finish;
		private Integer request_count;
		private Integer request_time;
		private Integer add_time;
		private Integer update_time;
		private Integer express_type;
		private String region_code;
		private String express_status;
		
		public JsonOrderExpress(OrderExpress orderExpress) {
			this.id = orderExpress.getId();
			this.order_id = orderExpress.getOrder().getId();
			this.shipper_id = orderExpress.getShipper().getId();
			this.shipper_name = orderExpress.getShipperName();
			this.shipper_code = orderExpress.getShipperCode();
			this.logistic_code = orderExpress.getLogisticCode();
			this.traces = orderExpress.getTraces();
			this.is_finish = orderExpress.getIsFinish();
			this.request_count = orderExpress.getRequestCount();
			this.request_time = orderExpress.getRequestTime();
			this.add_time = orderExpress.getAddTime();
			this.update_time = orderExpress.getUpdateTime();
			this.express_type = orderExpress.getExpressType();
			this.region_code = orderExpress.getRegionCode();
			this.express_status = orderExpress.getExpressStatus();
		}
	}
	
}
