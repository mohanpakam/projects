package com.mpakam.service.strategy;

import java.util.Set;

import com.mpakam.model.StockQuote;

public interface IStrategyService {

	public void executeStrategy(Set<StockQuote> quotes);
	
}
