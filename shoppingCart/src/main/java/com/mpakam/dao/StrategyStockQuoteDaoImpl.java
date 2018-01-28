package com.mpakam.dao;

import java.util.LinkedList;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mpakam.model.StrategyStockQuote;
import com.mpakam.util.ListUtil;

@Repository
public class StrategyStockQuoteDaoImpl  extends AbstractGenericDao<StrategyStockQuote> implements StrategyStockQuoteDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public LinkedList<StrategyStockQuote> retrieveLastXQuotesByStockNumStrategyId(int stockNum, int strategyNum) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery("StrategyStockQuote.getTopXByStockNum")
				.setInteger("strategyId", strategyNum)
				.setInteger("stockNum", stockNum);
//		return  ListUtil.mapInstance((ArrayList<StrategyStockQuote>)query.list());
		return  (LinkedList<StrategyStockQuote>)ListUtil.getLinkedListInstance(query.list());
	}
	
	@Override
	public LinkedList<StrategyStockQuote> retrieveQuotesByStockNumStrategyId(int stockNum, int strategyNum) {
		Query query = sessionFactory.getCurrentSession().getNamedQuery("StrategyStockQuote.getAllByStockNumStrategyId")
				.setInteger("strategyId", strategyNum)
				.setInteger("stockNum", stockNum);
//		return  ListUtil.mapInstance((ArrayList<StrategyStockQuote>)query.list());
		LinkedList<StrategyStockQuote> list=(LinkedList<StrategyStockQuote>)ListUtil.getLinkedListInstance(query.list());
		list.sort(( StrategyStockQuote o1,  StrategyStockQuote o2)->o1.getStrategyStockQuoteId() - o2.getStrategyStockQuoteId());

		return  list;
	}

}
