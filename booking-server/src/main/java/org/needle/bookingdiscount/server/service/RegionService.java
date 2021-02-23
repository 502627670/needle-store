package org.needle.bookingdiscount.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.support.Region;
import org.needle.bookingdiscount.domain.support.Region.JsonRegion;
import org.needle.bookingdiscount.server.repository.support.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class RegionService {
	
	@Autowired
	private RegionRepository regionRepository;
	
	public List<JsonRegion> list(String parentId) {
		Long pid = 0L;
		if(StringUtils.hasText(parentId)) {
			pid = Long.valueOf(parentId);
		}
		List<Region> regionList = regionRepository.findByParent(pid);
        return regionList.stream().map(r -> new JsonRegion(r)).collect(Collectors.toList());
	}
	
}
