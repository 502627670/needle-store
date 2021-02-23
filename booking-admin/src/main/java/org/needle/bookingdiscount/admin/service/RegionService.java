package org.needle.bookingdiscount.admin.service;

import org.needle.bookingdiscount.admin.repository.RegionRepository;
import org.needle.bookingdiscount.domain.support.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegionService {
	
	@Autowired
	private RegionRepository regionRepository;
	
	public Region findById(Long id) {
		return regionRepository.findById(id).get();
	}
	
}
