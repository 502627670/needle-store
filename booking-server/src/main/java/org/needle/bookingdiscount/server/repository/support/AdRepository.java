package org.needle.bookingdiscount.server.repository.support;

import java.util.List;

import org.needle.bookingdiscount.domain.support.Ad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AdRepository extends PagingAndSortingRepository<Ad, Long> {
	
	@Query("from Ad t where t.enabled=?1 and t.isDelete=?2 order by t.sortOrder asc")
	public List<Ad> findByEnabledAndIsDelete(Boolean enabled, Boolean isDelete);
	
}
