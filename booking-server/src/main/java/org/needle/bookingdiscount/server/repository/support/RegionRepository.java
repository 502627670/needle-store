package org.needle.bookingdiscount.server.repository.support;

import java.util.List;

import org.needle.bookingdiscount.domain.support.Region;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RegionRepository extends PagingAndSortingRepository<Region, Long> {
	
	@Query("from Region t where t.parent.id=?1 and t.isDelete=false")
	public List<Region> findByParent(Long parentId);
	
}
