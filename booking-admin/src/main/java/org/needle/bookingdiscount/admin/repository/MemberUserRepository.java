package org.needle.bookingdiscount.admin.repository;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberUserRepository extends PagingAndSortingRepository<MemberUser, Long> {

}
