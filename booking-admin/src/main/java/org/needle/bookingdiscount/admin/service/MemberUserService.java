package org.needle.bookingdiscount.admin.service;

import org.needle.bookingdiscount.admin.repository.MemberUserRepository;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberUserService {
	
	@Autowired
	private MemberUserRepository memberUserRepository;
	
	public MemberUser getById(Long id) {
		return memberUserRepository.findById(id).get();
	}
	
}
