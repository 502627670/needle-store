package org.needle.bookingdiscount.domain.order;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.order.OrderGoods.JsonOrderGoods;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_order")
public class Order extends BaseEntity {

	@Column(name="order_sn" ,length=20)
	private String orderSn;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	private MemberUser user;
	
	// 300：待发货；301：待收货；401：已收货；102：已关闭；101：待付款；
	@Column(name="order_status", columnDefinition="smallint(4) unsigned default 0")
	private Integer orderStatus;
	
	@Column(name="offline_pay", columnDefinition="tinyint(1) unsigned")
	private Integer offlinePay;
	
	@Column(name="shipping_status", columnDefinition="tinyint(1) unsigned")
	private Integer shippingStatus;
	
	@Column(name="print_status", columnDefinition="tinyint(1) unsigned")
	private Integer printStatus;
	
	@Column(name="pay_status", columnDefinition="tinyint(1)")
	private Integer payStatus;
	
	// 收货人
	@Column(name="consignee", length=60)
	private String consignee;
	
	@Column(name="country", columnDefinition="smallint(5) unsigned")
	private Long country;
	
	@Column(name="province", columnDefinition="smallint(5) unsigned")
	private Long province;
	
	@Column(name="city", columnDefinition="smallint(5) unsigned")
	private Long city;
	
	@Column(name="district", columnDefinition="smallint(5) unsigned")
	private Long district;
	
	@Column(name="address", length=255)
	private String address;
	
	@Column(name="print_info", length=255)
	private String printInfo;
	
	@Column(name="mobile", length=60)
	private String mobile;
	
	// 留言
	@Column(name="postscript", length=255)
	private String postscript;
	
	@Column(name="admin_memo", length=255)
	private String adminMemo;
	
	@Column(name="shipping_fee", columnDefinition="decimal(10,2)")
	private BigDecimal shippingFee;
	
	@Column(name="pay_name", length=120)
	private String payName;
	
	@Column(name="pay_id", length=255)
	private String payId;
	
	@Column(name="change_price", columnDefinition="decimal(10,2) unsigned")
	private BigDecimal changePrice;
	
	@Column(name="order_price", columnDefinition="decimal(10,2)")
	private BigDecimal orderPrice;
	
	@Column(name="goods_price", columnDefinition="decimal(10,2)")
	private BigDecimal goodsPrice;
	
	@Column(name="actual_price", columnDefinition="decimal(10,2) unsigned")
	private BigDecimal actualPrice;
	
	@Column(name="add_time", columnDefinition="int(11) unsigned")
	private Integer addTime;
	
	@Column(name="pay_time", columnDefinition="int(11) unsigned")
	private Integer payTime;
	
	@Column(name="shipping_time", columnDefinition="int(11) unsigned")
	private Integer shippingTime;
	
	@Column(name="confirm_time", columnDefinition="int(11) unsigned")
	private Integer confirmTime;
	
	@Column(name="dealdone_time", columnDefinition="int(11) unsigned default 0")
	private Integer dealdoneTime;
	
	@Column(name="freight_price", columnDefinition="int(10) unsigned default 0")
	private Integer freightPrice;
	
	@Column(name="express_value", columnDefinition="decimal(10,2)")
	private BigDecimal expressValue;
	
	@Column(name="remark", length=255)
	private String remark;
	
	@Column(name="order_type", columnDefinition="tinyint(2) unsigned default 0")
	private Integer orderType;
	
	@Column(name="is_delete", columnDefinition="tinyint(1) unsigned default 0")
	private Boolean isDelete;
	
	public static List<Integer> getOrderStatus(int showType) {
        List<Integer> status = new ArrayList<Integer>();
        if (showType == 0) {
        	status = Arrays.asList(101, 102, 103, 201, 202, 203, 300, 301, 302, 303, 401);
        } 
        else if (showType == 1) {  // 待付款订单
            status = Arrays.asList(101, 801);
        } 
        else if (showType == 2) {  // 待发货订单
            status = Arrays.asList(300);
        } 
        else if (showType == 3) {  // 待收货订单
            status = Arrays.asList(301);
        } 
        else if (showType == 4) {  // 待评价订单
            status = Arrays.asList(302, 303);
        } 
        return status;
    }
	
	public static String getOrderStatusText(Order order) {
        return getOrderStatusText(order.getOrderStatus());
	}
	
	public static String getOrderStatusText(int orderStatus) {
        String statusText = "待付款";
        switch(orderStatus) {
            case 101:
                statusText = "待付款";
                break;
            case 102:
                statusText = "交易关闭";
                break;
            case 103:
                statusText = "交易关闭"; //到时间系统自动取消
                break;
            case 201:
                statusText = "待发货";
                break;
            case 300:
                statusText = "待发货";
                break;
            case 301:
                statusText = "已发货";
                break;
            case 401:
                statusText = "交易成功"; //到时间，未收货的系统自动收货、
                break;
        }
        return statusText;
	}
	
	
	@Getter
	@Setter
	public static class JsonOrder {
		private Long id;
		private String order_sn;
		private Long user_id;
		private Integer order_status;
		private Integer offline_pay;
		private Integer shipping_status;
		private Integer print_status;
		private Integer pay_status;
		private String consignee;
		private Long country;
		private Long province;
		private Long city;
		private Long district;
		private String address;
		private String print_info;
		private String mobile;
		private String postscript;
		private String admin_memo;
		private BigDecimal shipping_fee;
		private String pay_name;
		private String pay_id;
		private BigDecimal change_price;
		private BigDecimal order_price;
		private BigDecimal goods_price;
		private BigDecimal actual_price;
		private Integer freight_price;
		private BigDecimal express_value;
		private String remark;
		private Integer order_type;
		private Boolean is_delete;
		
		private String add_time;
		private String pay_time;
		private String confirm_time;
		private String shipping_time;
		private String dealdone_time;
		private int confirm_remainTime;
		private int final_pay_time;
		
		private int goodsCount = 0;
		private List<JsonOrderGoods> goodsList = new ArrayList<JsonOrderGoods>();
		private String order_status_text;
		private HandleOption handleOption;
		
		private String province_name;
		private String city_name;
		private String district_name;
		private String full_region;
		
		private String userNickname;
		private String username;
		private String userMobile;
		private String userAvatar;
		
		private List<JsonOrderGoods> orderGoods = new ArrayList<JsonOrderGoods>();
		
		public JsonOrder(Order order) {
			this.id = order.getId();
			this.order_sn = order.getOrderSn();
			this.user_id = order.getUser().getId();
			this.offline_pay = order.getOfflinePay();
			this.shipping_fee = order.getShippingFee();
			this.print_status = order.getPrintStatus();
			this.pay_status = order.getPayStatus();
			this.consignee = order.getConsignee();
			this.country = order.getCountry();
			this.province = order.getProvince();
			this.city = order.getCity();
			this.district = order.getDistrict();
			this.address = order.getAddress();
			this.print_info = order.getPrintInfo();
			this.mobile = order.getMobile();
			this.postscript = order.getPostscript();
			this.admin_memo = order.getAdminMemo();
			this.shipping_fee = order.getShippingFee();
			this.pay_name = order.getPayName();
			this.pay_id = order.getPayId();
			this.change_price = order.getChangePrice();
			this.order_price = order.getOrderPrice();
			this.goods_price = order.getGoodsPrice();
			this.actual_price = order.getActualPrice();
			this.add_time = order.getAddTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getAddTime() * 1000));
			this.pay_time = order.getPayTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getPayTime() * 1000));
			this.shipping_time = order.getShippingTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getShippingTime() * 1000));
			this.confirm_time = order.getConfirmTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getConfirmTime() * 1000));
			this.dealdone_time = order.getDealdoneTime() == null ? null : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(order.getDealdoneTime() * 1000));
			this.freight_price = order.getFreightPrice();
			this.express_value = order.getExpressValue();
			this.remark = order.getRemark();
			this.order_type = order.getOrderType();
			this.is_delete = order.getIsDelete();
			
			this.userNickname = order.getUser().getNickname();
			this.username = order.getUser().getUsername();
			this.userMobile = order.getUser().getMobile();
			this.userAvatar = order.getUser().getAvatar();
		}
	}
	
	public static class HandleOption {
		public boolean cancel = false;         // 取消操作
		public boolean delete = false;         // 删除操作
		public boolean pay = false;            // 支付操作
		public boolean confirm = false;        // 确认收货完成订单操作
		public boolean cancel_refund = false;
	}
	
	public static class TextCode {
		public boolean pay = false;
		public boolean close = false;
		public boolean delivery = false;
		public boolean receive = false;
		public boolean success = false;
		public boolean countdown = false;
	}
}
