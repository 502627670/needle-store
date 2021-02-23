package org.needle.bookingdiscount.server.repository.member;

import java.util.List;

import org.needle.bookingdiscount.domain.member.Footprint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FootprintRepository extends PagingAndSortingRepository<Footprint, Long> {
	
	@Query("select t from Footprint t left join t.goods t1 where t.user.id=?1 order by t.addTime desc")
	public List<Footprint> findByUser(Long userId, Pageable pageable);
	
}
