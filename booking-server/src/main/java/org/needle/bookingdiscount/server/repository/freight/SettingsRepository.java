package org.needle.bookingdiscount.server.repository.freight;

import org.needle.bookingdiscount.domain.freight.Settings;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettingsRepository extends PagingAndSortingRepository<Settings, Long> {
	
}
