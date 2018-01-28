package com.mpakam;

import java.util.LinkedList;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mpakam.dao.CustomerTickerTrackerDao;
import com.mpakam.dao.StockDao;
import com.mpakam.dao.StockQuoteDao;
import com.mpakam.dao.StrategyStockQuoteDao;
import com.mpakam.model.CustomerTickerTracker;
import com.mpakam.model.Stock;
import com.mpakam.model.StockQuote;
import com.mpakam.model.StrategyStockQuote;
import com.mpakam.service.IStockQuoteService;
import com.mpakam.service.strategy.IStrategyService;
import com.mpakam.util.IQuoteDataProviderService;
import com.mpakam.util.UIToolsService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TradingAlertsApplication.class)
@TestPropertySource("classpath:application-test.properties")
public class TradingAlertsApplicationTests {

	@Autowired
	IStockQuoteService quoteService;
	
	@Autowired
	IQuoteDataProviderService dataProvider;
	
	@Autowired
	StockDao stockDao;
	
	@Autowired
	StockQuoteDao stockQuoteDao;
	
	@Autowired
	UIToolsService uiSvc;
	
	@Autowired
	IStrategyService strategySvc;
	
	@Autowired
	StrategyStockQuoteDao strategyStockDao;
	
	@Autowired
	CustomerTickerTrackerDao trackerDao;
	
	
//    @Test
    public void runSampleTest() {
    	System.out.println("Hello sweet success");
    }
    
  //  @Test
    public void retrieveStockQuotes() throws Exception {
    	Stock stock = stockDao.findBy(7);
    	dataProvider.retrieveCandleData(stock).forEach(s->{
    		System.out.println(s.getStockQuoteId()+"-"+s.getQuoteDatetime());
    		});

    }
    
    @Test
    public void analyzeStockTest() throws Exception{
    	Stock s =new Stock();
    	s.setInterval(1);
    	s.setStocknum(7);
    	s.setStockName("BA");
    	s.setTicker("BA");
    	quoteService.analyzeStock(s);
    }
    
    
    @Test
    public void retrieveLatestStockQuote() {
    	StockQuote sq=stockQuoteDao.findLastStockQuote(7);
    	System.out.println(sq!=null?sq.getStockQuoteId():"No Records exists");
    }
    
    @Test
    @Transactional
    public void retrieveLatestStrategyStockQuote() {
CustomerTickerTracker custTrkr = trackerDao.findById(3);
		
		LinkedList<StrategyStockQuote> quoteList = 
				strategyStockDao.retrieveQuotesByStockNumStrategyId(custTrkr.getStock().getStocknum(),
						custTrkr.getStrategyId());
		
		uiSvc.convertToHACandleData(quoteList);
		uiSvc.stochRsiFromHA(quoteList);
    }
}

