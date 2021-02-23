package org.needle.booking_server;

import org.needle.bookingdiscount.utils.DateUtils;

public class PayTest {
	
//	<xml><appid><![CDATA[wx4e9b74e0999d1764]]></appid>
//	<bank_type><![CDATA[OTHERS]]></bank_type>
//	<cash_fee><![CDATA[1500]]></cash_fee>
//	<fee_type><![CDATA[CNY]]></fee_type>
//	<is_subscribe><![CDATA[N]]></is_subscribe>
//	<mch_id><![CDATA[1605111309]]></mch_id>
//	<nonce_str><![CDATA[1613121377575]]></nonce_str>
//	<openid><![CDATA[oDV-d5eoxj38EoG7zRtCsZfo5ifE]]></openid>
//	<out_trade_no><![CDATA[12200212000001353]]></out_trade_no>
//	<result_code><![CDATA[SUCCESS]]></result_code>
//	<return_code><![CDATA[SUCCESS]]></return_code>
//	<sign><![CDATA[0AD78B7BEFC08EF61124993F0CD8ACB7]]></sign>
//	<time_end><![CDATA[20210212171623]]></time_end>
//	<total_fee>1500</total_fee>
//	<trade_type><![CDATA[JSAPI]]></trade_type>
//	<transaction_id><![CDATA[4200000944202102121924625499]]></transaction_id>
//	</xml>
	
	public static void main(String[] args) {
//		20210212171623
		
		System.out.println(DateUtils.parseDate("20210212171623", "yyyyMMddHHmmss"));
		
		System.out.println((int) (DateUtils.parseDate("20210212171623", "yyyyMMddHHmmss").getTime()/1000));
	}
	
}
