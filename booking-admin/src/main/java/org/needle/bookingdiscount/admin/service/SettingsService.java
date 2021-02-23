package org.needle.bookingdiscount.admin.service;

import java.util.List;
import java.util.Map;

import org.needle.bookingdiscount.admin.repository.RegionRepository;
import org.needle.bookingdiscount.domain.freight.Settings;
import org.needle.bookingdiscount.domain.support.Region;
import org.needleframe.core.model.ActionData;
import org.needleframe.core.model.Module;
import org.needleframe.core.service.AbstractDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingsService extends AbstractDataService {

	@Autowired
	private RegionRepository regionRepository;
	
	@Override
	protected Class<?> getModelClass() {
		return Settings.class;
	}

	@Override
	protected void beforeCreate(Module module, List<ActionData> dataList) {
		dataList.forEach(actionData -> {
			actionData.getData().forEach(data -> {
				Settings settings = module.fromData(data);
				Region province = regionRepository.findById(settings.getProvince().getId()).get();
				Region city = regionRepository.findById(settings.getCity().getId()).get();
				Region district = regionRepository.findById(settings.getDistrict().getId()).get();
				
				data.put("provinceName", province.getName());
				data.put("cityName", city.getName());
				data.put("expAreaName", district.getName());
			});
		});
	}

	@Override
	protected void beforeUpdate(Module module, List<Map<String, Object>> dataList) {
		dataList.forEach(data -> {
			Settings settings = module.fromData(data);
			Region province = regionRepository.findById(settings.getProvince().getId()).get();
			Region city = regionRepository.findById(settings.getCity().getId()).get();
			Region district = regionRepository.findById(settings.getDistrict().getId()).get();
			data.put("provinceName", province.getName());
			data.put("cityName", city.getName());
			data.put("expAreaName", district.getName());
		});
	}
}
