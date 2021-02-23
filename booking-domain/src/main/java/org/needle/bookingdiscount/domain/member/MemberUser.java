package org.needle.bookingdiscount.domain.member;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.needle.bookingdiscount.domain.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="needle_user")
public class MemberUser extends BaseEntity {
	
	@Column(name="avatar", length=255)
	private String avatar;
	
	@Column(name="nickname", length=120)
	private String nickname;
	
	@Column(name="name", length=60)
	private String name;
	
	@Column(name="username", length=60)
	private String username;
	
	@Column(name="password", length=32)
	private String password;
	
	@Column(name="gender", columnDefinition="int default 0")
	private int gender;
	
	@Column(name="birthday")
	private Integer birthday;
	
	@Column(name="mobile", length=20)
	private String mobile;
	
	@Column(name="register_time")
	private Integer registerTime;
	
	@Column(name="register_ip", length=45)
	private String registerIp;
	
	@Column(name="last_login_ip", length=15)
	private String lastLoginIp;
	
	@Column(name="last_login_time")
	private Integer lastLoginTime;
	
	@Column(name="weixin_openid", length=50)
	private String weixinOpenid;
	
	@Column(name="name_mobile", columnDefinition="tinyint default 0")
	private Integer nameMobile;
	
	@Column(name="country", length=255)
	private String country;
	
	@Column(name="province", length=100)
	private String province;
	
	@Column(name="city", length=100)
	private String city;
	
	@Column(name="token", length=120)
	private String token;
	
	public MemberUser() {}
	
	public MemberUser(Long id) {
		this.id = id;
 	}
	
	@Data
	public static class JsonMemberUser {
		private Long id;
		private String avatar;
		private String nickname;
		private String name;
		private String username;
		private String password;
		private int gender;
		private Integer birthday;
		private String mobile;
		private Integer register_time;
		private String register_ip;
		private String last_login_ip;
		private Integer last_login_time;
		private String weixin_openid;
		private Integer name_mobile;
		private String country;
		private String province;
		private String city;
		
		public JsonMemberUser(MemberUser user) {
			this.id = user.getId();
			this.avatar = user.getAvatar();
			this.nickname = user.getNickname();
			this.name = user.getName();
			this.username = user.getUsername();
			this.password = user.getPassword();
			this.gender = user.getGender();
			this.birthday = user.getBirthday();
			this.mobile = user.getMobile();
			this.register_time = user.getRegisterTime();
			this.register_ip = user.getRegisterIp();
			this.last_login_ip = user.getLastLoginIp();
			this.last_login_time = user.getLastLoginTime();
			this.weixin_openid = user.getWeixinOpenid();
			this.name_mobile = user.getNameMobile();
			this.country = user.getCountry();
			this.province = user.getProvince();
			this.city = user.getCity();
		}
		
	}
}

