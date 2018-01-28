package com.mpakam.dao;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mpakam.model.Stock;

@Repository
public class StockDaoImpl implements StockDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	@Override
	public Stock findBy(int stocknum) {
		String queryString = "FROM Stock WHERE stocknum = :stocknum";
		return (Stock) sessionFactory.getCurrentSession()
								.createQuery(queryString)
								.setParameter("stocknum", stocknum)
								.uniqueResult();
	}

	@Override
	public Long save(Stock stock) {
		return (Long) sessionFactory.getCurrentSession().save(stock);
		
	}

	@Override
	public void update(Stock stock) {
		sessionFactory.getCurrentSession().update(stock);
		return;
	}


}
