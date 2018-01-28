package com.mpakam.dao;

import java.util.List;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mpakam.model.StockQuote;

@Repository
public class StockQuoteDaoImp implements StockQuoteDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	public int save(StockQuote stockQuote) {
		return (int) sessionFactory.getCurrentSession().save(stockQuote);		
	}

	@Override
	public TreeSet<StockQuote> findAllByStock(Long stockNum) {
		String queryString = "FROM Stock WHERE stocknum = :stocknum";
		return new TreeSet<StockQuote>((List<StockQuote>) sessionFactory.getCurrentSession()
								.createQuery(queryString)
								.setParameter("stocknum", stockNum)
								.list());
	}

	@Override
	public void update(StockQuote stockQuote) {
		sessionFactory.getCurrentSession().update(stockQuote);
		return;
	}

	@Override
	public void save(TreeSet<StockQuote> stockQuoteList) {
		/*stockQuoteList.sort(new Comparator<StockQuote>() {
			@Override
			public int compare(StockQuote o1, StockQuote o2) {
				return o1.getQuoteDatetime().compareTo(o2.getQuoteDatetime());
			}
		});*/
		stockQuoteList.forEach(p->save(p));
	}

	@Transactional
	@Override
	public StockQuote findLastStockQuote(int stockNum) {
		String queryString = "FROM StockQuote WHERE stocknum = :stocknum order by quoteDatetime desc";
		return (StockQuote) sessionFactory.getCurrentSession()
								.createQuery(queryString)
								.setMaxResults(1)
								.setParameter("stocknum", stockNum)
								.uniqueResult();
	}

}
