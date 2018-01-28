package com.mpakam.dao;

import javax.transaction.Transactional;

import com.mpakam.model.Stock;

public interface StockDao {
	@Transactional
	Stock findBy(int i);
	Long save(Stock stock);
	void update(Stock cart);
	
}
