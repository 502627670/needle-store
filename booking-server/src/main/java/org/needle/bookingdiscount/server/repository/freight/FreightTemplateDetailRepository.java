package org.needle.bookingdiscount.server.repository.freight;

import java.util.List;

import org.needle.bookingdiscount.domain.freight.FreightTemplateDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FreightTemplateDetailRepository extends PagingAndSortingRepository<FreightTemplateDetail, Long> {
	
	@Query("from FreightTemplateDetail t where t.template.id=?1 and t.area=?2 and t.isDelete=false")
	public List<FreightTemplateDetail> findByTemplateAndArea(Long freightTemplateId, Integer area);
	
}
