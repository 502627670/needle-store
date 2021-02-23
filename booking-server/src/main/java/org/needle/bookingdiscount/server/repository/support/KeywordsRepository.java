package org.needle.bookingdiscount.server.repository.support;

import java.util.List;

import org.needle.bookingdiscount.domain.support.Keywords;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface KeywordsRepository extends PagingAndSortingRepository<Keywords, Long> {
	
	@Query("from Keywords t where t.isDefault=?1 ")
	public List<Keywords> findByIsDefault(Boolean isDefault, Pageable pageable);
	
	@Query("select distinct t.keyword, t.isHot from Keywords t ")
	public List<Object[]> distinctKeywordAndIsHot(Pageable pageable);
	
	@Query("select distinct t.keyword from Keywords t where t.keyword like ?1")
	public List<String> distinctKeywordByKeyword(String keyword, Pageable pageable);
	
	
}
