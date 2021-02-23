package org.needle.bookingdiscount.server.repository.product;

import java.util.List;

import org.needle.bookingdiscount.domain.product.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {
	
	@Query("from Category t where t.isChannel=?1 and t.parentId=?2 order by t.sortOrder")
	public List<Category> findByIsChannelAndParent(Boolean isChannel, Long parentId);
	
	@Query("from Category t where t.isShow=?1 and t.parentId=?2 order by t.sortOrder")
	public List<Category> findByIsShowAndParent(Boolean isShow, Long parentId);
	
	@Query("from Category t where t.isCategory=?1 and t.parentId=?2 order by t.sortOrder")
	public List<Category> findByIsCategoryAndParentId(Boolean isCategory, Long parentId, Pageable pageable);
	
}
