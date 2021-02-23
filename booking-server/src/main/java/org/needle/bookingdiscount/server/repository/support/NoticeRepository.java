package org.needle.bookingdiscount.server.repository.support;

import java.util.List;

import org.needle.bookingdiscount.domain.support.Notice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NoticeRepository extends PagingAndSortingRepository<Notice, Long> {
	
	@Query("from Notice t where t.isDelete=?1 order by t.id asc")
	public List<Notice> findByIsDelete(Boolean isDelete);
	
}
