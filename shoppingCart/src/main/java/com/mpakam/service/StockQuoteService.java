/**
 * 
 */
package com.mpakam.service;

import java.util.Set;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpakam.dao.CustomerTickerTrackerDao;
import com.mpakam.dao.StockQuoteDao;
import com.mpakam.model.Stock;
import com.mpakam.model.StockQuote;
import com.mpakam.service.strategy.IStrategyService;
import com.mpakam.util.IQuoteDataProviderService;

/**
 * @author LuckyMonaA
 *
 */
@Service
public class StockQuoteService implements IStockQuoteService {
	
	
	@Autowired
	IQuoteDataProviderService dataProviderSvc;
	
	@Autowired
	StockQuoteDao quoteDao;
	
	@Autowired
	CustomerTickerTrackerDao customerTkrDao;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	IStrategyService strategySvc;
	
	

	/* (non-Javadoc)
	 * @see com.mpakam.service.StockQuoteService#syncBySymbol(java.lang.String)
	 */
	@Transactional
	@Override
	public void analyzeStock(Stock symbol) throws Exception {
		Set<StockQuote> quoteList =dataProviderSvc.retrieveCandleData(symbol);
		//calculateHeikenAshi(quoteList,lastItem);
		//TODO: Call the strategy selector
		TreeSet<StockQuote> quotes = saveQuotes(symbol.getStocknum(),quoteList);
		if(!quotes.isEmpty())
			strategySvc.executeStrategy(quotes);
		else
			System.out.println("No new records were found- Market may have probably closed");
		return ;
	}
	
	private TreeSet<StockQuote> saveQuotes(int stockNum, Set<StockQuote> quoteList) {
		TreeSet<StockQuote> newRecords = new TreeSet<StockQuote>();
		StockQuote lastItem=quoteDao.findLastStockQuote(stockNum);
//		newRecords.add(lastItem);
		quoteList.forEach(p->{
			if(lastItem ==null || p.getQuoteDatetime().isAfter(lastItem.getQuoteDatetime())){
				p.setStockQuoteId(quoteDao.save(p));
				newRecords.add(p);
			}
		});
		return newRecords;
	}
	
}
