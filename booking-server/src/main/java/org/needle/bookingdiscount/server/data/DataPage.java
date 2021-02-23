package org.needle.bookingdiscount.server.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class DataPage {
	
	private int count = 0;
	
	private int currentPage;
	
	private List<?> data = new ArrayList<Object>();
	
	public DataPage(int currentPage, List<?> data) {
		this.count = data.size();
		this.currentPage = currentPage;
		this.data = data;
	}
	
}
