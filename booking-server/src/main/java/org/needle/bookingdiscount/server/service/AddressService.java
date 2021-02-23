package org.needle.bookingdiscount.server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.member.Address;
import org.needle.bookingdiscount.domain.member.Address.JsonAddress;
import org.needle.bookingdiscount.domain.member.MemberUser;
import org.needle.bookingdiscount.server.exception.ServiceException;
import org.needle.bookingdiscount.server.repository.member.AddressRepository;
import org.needle.bookingdiscount.server.repository.support.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private RegionRepository regionRepository;
	
	public List<JsonAddress> getAddresses(Long userId) {
		List<Address> addressList = addressRepository.findByUserAndIsDelete(userId, false);
        for(int i = 0; i < addressList.size(); i++) {
        	Address addressItem = addressList.get(i);
        	addressItem.setProvinceName(regionRepository.findById(addressItem.getProvinceId()).get().getName());
        	addressItem.setCityName(regionRepository.findById(addressItem.getCityId()).get().getName());
        	addressItem.setDistrictName(regionRepository.findById(addressItem.getDistrictId()).get().getName());
        	addressItem.setFullRegion(addressItem.getProvinceName() + addressItem.getCityName() + addressItem.getDistrictName());
        }
        return addressList.stream().map(a -> new JsonAddress(a)).collect(Collectors.toList());
	}
	
	//收货地址详情
	public JsonAddress addressDetail(Long userId, Long id) {
        Long addressId = id;
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        if(addressOpt.isPresent()) {
        	Address addressInfo = addressOpt.get();
        	addressInfo.setProvinceName(regionRepository.findById(addressInfo.getProvinceId()).get().getName());
        	addressInfo.setCityName(regionRepository.findById(addressInfo.getCityId()).get().getName());
        	addressInfo.setDistrictName(regionRepository.findById(addressInfo.getDistrictId()).get().getName());
        	addressInfo.setFullRegion(addressInfo.getProvinceName() + addressInfo.getCityName() + addressInfo.getDistrictName());
        	return new JsonAddress(addressInfo);
        }
        return new JsonAddress();
	}
	
	//保存收货地址
	public JsonAddress saveAddress(Long userId, Long id, String name, String mobile, Long province_id, Long city_id, 
			Long district_id, String address, Boolean is_default) {
		
        Long addressId = id;
        Address addressData = new Address();
        addressData.setId(id);
        addressData.setName(name);
        addressData.setMobile(mobile);
        addressData.setCountryId(1L);
        addressData.setProvinceId(province_id);
        addressData.setCityId(city_id);
        addressData.setDistrictId(district_id);
        addressData.setAddress(address);
        addressData.setIsDefault(is_default);
        addressData.setIsDelete(false);
        addressData.setUser(new MemberUser(userId));
        addressRepository.save(addressData);
        
        if(Boolean.TRUE.equals(is_default)) {
        	addressRepository.findByUserAndIsDelete(userId, false).forEach(adds -> {
        		if(addressId != null &&  adds.getId() != addressId) {
        			if(Boolean.TRUE.equals(adds.getIsDefault())) {
        				adds.setIsDefault(false);
        				addressRepository.save(adds);
        			}
        		}
        	});
        }
        
        return new JsonAddress(addressData);
	}

	public JsonAddress deleteAddress(Long userId, Long id) {
		Optional<Address> addressOpt = addressRepository.findById(id);
        if(!addressOpt.isPresent()) {
        	throw new ServiceException("地址不存在");
        }
        Address address = addressOpt.get();
    	address.setIsDefault(true);
    	addressRepository.save(address);
        return new JsonAddress(address);
	}
}
