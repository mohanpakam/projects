package com.mpakam.service.strategy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpakam.dao.StrategyStockQuoteDao;
import com.mpakam.model.StockQuote;
import com.mpakam.model.Strategy;
import com.mpakam.model.StrategyStockQuote;

@Service
public class HeikenAshiStochRsiStrategyService implements IStrategyService{

	final int STRATEGYID=1;
	final BigDecimal periodLength = new BigDecimal(14);
	final int stochPeriodLength = 14;
	final int K_LENGTH=3;
	final int D_LENGTH=3;
	final BigDecimal ZERO= new BigDecimal(0).setScale(4);
	final BigDecimal HUNDRED = new BigDecimal(100).setScale(4);
	final BigDecimal ONE = new BigDecimal(1).setScale(4);
	final MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
	final Strategy s= new Strategy();
	{
		s.setStrategyId(STRATEGYID);
	}
	
	
	
	@Autowired
	StrategyStockQuoteDao strategyStockQuoteDao;
	
	@Override
	public void executeStrategy(Set<StockQuote> quotes) {
		
		LinkedList<BigDecimal> lastNRsi= new LinkedList<BigDecimal>();
		LinkedList<StrategyStockQuote> smaStockRsiList = new LinkedList<StrategyStockQuote>(); 
		int quoteCounter=0; // for initial loads
		TreeSet<StrategyStockQuote> initialRsiQuotes= new TreeSet<StrategyStockQuote>();
		int stockNum =((TreeSet<StockQuote>)quotes).first().getStock().getStocknum();
		LinkedList<StrategyStockQuote> lastStrategyQuotes=strategyStockQuoteDao.retrieveLastXQuotesByStockNumStrategyId(stockNum, STRATEGYID);
		
		StrategyStockQuote lastStrategyQuote = 
				(lastStrategyQuotes == null || lastStrategyQuotes.size()==0)?
						null: lastStrategyQuotes.get(0);
		boolean lastItemExists = lastStrategyQuote!=null;
		if(lastItemExists) {
			int size = lastStrategyQuotes.size();
			for(int i=size-1;i>=0;i--) {
				lastNRsi.addLast(lastStrategyQuotes.get(i).getRsi());
			}
		}
		for (StockQuote stockQuote : quotes) {
			System.out.println("Quote TimeSTamp: "+stockQuote.getQuoteDatetime());
			BigDecimal xOpenPrev = new BigDecimal(0);
			BigDecimal xClosePrev = new BigDecimal(0);
			
			StrategyStockQuote strategyStockQuote = new StrategyStockQuote();
			strategyStockQuote.setStockQuote(stockQuote);			
			strategyStockQuote.setStrategy(s);
			
			if (lastItemExists || lastStrategyQuote != null) {
				xOpenPrev = lastStrategyQuote.getXopen();
				xClosePrev = lastStrategyQuote.getXclose();
				quoteCounter=15; // for continued loads, we just need to start caculating the RSI instead of initial load code
			} else {
				System.out.println("Initial settingup of stock" + stockQuote.getStock().getTicker());
				initialRsiQuotes.add(strategyStockQuote);
				xClosePrev = stockQuote.getClose();
				xOpenPrev = stockQuote.getOpen();
			}
			
			BigDecimal xClose = (stockQuote.getOpen().add(stockQuote.getHigh()).add(stockQuote.getLow()).add(stockQuote.getClose())).divide(new BigDecimal(4));
			BigDecimal xOpen = (xOpenPrev.add(xClosePrev)).divide(new BigDecimal(2));
			BigDecimal xHigh = stockQuote.getHigh().max(xClosePrev.max(xOpenPrev));
			BigDecimal xLow = stockQuote.getLow().min(xClosePrev.min(xOpenPrev));
			
			strategyStockQuote.setXclose(xClose);
			strategyStockQuote.setXopen(xOpen);
			strategyStockQuote.setXhigh(xHigh);
			strategyStockQuote.setXlow(xLow);
			
			if(quoteCounter<14) {
				if(!initialRsiQuotes.isEmpty()) {
					//Collecting first 14 records
					initialRsiQuotes.add(strategyStockQuote);
				}
				quoteCounter++;
			}else if(quoteCounter == 14) {
				quoteCounter++;
				//we now have 14 previous records to calculate the RSI
				initialRsiQuotes.add(strategyStockQuote);
				calculateInitialStochRsi(initialRsiQuotes);
			}else {
				BigDecimal gains=new BigDecimal(0);
				BigDecimal losses=new BigDecimal(0);
				BigDecimal avgUp = lastStrategyQuote.getAvgGain();
				BigDecimal avgDown = lastStrategyQuote.getAvgLoss();
				System.out.println("Before-Avgup" + avgUp +"AvgDown-"+avgDown);
		        BigDecimal change = strategyStockQuote.getXclose().subtract(lastStrategyQuote.getXclose(),mc); 
		        
		        gains=gains.add(ZERO.max(change));
	            losses = losses.add(ZERO.max(change.negate()));
		        
				//avgUp = ((avgUp * (periodLength - 1)) + gains) / (periodLength);
	            avgUp = (avgUp.multiply(periodLength.subtract(ONE)).add(gains)).divide( periodLength, mc);
				//avgDown = ((avgDown * (periodLength - 1)) + losses)/ (periodLength);
				avgDown= (avgDown.multiply(periodLength.subtract(ONE)).add(losses)).divide( periodLength, mc);
				strategyStockQuote.setAvgGain(avgUp);
				strategyStockQuote.setAvgLoss(avgDown);
				//stockQuote.setRsi(new BigDecimal(100 - (100 / (1 + (avgUp / avgDown)))));
				System.out.println("Gains:"+gains + "losses:"+losses+"Change:"+change);
				System.out.println("After-Avgup" + avgUp +"AvgDown-"+avgDown);
				if(!(avgDown.compareTo(ZERO) == 0))
					strategyStockQuote.setRsi(HUNDRED.subtract(HUNDRED.divide(ONE.add(avgUp.divide(avgDown, mc)),mc)));
				else
					strategyStockQuote.setRsi(ZERO);
					
				if(strategyStockQuote.getRsi() != null) {
					addRsiToList(lastNRsi, strategyStockQuote.getRsi());
					strategyStockQuote.setHighrsi(getMaxRSIn(lastNRsi, strategyStockQuote.getRsi()));
					strategyStockQuote.setLowrsi(getMinRSIn(lastNRsi, strategyStockQuote.getRsi()));
//					strategyStockQuote.setStochRsi(calculateStochRsi(strategyStockQuote.getLowrsi(),strategyStockQuote.getHighrsi(),strategyStockQuote.getRsi()));
					calculateStochRsi(strategyStockQuote,lastStrategyQuotes);
					addStochRsiToList(strategyStockQuote,lastStrategyQuotes,smaStockRsiList);
				}
				
//				determineTrend(stockQuote,lastItem);
//				sendAlert(stockQuote);//TODO: uncomment
				System.out.println("AvgUp:"+ avgUp +" AvgDown:"+avgDown+" RsiK:"+strategyStockQuote.getStochRsiK() +" RsiD:"+strategyStockQuote.getStochRsiD() );
			}
			lastStrategyQuote = strategyStockQuote;
			strategyStockQuote.setStrategyStockQuoteId((int)strategyStockQuoteDao.save(strategyStockQuote));
		}
	}
	
	private void addStochRsiToList(StrategyStockQuote currentStrategyQuote, LinkedList<StrategyStockQuote> lastStrategyQuotes, LinkedList<StrategyStockQuote> lastStochRsi) {
		
		if(lastStrategyQuotes != null && lastStrategyQuotes.size() > 0 && lastStochRsi.size()<2) {
				lastStochRsi.addLast(lastStrategyQuotes.get(1));
				lastStochRsi.addLast(lastStrategyQuotes.get(0));
		}
		
		if(lastStochRsi.size() >= K_LENGTH) {
			lastStochRsi.removeFirst();
		}
		
		lastStochRsi.addLast(currentStrategyQuote);
		BigDecimal rawK=new BigDecimal(0);
		for(int i=0;i<lastStochRsi.size();i++ ) {
			rawK = rawK.add(lastStochRsi.get(i).getStochRsi());
			System.out.println(i+" RawK is" + rawK);
		}
		currentStrategyQuote.setStochRsiK(rawK.divide(new BigDecimal(lastStochRsi.size()),mc));
		System.out.println("RStoch Size is"+lastStochRsi.size()+"Smoothed Stoch RSIK is" + currentStrategyQuote.getStochRsiK());
		BigDecimal smoothedD = new BigDecimal(0);
		for(int i=0;i<lastStochRsi.size();i++ ) {
			smoothedD = smoothedD.add(lastStochRsi.get(i).getStochRsiK());
		}
		currentStrategyQuote.setStochRsiD(smoothedD.divide(new BigDecimal(lastStochRsi.size()),mc));
		System.out.println(" StockRsiD is" + currentStrategyQuote.getStochRsiD());
	}

	/* (non-Javadoc)
	 * @see com.mpakam.service.StockQuoteService#calculateHeikenAshi(java.util.List)
	 */
	/*
	private void sendAlert(StockQuote stockQuote) {
		int trend = stockQuote.getTrend();
		if(trend != 1 & trend != 1)
			return;
		
		List<CustomerTickerTracker> customerTickers = 
				customerTkrDao.findByTicker(stockQuote.getStock().getStocknum());
		customerTickers.forEach(c->{
			System.out.println("STock alert will be sent");
			//emailService.sendMail(c.getCustomer(), stockQuote);
		});
	}*/
	
	public void calculateInitialStochRsi(TreeSet<StrategyStockQuote> quotesList) {		
		StrategyStockQuote[] quotesArray=(StrategyStockQuote[]) quotesList.toArray(new StrategyStockQuote[quotesList.size()]);
		BigDecimal gains=new BigDecimal(0);
		gains.setScale(6);
		BigDecimal losses=new BigDecimal(0);
		losses.setScale(6);
		BigDecimal avgUp = new BigDecimal(0);
		BigDecimal avgDown = new BigDecimal(0);
		
		for (int bar = 0; bar <14; bar++) {
            BigDecimal change = quotesArray[bar+1].getXclose().subtract(quotesArray[bar].getXclose());
            gains=gains.add(ZERO.max(change));
            losses = losses.add(ZERO.max(change.negate()));
//            System.out.println("Initial load Gains:"+gains + " losses:"+losses+" Change:"+change);
        }
		avgUp = gains.divide(periodLength, mc);
        avgDown = losses.divide(periodLength, mc);
        //quotesList.get(quotesList.size()-1).setAvgGain(avgUp);
        quotesList.last().setAvgGain(avgUp);
        quotesList.last().setAvgLoss(avgDown);
        //stockQuote.setRsi(new BigDecimal(100 - (100 / (1 + (avgUp / avgDown)))));
        quotesList.last().setRsi(HUNDRED.subtract(HUNDRED.divide(ONE.add(avgUp.divide(avgDown, mc)), mc)));
        System.out.println("AvgUp:"+ avgUp +" AvgDown:"+avgDown+" Rsi:"+quotesList.last().getRsi());
	}
	
	private BigDecimal getMinRSIn(LinkedList<BigDecimal> minRSIn, BigDecimal currentRsi) {
		BigDecimal minRsin=currentRsi;
		
		for(BigDecimal d: minRSIn) {
			minRsin=minRsin.min(d);
		}
		System.out.println("MinRSI is " + minRsin);
		return minRsin;
	}
	
	private BigDecimal getMaxRSIn(LinkedList<BigDecimal> maxRSIn, BigDecimal currentRsi) {
		BigDecimal maxRsin=currentRsi;
		for(BigDecimal d: maxRSIn) {
			maxRsin=maxRsin.max(d);
		}
		System.out.println("MaxRSI is " + maxRsin);
		return maxRsin;
	}
	private void addRsiToList(LinkedList<BigDecimal> lastNRsi, BigDecimal rsi) {
		if(lastNRsi.size() >=stochPeriodLength) {
			lastNRsi.removeFirst();
		}
		lastNRsi.addLast(rsi);
	}
	
	/*
	 * Read more: StochRSI https://www.investopedia.com/terms/s/stochrsi.asp#ixzz55BlKNVzg 
	 */
	private void calculateStochRsi(StrategyStockQuote quote, LinkedList<StrategyStockQuote> quotes) {
			//BigDecimal minRsi,BigDecimal maxRsi, BigDecimal currentRsi) {
		//StochRSI = (RSI - Lowest Low RSI) / (Highest High RSI - Lowest Low RSI)
		BigDecimal maxRsi= quote.getHighrsi();
		BigDecimal minRsi= quote.getLowrsi();
		BigDecimal currentRsi= quote.getRsi();
		BigDecimal stochRsiRawK =(!maxRsi.equals(minRsi))? ((currentRsi.subtract(minRsi))
								.divide(maxRsi.subtract(minRsi), mc)).multiply(HUNDRED, mc):new BigDecimal(0);
		System.out.println("MaxRsi-" +maxRsi +"MinRsi-"+minRsi +"currentRsi-"+currentRsi +"stochRsiRawK-"+stochRsiRawK);
		quote.setStochRsi(stochRsiRawK);
	}
	
	/*
	private void determineTrend(StockQuote currentItem, StockQuote lastItem) {
		double stochRsi=(lastItem!=null && lastItem.getStochRsi() != null)?lastItem.getStochRsi().doubleValue():50;
		if(stochRsi <25 && 
				lastItem.getXopen().doubleValue() < lastItem.getXclose().doubleValue() &&
				currentItem.getXopen().doubleValue() < currentItem.getXclose().doubleValue()) {
			currentItem.setTrend(1);
		}else if(stochRsi >75 && 
				lastItem.getXopen().doubleValue() > lastItem.getXclose().doubleValue() &&
				currentItem.getXopen().doubleValue() > currentItem.getXclose().doubleValue()) {
			currentItem.setTrend(-1);
		}
	}
	*/	
}
