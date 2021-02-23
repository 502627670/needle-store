package org.needle.bookingdiscount.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.needle.bookingdiscount.domain.support.Keywords;
import org.needle.bookingdiscount.domain.support.Keywords.JsonKeywords;
import org.needle.bookingdiscount.server.repository.member.SearchHistoryRepository;
import org.needle.bookingdiscount.server.repository.support.KeywordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
@Transactional
public class SearchService {
	
	@Autowired
	private KeywordsRepository keywordsRepository;
	
	@Autowired
	private SearchHistoryRepository searchHistoryRepository;
	
	// 搜索页面数据
	public ModelMap index(String keywords, Long userId) {
		Pageable pageable = PageRequest.of(0, 1);
		// 取出输入框默认的关键词
		List<Keywords> defaultKeyword = keywordsRepository.findByIsDefault(true, pageable);
		
		// 取出热闹关键词
        List<Object[]> hotKeywordList = keywordsRepository.distinctKeywordAndIsHot(PageRequest.of(0, 10));
        List<String> historyKeywordList = userId == null ? new ArrayList<String>() : 
    			searchHistoryRepository.distinctKeywordByUser(userId, PageRequest.of(0, 10));
        
        ModelMap model = new ModelMap();
        model.addAttribute("defaultKeyword", defaultKeyword.stream().map(k -> new JsonKeywords(k)).collect(Collectors.toList()));
        model.addAttribute("hotKeywordList", hotKeywordList);
        model.addAttribute("historyKeywordList", historyKeywordList);
        
        return model;
	}
	
	//搜索帮助
	public List<String> helper(String keyword) {
		List<String> keywords = keywordsRepository.distinctKeywordByKeyword(keyword + "%", PageRequest.of(0, 10));
        return keywords;
	}
	
	public void clearHistory(Long userId) {
		if(userId != null) {
			searchHistoryRepository.findByUser(userId).forEach(searchHistory -> {
				searchHistoryRepository.delete(searchHistory);
			});
		}
	}
		
}
