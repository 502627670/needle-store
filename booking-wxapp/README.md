### 绣花针小店，开源商城（微信小程序端）

+ 本项目基于开源项目构建，使用java重写了服务和后台
+ 测试数据来自于开源项目
+ 服务端api基于java + springboot + jpa + MySQL

#### 本项目需要配合  
服务端： https://github.com/502627670/needle-store/tree/master/booking-server 
管理后台：https://github.com/502627670/needle-store/tree/master/booking-admin

线上demo：
后台：http://47.107.173.194/booking-admin/
用户名：guest  
密码：123456

小程序：
<img src="https://mp.weixin.qq.com/wxopen/qrcode?action=show&type=2&fakeid=3829364956&token=21674227" />

### 项目截图

### 功能列表
+ 首页：搜索、Banner、公告、分类Icons、分类商品列表
+ 详情页：加入购物车、立即购买、选择规格
+ 搜索页：排序
+ 分类页：分页加载商品
+ 我的页面：订单（待付款，待发货，待收货），足迹，收货地址

### 最近更新 
- 3.26 更新详情  
U 将网络图标改成本地图标  
U 更新支付方式的UI  
F 修复轮播图的bug  
F 修复没有商品时的错误显示问题  
F 修复share.js的一个bug  
A 增加发货时的订阅消息  

- 12.14 新增生成分享图的功能

#### 完整的购物流程，商品加入购物车 --> 收货地址的选择 --> 下单支付 --> 确认收货

### 项目结构
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

请在https://mp.weixin.qq.com/ 注册你的小程序，得到appid和secret，微信开发者工具中设置appid。  
在booking-server的application.properties中设置好appid和secret。  

- 项目地址  
微信小程序：https://gitee.com/jee502627670/needle-store-wxapp.git/booking-wxapp
服务端： https://github.com/502627670/needle-store/tree/master/booking-server 
管理后台：https://github.com/502627670/needle-store/tree/master/booking-admin

开发说明：
1、下载代码
2、在微信开发工具中修改小程序appid
3、在config/api.js文件中修改appid
4、在config/api.js文件中修改ApiRootUrl为正确的接口服务地址

- 本项目会持续更新和维护，喜欢别忘了 Star，有问题可通过QQ（502627670）联系我，谢谢您的关注。



