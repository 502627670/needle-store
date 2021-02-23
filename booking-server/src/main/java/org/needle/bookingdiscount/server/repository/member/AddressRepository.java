package org.needle.bookingdiscount.server.repository.member;

import java.util.List;

import org.needle.bookingdiscount.domain.member.Address;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
	
	@Query("from Address t where t.user.id=?1 and t.isDefault=?2 and t.isDelete=?3 order by id desc")
	public List<Address> findByUserAndIsDefaultAndIsDelete(Long userId, boolean isDefault, boolean isDelete);
	
	@Query("from Address t where t.user.id=?1 and t.isDelete=?2 order by id desc")
	public List<Address> findByUserAndIsDelete(Long userId, boolean isDelete);
	
}
