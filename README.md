### 绣花针小店，开源商城

+ 基于开源项目构建，使用java重写了服务和后台
+ 测试数据来自开源项目
+ 服务端api基于java + springboot + jpa + MySQL
+ 后台管理端基于vue + vue-element-admin + java + springboot + jpa + MySQL，依赖needle-design框架

#### 本项目需要配合  
服务端： booking-server <br />
管理后台：booking-admin <br />
微信小程序：booking-wxapp <br/>

线上后台demo： http://47.107.173.194/booking-admin/ <br />
用户名：guest  
密码：guest  

微信小程序演示可搜索：绣花针小店

! [image] (https://mp.weixin.qq.com/wxopen/qrcode?action=show&type=2&fakeid=3829364956&token=21674227)

### 项目截图

### 小程序功能列表
+ 首页：搜索、Banner、公告、分类Icons、分类商品列表
+ 详情页：加入购物车、立即购买、选择规格
+ 搜索页：排序
+ 分类页：分页加载商品
+ 我的页面：订单（待付款，待发货，待收货），足迹，收货地址

### 小程序最近更新 
U 将网络图标改成本地图标  
U 更新支付方式的UI  
F 修复轮播图的bug  
F 修复没有商品时的错误显示问题  
F 修复share.js的一个bug  
A 增加发货时的订阅消息  

#### 小程序完整的购物流程，商品加入购物车 --> 收货地址的选择 --> 下单支付 --> 确认收货

### 小程序项目结构
```
├─config     
│  └─api　
├─images    
│  └─icon
│  └─nav
├─lib
│  └─wxParse　　　
├─pages
│  ├─app-auth
│  ├─cart
│  ├─category
│  ├─goods
│  ├─index
│  ├─order-check
│  ├─payResult
│  ├─search
│  └─ucenter
│      ├─address
│      ├─address-detail
│      ├─express-info
│      ├─footprint
│      ├─goods-list
│      ├─index
│      ├─order-details
│      ├─order-list
│      └─settings
├─services
└─utils
```
### 本地开发

1、在https://mp.weixin.qq.com/ 注册你的小程序，得到appid和secret，微信开发者工具中设置appid。<br />

2、在booking-server的application.properties中设置好appid和secret。<br />

- 项目地址：<br />

微信小程序：booking-wxapp <br/>

后台管理：booking-admin <br/>

服务端： booking-server <br/>

- 本项目会持续更新和维护，喜欢别忘了 Star，有问题可通过微信、QQ群联系我，谢谢您的关注。



