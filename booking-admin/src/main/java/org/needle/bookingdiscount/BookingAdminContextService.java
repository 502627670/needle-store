package org.needle.bookingdiscount;

import java.util.Date;

import org.needle.bookingdiscount.domain.cart.Cart;
import org.needle.bookingdiscount.domain.freight.FreightTemplate;
import org.needle.bookingdiscount.domain.freight.FreightTemplateDetail;
import org.needle.bookingdiscount.domain.freight.FreightTemplateGroup;
import org.needle.bookingdiscount.domain.freight.Settings;
import org.needle.bookingdiscount.domain.freight.Shipper;
import org.needle.bookingdiscount.domain.member.Address;
import org.needle.bookingdiscount.domain.member.Footprint;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.domain.member.SearchHistory;
import org.needle.bookingdiscount.domain.order.Formid;
import org.needle.bookingdiscount.domain.order.Order;
import org.needle.bookingdiscount.domain.order.OrderExpress;
import org.needle.bookingdiscount.domain.order.OrderGoods;
import org.needle.bookingdiscount.domain.product.Category;
import org.needle.bookingdiscount.domain.product.Goods;
import org.needle.bookingdiscount.domain.product.GoodsGallery;
import org.needle.bookingdiscount.domain.product.GoodsSpecification;
import org.needle.bookingdiscount.domain.product.Product;
import org.needle.bookingdiscount.domain.product.Specification;
import org.needle.bookingdiscount.domain.support.Ad;
import org.needle.bookingdiscount.domain.support.ExceptArea;
import org.needle.bookingdiscount.domain.support.ExceptAreaDetail;
import org.needle.bookingdiscount.domain.support.Keywords;
import org.needle.bookingdiscount.domain.support.Notice;
import org.needle.bookingdiscount.domain.support.Region;
import org.needle.bookingdiscount.domain.support.ShowSettings;
import org.needleframe.AbstractContextService;
import org.needleframe.context.ModuleFactory;
import org.needleframe.context.ModuleFactory.MenuFactory;
import org.needleframe.core.model.ModuleProp.Decoder;
import org.needleframe.core.model.ModuleProp.Encoder;
import org.needleframe.core.model.ModuleProp.Feature;
import org.needleframe.core.model.ViewFilter.Op;
import org.needleframe.utils.Base64Utils;
import org.needleframe.utils.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class BookingAdminContextService extends AbstractContextService {
	
	@Override
	protected void defModules(ModuleFactory mf) {
		mf.build(Cart.class).addCRUD()
			.filters("goodsName").op(Op.RLIKE)
			.showList("user.id", "user.nickName", "goods.id", "listPicUrl", "goodsName", 
					"goodsSpecificationNameValue", "number", "retailPrice", "addTime", "isDelete")
			.fk("user").show("nickName").end()
			.fk("goods").show("name").end()
			.prop("listPicUrl").absoluteImage().end()
			.prop("isDelete").feature(Feature.SELECT).values(new Object[]{1, 0}).end()
			.prop("addTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("isDelete").values(new Object[] {"true", "false"})
				.encoder(v -> "true".equals(v) ? 1 : 0)
				.decoder(v -> v == null ? v : Integer.valueOf(v.toString()) == 1 ? "true" : "false").end()
			.prop("product").hide().end()
			.prop("goodsAka").hide().end()
			.prop("goodsWeight").hide().end()
			.prop("addPrice").hide().end()
			.prop("goodsSpecificationIds").hide().end()
			.prop("checked").hide().end()
			.prop("freightTemplateId").hide().end()
			.prop("isOnSale").hide().end()
			.prop("goodsSn").hide().end()
			.prop("isFast").hide().end()
			.prop("weightCount").hide()
			.prop("goodsNumber").hide().end()
			
			.addProp("user.id", Long.class).hide().end()
			.addProp("goods.id", Long.class).hide().end();
		
		mf.build(FreightTemplate.class)
			.filters("name").op(Op.RLIKE)
			.prop("name").required().end()
			.prop("packagePrice").required().end()
			.prop("freightType")
				.values(new Object[] {"按件", "按重"}).encoder(new FreightTypeConverter()).decoder(new FreightTypeConverter()).end()
			.prop("isDelete").encoder(new BooleanConverter()).decoder(new BooleanConverter()).end();
		
		mf.build(FreightTemplateDetail.class);
		mf.build(FreightTemplateGroup.class);
		
		mf.build(Order.class).addCRUD()
			.filters("orderSn").op(Op.RLIKE)
			.filters("user").eq()
			.filters("consignee").op(Op.RLIKE)
			.deletedProp("isDelete", true)
			.fk("user").ref("id").show("nickname").end()
			.prop("orderStatus")
				.values(new Object[] {"待付款", "已关闭", "待发货", "待收货", "已收货"})
				.encoder(new OrderStatusConvert()).decoder(new OrderStatusConvert()).end()
			.prop("orderSn").rule().noUpdate().end()
			.prop("addTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).rule().noUpdate().end()
			.prop("payTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).rule().noUpdate().end()
			.prop("shippingTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("confirmTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("dealdoneTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("changePrice").pattern("0.00").end()
			.prop("actualPrice").end()
			.prop("orderPrice").end()
			.prop("goodsPrice").end()
			.prop("freightPrice").end()
			.prop("remark").inRow().end()
			.prop("adminMemo").inRow().end()
			.prop("postscript")
				.encoder(v -> Base64Utils.encode(v.toString()))
				.decoder(v -> Base64Utils.decode(v.toString())).end()
			.prop("country").hide().end()
			.prop("province").hide().end()
			.prop("city").hide().end()
			.prop("district").hide().end()
			.prop("address").hide().end()
			.prop("printInfo").hide().end()
			.prop("postscript").hide().end()
			.prop("postscript").hide().end()
			.prop("shippingFee").hide().end()
			.prop("payName").hide().end()
			.prop("payId").hide().end()
			.prop("expressValue").hide().end()
			.prop("orderType").hide().end()
			.prop("offlinePay").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("shippingStatus").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("printStatus").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("payStatus").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
		
		.addChild(OrderGoods.class).addCRUD()
			.fk("order").show("orderSn").hideList().end()
			.fk("goods").show("name").hide().end()
			.fk("user").show("nickname").hide().end()
			.prop("goodsAka").hide().end()
			.prop("product").hide().end()
			.prop("goodsSpecificationIds").hide().end()
			.prop("listPicUrl").absoluteImage().end()
			.endChild()
			
		.addChild(OrderExpress.class).addCRUD()
			.fk("order").show("orderSn").hideList().end()
			.fk("shipper").show("name").hide().end()
			.prop("shipperName").end()
			.prop("logisticCode").end()
			.prop("traces").end()
			.prop("isFinish").end()
			.prop("requestTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("addTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("updateTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("expressType").end()
			.endChild();
		
		mf.build(MemberUser.class)
			.filters("nickname").op(Op.RLIKE)
			.showList("avatar", "nickname", "gender", "registerTime", "lastLoginTime")
			.prop("avatar").absoluteImage().end()
			.prop("nickname")
				.encoder(v -> Base64Utils.encode(v.toString()))
				.decoder(v -> Base64Utils.decode(v.toString()))
				.end()
			.prop("gender").values(new Object[] {"女", "男"}).encoder(v -> "女".equals(v) ? 2 : 1).decoder(v -> ((int) v) == 2 ? "女" : "男").end()
			.prop("registerTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("lastLoginTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("birthday").date().encoder(new DateEncoder("yyyy-MM-dd")).decoder(new DateDecoder("yyyy-MM-dd")).end()
			.prop("nameMobile").hide().end()
			.prop("token").hide().end()
			
		.addChild(Cart.class).addCRUD()
			.fk("user").show("nickname").end()
			.fk("goods").show("name").end()
			.fk("product").show("goodsName").end()
			.endChild()
		.addChild(Order.class).addCRUD()
			.fk("user").show("nickname").end()
			.endChild()
		.addChild(Address.class).addCRUD()
			.showList("name", "mobile", "address")
			.fk("user").show("nickname").end()
			.endChild()
		.addChild(Footprint.class).addCRUD()
			.showList("goods.id", "goods.listPicUrl", "goods.name", "addTime")
			.fk("user").show("nickname").end()
			.fk("goods").show("name").end()
			.addProp("goods.id", Long.class).end()
			.addProp("goods.listPicUrl", String.class).absoluteImage().end()
			.addProp("goods.name", String.class).end()
			.endChild();
		
		mf.build(SearchHistory.class);
		
		mf.build(Formid.class);
		
		mf.build(Settings.class)
			.fk("province").map("name", "provinceName").show("name").hide().showForm().showEdit().end()
			.fk("city").map("name", "cityName").show("name").hide().showForm().showEdit().end()
			.fk("district").map("name", "expAreaName").show("name").hide().showForm().showEdit().end()
			.fk("goods").show("name").hide().end()
			.prop("name").required().end()
			.prop("tel").required().end()
			.prop("autoDelivery").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("discoveryImg").absoluteImage().hide().end()
			.prop("countdown").hide()
			.prop("reset").hide()
			.prop("provinceName").show().hideForm().hideEdit().end()
			.prop("cityName").show().hideForm().hideEdit().end()
			.prop("expAreaName").show().hideForm().hideEdit().end();
			
		mf.build(Shipper.class)
			.filters("name", "code").op(Op.RLIKE)
			.prop("name").required().end()
			.prop("code").required().end()
			.prop("enabled").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end();
		
		mf.build(Category.class).addCRUD()
			.showList("iconUrl", "name", "isChannel", "isShow", "isCategory", "sortOrder")
			.filters("name").op(Op.RLIKE)
			.prop("iconUrl").absoluteImage().inRow().sortOrder(1).required().end()
			.prop("name").required().sortOrder(2)
			.prop("isChannel").defaultValue("true").required().sortOrder(3).end()
			.prop("keywords").sortOrder(4)
			.prop("isShow").defaultValue("true").sortOrder(5)
			.prop("isCategory").defaultValue("true").sortOrder(6)
			.prop("pHeight").sortOrder(7)
			.prop("sortOrder").sortOrder(8)
			.prop("imgUrl").absoluteImage().inRow().sortOrder(9).end()
			.prop("parentId").hide()
			.prop("showIndex").hide()
			.prop("level").hide()
			.prop("frontName").hide()
			.prop("frontDesc").hide();
			
		mf.build(Goods.class).addCRUD()
			.showList("listPicUrl", "name", "category", "sortOrder", "sellVolume", "retailPrice", "goodsNumber", "isIndex", "isOnSale")
			.filters("name").op(Op.RLIKE)
			.filters("category").eq()
			.filters("retailPrice").ops(Op.LARGE_EQUAL, Op.LESS_EQUAL)
			.filters("goodsNumber").ops(Op.LARGE_EQUAL, Op.LESS_EQUAL)
			.deletedProp("isDelete", Boolean.TRUE)
			.fk("category").show("name").end()
			.fk("freightTemplate").show("name").end()
			.prop("goodsNumber").feature(Feature.NUMBER).end()
			.prop("sellVolume").feature(Feature.NUMBER).end()
			.prop("goodsBrief").inRow().end()
			.prop("httpsPicUrl").absoluteImage().inRow().hide().end()
			.prop("listPicUrl").absoluteImage().inRow().end()
			.prop("freightTemplate").fk().ref("id").map("id", "freightTemplateId").show("name").end()
			.prop("freightType").values(new Object[] {"按件", "按重"}).encoder(new FreightTypeConverter()).decoder(new FreightTypeConverter()).end()
			.prop("goodsDesc").editor().end()
			.prop("freightTemplateId").hide().end()
			.prop("specificationId1").hide().end()
			.prop("isIndex").values(new Object[] {true, false}).defaultValue(true)
			.prop("keywords").hide().end()
			
		.addChild(GoodsGallery.class).addCRUD()
		.showList("goods", "imgUrl", "imgDesc", "sortOrder", "isDelete")
		.deletedProp("isDelete", true)
		.prop("imgUrl").absoluteImage().inRow().end()
		.endChild()
		
		.addChild(GoodsSpecification.class).addCRUD()
		.fk("goods").show("name").end()
		.fk("specification").show("name").end()
		.prop("picUrl").absoluteImage().end()
		.endChild()
		
		.addChild(Product.class).addCRUD()
		.fk("goods").show("name").show().hideList().end()
		.deletedProp("isDelete", true)
		.prop("goodsSpecificationIds").hide().end()
		.prop("goodsSn").end()
		.prop("goodsName").hide().showList().end()
		.prop("goodsNumber").end()
		.prop("retailPrice").end()
		.prop("cost").end()
		.prop("goodsWeight").end()
		.prop("hasChange").end()
		.prop("isOnSale").end()
		.prop("isDelete").end()
		.endChild();
		
		mf.build(Specification.class)
			.prop("name").required();
		
		mf.build(Product.class);
		
		mf.build(Ad.class)
			.filters("endTime").ops(Op.LARGE_EQUAL, Op.LESS_EQUAL)
			.fk("goods").show("name").end()
			.prop("linkType").hide().end()
			.prop("link").hide().end()
			.prop("imageUrl").absoluteImage().showList().end()
			.prop("endTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("enabled").values(new Object[] {"true", "false"})
			.prop("isDelete").values(new Object[] {"true", "false"})
				.encoder(new BooleanConverter())
				.decoder(new BooleanConverter()).end();
		
		mf.build(ExceptArea.class);
		mf.build(ExceptAreaDetail.class);
		mf.build(Keywords.class);
		
		mf.build(Notice.class)
			.filters("content").op(Op.RLIKE)
			.filters("endTime").ops(Op.LARGE_EQUAL, Op.LESS_EQUAL)
			.prop("content").end()
			.prop("endTime").dateTime().encoder(new DateEncoder("yyyy-MM-dd HH:mm:ss")).decoder(new DateDecoder("yyyy-MM-dd HH:mm:ss")).end()
			.prop("isDelete").encoder(new BooleanConverter()).decoder(new BooleanConverter()).end();
		
		mf.build(Region.class)
			.filters("parent.name").eq()
			.filters("name").op(Op.RLIKE)
			.filters("type").eq()
			.fk("parent").show("name").end()
			.prop("type").values(new Object[] {"一级", "二级", "三级", "四级"})
				.encoder(v -> { if("一级".equals(v)) return 0; else if("二级".equals(v)) return 1; else if("三级".equals(v)) return 2; else return 3;})
				.decoder(v -> { if(v == null) return v; else { int level = Boolean.TRUE.equals(v) ? 1 : Integer.valueOf(v.toString()); if(level == 0) return "一级"; else if(level==1) return "二级"; else if(level==2) return "三级";else return "四级"; }})
			.prop("areaCode").end()
			.prop("agencyId").hide().end()
			.prop("area").hide().end()
			.prop("farArea").hide().end();
		
		mf.build(ShowSettings.class)
			.prop("banner").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("channel").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("indexBannerImg").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end()
			.prop("notice").values(new Object[] {"true", "false"}).encoder(new BooleanConverter()).decoder(new BooleanConverter()).end();
	}
	
	protected void defMenus(MenuFactory mf) {
		mf.build("订单列表").uri("/orderList").icon("el-icon-s-grid")
			.addItem(Order.class).name("订单列表").uri("/order").icon("el-icon-s-shop");
		
		mf.build("商品管理").uri("/prod").icon("el-icon-s-grid")
			.addItem(Category.class).name("商品分类").icon("el-icon-office-building")
			.addItem(Specification.class).name("商品型号").icon("el-icon-notebook-2")
			.addItem(Goods.class).name("商品列表").icon("el-icon-goods");
		
		mf.build("购物车").uri("/cartList").icon("el-icon-s-grid")
			.addItem(Cart.class).name("购物车").uri("/cart").icon("el-icon-shopping-cart-1");
		
		mf.build("用户列表").uri("/memberList").icon("el-icon-s-grid")
			.addItem(MemberUser.class).name("用户列表").uri("/memberUser").icon("el-icon-user");
		
		mf.build("店铺设置").uri("/store").icon("el-icon-setting")
			.addItem(ShowSettings.class).name("显示设置").uri("/showSettings").icon("el-icon-s-data")
			.addItem(Ad.class).name("广告列表").uri("/ad").icon("el-icon-s-operation")
			.addItem(Notice.class).name("公告管理").uri("/notice").icon("el-icon-date")
			.addItem(FreightTemplate.class).name("运费模板").uri("/freightTemplate").icon("el-icon-tickets")
			.addItem(Shipper.class).name("快递设置").uri("/shipper").icon("el-icon-s-shop")
			.addItem(Settings.class).name("寄件设置").uri("/settings").icon("el-icon-truck");
	}
	
	public class DateEncoder implements Encoder {
		private String pattern = "yyyy-MM-dd HH:mm:ss";
		private DateEncoder(String pattern) {
			this.pattern = pattern;
		}
		public Object encode(Object v) {
			if(v == null || "0".equals(v.toString())) return "";
			return DateUtils.parseDate(v.toString(), this.pattern).getTime()/1000;
		}
	}
	
	public class DateDecoder implements Decoder {
		private String pattern = "yyyy-MM-dd HH:mm:ss";
		private DateDecoder(String pattern) {
			this.pattern = pattern;
		}
		public Object decode(Object v) {
			if(v == null || "0".equals(v.toString())) return "";
			return DateUtils.formatDate(new Date(Integer.valueOf(v.toString()) * 1000L), pattern);
		}
	}
	
	public class OrderStatusConvert implements Encoder, Decoder {
		@Override
		public Object decode(Object value) {
			if(value != null) {
				int v = Integer.valueOf(value.toString());
				if(300 == v) {
					return "待发货";
				}
				else if(301 == v) {
					return "待收货";
				}
				else if(401 == v) {
					return "已收货";
				}
				else if(102 == v) {
					return "已关闭";
				}
				else if(101 == v) {
					return "待付款";
				}
			}
			return value;
		}

		@Override
		public Object encode(Object value) {
			if("待发货".equals(value)) {
				return 300;
			}
			else if("待收货".equals(value)) {
				return 301;
			}
			else if("已收货".equals(value)) {
				return 401;
			}
			else if("已关闭".equals(value)) {
				return 102;
			}
			else if("待付款".equals(value)) {
				return 101;
			}
			return value;
		}
	}
	
	public class BooleanConverter implements Encoder, Decoder {
		@Override
		public Object encode(Object value) {
			return "true".equals(value) ? 1 : 0;
		}
		@Override
		public Object decode(Object value) {
			if(value == null) {
				return value;
			}
			if("true".equals(value.toString()) || "false".equals(value.toString())) {
				return value;
			}
			return Integer.valueOf(value.toString()) == 1 ? "true" : "false";
		}
	}
	
	public class FreightTypeConverter implements Encoder, Decoder {
		public Object encode(Object value) {
			return "按件".equals(value) ? 0 : 1;
		}
		public Object decode(Object value) {
			if(value == null) return value;
			if("true".equals(value.toString())) return "按重";
			else if("false".equals(value.toString())) return "按件";
			return Integer.valueOf(value.toString()) == 0 ? "按件" : "按重";
		}
	}
}
