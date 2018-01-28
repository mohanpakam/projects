package com.mpakam.dao;

import java.util.LinkedList;

import com.mpakam.model.StrategyStockQuote;

public interface StrategyStockQuoteDao extends GenericDao<StrategyStockQuote>{
	LinkedList<StrategyStockQuote> retrieveLastXQuotesByStockNumStrategyId(int stockNum, int strategyNum);
	LinkedList<StrategyStockQuote> retrieveQuotesByStockNumStrategyId(int stockNum, int strategyNum);
}
