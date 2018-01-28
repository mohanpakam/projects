package com.mpakam.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;
import org.springframework.stereotype.Service;

import com.mpakam.model.Stock;
import com.mpakam.model.StockQuote;

@Service
public class AlphaVantageService implements IQuoteDataProviderService {

	@Override
	public TreeSet<StockQuote> retrieveCandleData(Stock stockObj) throws Exception{
		
		String symbol=stockObj.getTicker();
		int stockNum = stockObj.getStocknum();
		int interval = stockObj.getInterval();
		if(Interval.getByTime(interval) == null) {
			throw new Exception("Interval " + interval + " provided is incorrect");
		}
		
		System.out.println("Symbol "+ symbol + " Interval: "+ interval );
		
		String apiKey = "VZ5VN30W0C05IUWA"; ///TODO: Externalize this apiKey 
	    int timeout = 30000;
	    AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
	    TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
	    
	    TreeSet<StockQuote> quotesList = new TreeSet<StockQuote>();
	    try {
			// TODO: Remove the hard coded Thirty Minutes and introduce something thats configurable
			IntraDay response = stockTimeSeries.intraDay(symbol, Interval.getByTime(interval),
					OutputSize.COMPACT);
			Map<String, String> metaData = response.getMetaData();
			System.out.println("Information: " + metaData.get("1. Information"));
			System.out.println("Stock: " + metaData.get("2. Symbol"));

	      List<StockData> stockData = response.getStockData();
	      stockData.forEach(stock -> {
	    	StockQuote quote = new StockQuote();
	    	quote.setClose(new BigDecimal(stock.getClose()));
	    	quote.setOpen(new BigDecimal(stock.getOpen()));
	    	quote.setHigh(new BigDecimal(stock.getHigh()));
	    	quote.setLow(new BigDecimal(stock.getLow()));
	    	quote.setQuoteDatetime(stock.getDateTime());
	    	quote.setStock(stockObj);
	        System.out.println("date:   " + stock.getDateTime());
	        System.out.println("open:   " + stock.getOpen());
	        System.out.println("high:   " + stock.getHigh());
	        System.out.println("low:    " + stock.getLow());
	        System.out.println("close:  " + stock.getClose());
	        System.out.println("volume: " + stock.getVolume());
	        quotesList.add(quote);
	      });
	    } catch (AlphaVantageException e) {
	    	e.printStackTrace();
	      System.out.println("something went wrong");

	    }

		return quotesList;
	}

}
