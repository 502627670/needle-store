package org.needle.bookingdiscount.server.repository.member;

import java.util.List;

import org.needle.bookingdiscount.domain.member.SearchHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SearchHistoryRepository extends PagingAndSortingRepository<SearchHistory, Long> {
	
	@Query("select distinct keyword from SearchHistory t where t.user.id=?1")
	public List<String> distinctKeywordByUser(Long userId, Pageable pageable);
	
	@Query("from SearchHistory t where t.user.id=?1")
	public List<SearchHistory> findByUser(Long userId);
	
}
