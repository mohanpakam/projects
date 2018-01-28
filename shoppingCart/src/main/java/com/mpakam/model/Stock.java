package com.mpakam.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the stocks database table.
 * 
 */
@Entity
@Table(name="stocks")
@NamedQuery(name="Stock.findAll", query="SELECT s FROM Stock s")
public class Stock implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private int stocknum;

	private int interval;

	@Column(name="stock_name", nullable=false, length=256)
	private String stockName;

	@Column(nullable=false, length=20)
	private String ticker;

	//bi-directional many-to-one association to CustomerTickerTracker
	@OneToMany(mappedBy="stock")
	private Set<CustomerTickerTracker> customerTickerTrackers;

	//bi-directional many-to-one association to StockAlert
	@OneToMany(mappedBy="stock")
	private Set<StockAlert> stockAlerts;

	//bi-directional many-to-one association to StockQuote
	@OneToMany(mappedBy="stock")
	private Set<StockQuote> stockQuotes;

	public Stock() {
	}

	public int getStocknum() {
		return this.stocknum;
	}

	public void setStocknum(int stocknum) {
		this.stocknum = stocknum;
	}

	public int getInterval() {
		return this.interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getStockName() {
		return this.stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getTicker() {
		return this.ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public Set<CustomerTickerTracker> getCustomerTickerTrackers() {
		return this.customerTickerTrackers;
	}

	public void setCustomerTickerTrackers(Set<CustomerTickerTracker> customerTickerTrackers) {
		this.customerTickerTrackers = customerTickerTrackers;
	}

	public CustomerTickerTracker addCustomerTickerTracker(CustomerTickerTracker customerTickerTracker) {
		getCustomerTickerTrackers().add(customerTickerTracker);
		customerTickerTracker.setStock(this);

		return customerTickerTracker;
	}

	public CustomerTickerTracker removeCustomerTickerTracker(CustomerTickerTracker customerTickerTracker) {
		getCustomerTickerTrackers().remove(customerTickerTracker);
		customerTickerTracker.setStock(null);

		return customerTickerTracker;
	}

	public Set<StockAlert> getStockAlerts() {
		return this.stockAlerts;
	}

	public void setStockAlerts(Set<StockAlert> stockAlerts) {
		this.stockAlerts = stockAlerts;
	}

	public StockAlert addStockAlert(StockAlert stockAlert) {
		getStockAlerts().add(stockAlert);
		stockAlert.setStock(this);

		return stockAlert;
	}

	public StockAlert removeStockAlert(StockAlert stockAlert) {
		getStockAlerts().remove(stockAlert);
		stockAlert.setStock(null);

		return stockAlert;
	}

	public Set<StockQuote> getStockQuotes() {
		return this.stockQuotes;
	}

	public void setStockQuotes(Set<StockQuote> stockQuotes) {
		this.stockQuotes = stockQuotes;
	}

	public StockQuote addStockQuote(StockQuote stockQuote) {
		getStockQuotes().add(stockQuote);
		stockQuote.setStock(this);

		return stockQuote;
	}

	public StockQuote removeStockQuote(StockQuote stockQuote) {
		getStockQuotes().remove(stockQuote);
		stockQuote.setStock(null);

		return stockQuote;
	}

}