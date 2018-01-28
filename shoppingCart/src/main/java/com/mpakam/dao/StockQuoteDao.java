package com.mpakam.dao;

import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.mpakam.model.StockQuote;

public interface StockQuoteDao {

	
	TreeSet<StockQuote> findAllByStock(Long stockNum);
	int save(StockQuote stockQuote);
	void update(StockQuote stockQuote);
	
	void save(TreeSet<StockQuote> stockQuoteList);
	@Transactional
	StockQuote findLastStockQuote(int stockNum);
	
}
