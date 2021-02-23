package org.needle.bookingdiscount.server.security;

import java.util.List;
import java.util.Optional;

import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.member.MemberUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class SecurityContext {
	
	@Autowired
	private MemberUserRepository memberUserRepository;
	
	public void setUserToken(Long userId, String token) {
		Optional<MemberUser> userOpt = memberUserRepository.findById(userId);
		if(!userOpt.isPresent()) {
			throw new ServiceException("用户不存在");
		}
		MemberUser user = userOpt.get();
		user.setToken(token);
		memberUserRepository.save(user);
	}
	
	public Optional<MemberUser> getUserByToken(String token) {
		List<MemberUser> users = memberUserRepository.findByToken(token);
		return Optional.ofNullable(users.isEmpty() ? null : users.get(0));
	}
	
}
