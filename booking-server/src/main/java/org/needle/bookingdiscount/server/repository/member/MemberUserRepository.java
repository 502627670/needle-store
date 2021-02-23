package org.needle.bookingdiscount.server.repository.member;

import java.util.List;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberUserRepository extends PagingAndSortingRepository<MemberUser, Long> {
	
	@Query("from MemberUser t where t.weixinOpenid=?1 ")
	public List<MemberUser> findByWeixinOpenid(String weixinOpenid);
	
	@Query("from MemberUser t where t.token=?1")
	public List<MemberUser> findByToken(String token);
	
}
