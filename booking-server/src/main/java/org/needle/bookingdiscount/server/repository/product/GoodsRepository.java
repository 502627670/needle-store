package org.needle.bookingdiscount.server.repository.product;

import java.util.List;

import org.needle.bookingdiscount.domain.product.Goods;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GoodsRepository extends PagingAndSortingRepository<Goods, Long> {
	
	@Query("from Goods t where t.category.id=?1 and t.goodsNumber>=?2 and t.isOnSale=?3 and t.isIndex=?4 and t.isDelete=?5 order by t.sortOrder")
	public List<Goods> findGoods(Long categoryId, int goodsNumber, Boolean isOnSale, Boolean isIndex, Boolean isDelete);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 order by t.sortOrder ")
	public List<Goods> findByIsOnSaleAndIsDelete(Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 and t.name like ?3 order by t.sortOrder asc")
	public List<Goods> findByIsOnSaleAndIsDeleteAndKeyword(Boolean isOnSale, Boolean isDelete, String keyword, Pageable pageable);
	
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 order by t.retailPrice asc")
	public List<Goods> findByIsOnSaleAndIsDeleteOrderByPriceAsc(Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 order by t.retailPrice desc")
	public List<Goods> findByIsOnSaleAndIsDeleteOrderByPriceDesc(Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 and t.name like ?3 order by t.retailPrice asc")
	public List<Goods> findByIsOnSaleAndIsDeleteAndKeywordOrderByPriceAsc(Boolean isOnSale, Boolean isDelete, String keyword, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 and t.name like ?3 order by t.retailPrice desc")
	public List<Goods> findByIsOnSaleAndIsDeleteAndKeywordOrderByPriceDesc(Boolean isOnSale, Boolean isDelete, String keyword, Pageable pageable);
	
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 order by t.sellVolume asc")
	public List<Goods> findByIsOnSaleAndIsDeleteOrderBySaleAsc(Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 order by t.sellVolume desc")
	public List<Goods> findByIsOnSaleAndIsDeleteOrderBySaleDesc(Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 and t.name like ?3 order by t.sellVolume asc")
	public List<Goods> findByIsOnSaleAndIsDeleteAndKeywordOrderBySaleAsc(Boolean isOnSale, Boolean isDelete, String keyword, Pageable pageable);
	
	@Query("from Goods t where t.isOnSale=?1 and t.isDelete=?2 and t.name like ?3 order by t.sellVolume desc")
	public List<Goods> findByIsOnSaleAndIsDeleteAndKeywordOrderBySaleDesc(Boolean isOnSale, Boolean isDelete, String keyword, Pageable pageable);
	
	
	@Query("from Goods t where t.category.id=?1 and t.isOnSale=?2 and t.isDelete=?3 order by t.sortOrder ")
	public List<Goods> findByCategoryAndIsOnSaleAndIsDelete(Long categoryId, Boolean isOnSale, Boolean isDelete, Pageable pageable);
	
	
    @Query("select count(t) from Goods t where t.isOnSale=?1 and t.isDelete=?2")
	public Long countByIsOnSaleAndIsDelete(boolean isOnSale, boolean isDelete);
    
    
    @Modifying
    @Query("update Goods t set t.goodsNumber=t.goodsNumber+?1 where id=?2")
    public void increment(int goodsNumber, Long id);
    
    @Modifying
    @Query("update Goods t set t.goodsNumber=t.goodsNumber-?1 where id=?2")
    public void decrement(int goodsNumber, Long id);
    
    @Modifying
    @Query("update Goods t set t.sellVolume=t.sellVolume+?1 where id=?2")
    public void incrementSellVolume(int sellVolume, Long id);
    
}
