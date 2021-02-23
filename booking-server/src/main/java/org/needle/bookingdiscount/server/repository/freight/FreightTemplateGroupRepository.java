package org.needle.bookingdiscount.server.repository.freight;

import java.util.List;

import org.needle.bookingdiscount.domain.freight.FreightTemplateGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FreightTemplateGroupRepository extends PagingAndSortingRepository<FreightTemplateGroup, Long> {

	@Query("from FreightTemplateGroup t where t.template.id=?1 and t.area=?2 ")
	public List<FreightTemplateGroup> findByTemplateAndArea(Long templateId, String area);
	
}
