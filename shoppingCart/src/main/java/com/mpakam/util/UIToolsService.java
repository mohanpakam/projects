package com.mpakam.util;

import java.util.LinkedList;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.mpakam.model.StrategyStockQuote;

@Service
public class UIToolsService {

	public Object[][] convertToHACandleData(LinkedList<StrategyStockQuote> quotes){
		Object[][] returnData = new Object[quotes.size()][5];
		int i=0;
		for(StrategyStockQuote quote:quotes) {
			returnData[i][0]=quote.getStockQuote().getQuoteDatetime().toString();
			returnData[i][1]=quote.getXlow();
			returnData[i][2]=quote.getXopen();
			returnData[i][3]=quote.getXclose();
			returnData[i][4]=quote.getXhigh();
			i++;
		}
		return returnData;
	}
	
	public Object[][] stochRsiFromHA(LinkedList<StrategyStockQuote> quotes){
		Object[][] returnData = new Object[quotes.size()][3];
		int i=0;
		for(StrategyStockQuote quote:quotes) {
			returnData[i][0]=quote.getStockQuote().getQuoteDatetime().toString();
			returnData[i][1]=(quote.getStochRsiK()==null)?0:quote.getStochRsiK();
			returnData[i][2]=quote.getStochRsiD()==null?0:quote.getStochRsiD();
			i++;
		}
		return returnData;
	}
	
}
