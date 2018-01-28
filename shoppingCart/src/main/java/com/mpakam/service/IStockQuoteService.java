package com.mpakam.service;

import java.util.List;
import java.util.TreeSet;

import javax.transaction.Transactional;

import com.mpakam.model.Stock;
import com.mpakam.model.StockQuote;

public interface IStockQuoteService {


	@Transactional
	public void analyzeStock(Stock symbol) throws Exception;

}
