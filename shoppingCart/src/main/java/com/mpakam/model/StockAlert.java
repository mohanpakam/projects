package com.mpakam.model;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the stock_alerts database table.
 * 
 */
@Entity
@Table(name="stock_alerts")
@NamedQuery(name="StockAlert.findAll", query="SELECT s FROM StockAlert s")
public class StockAlert implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="stock_alert_id", unique=true, nullable=false)
	private int stockAlertId;

	@Column(name="buy_sell_signal")
	private int buySellSignal;

	@Column(name="stock_price", precision=10, scale=4)
	private BigDecimal stockPrice;

	//bi-directional many-to-one association to Customer
	@ManyToOne
	@JoinColumn(name="customerid")
	private Customer customer;

	//bi-directional many-to-one association to Stock
	@ManyToOne
	@JoinColumn(name="stocknum", nullable=false)
	private Stock stock;

	//bi-directional many-to-one association to StrategyStockQuote
	@ManyToOne
	@JoinColumn(name="strategy_stock_quote_id")
	private StrategyStockQuote strategyStockQuote;

	public StockAlert() {
	}

	public int getStockAlertId() {
		return this.stockAlertId;
	}

	public void setStockAlertId(int stockAlertId) {
		this.stockAlertId = stockAlertId;
	}

	public int getBuySellSignal() {
		return this.buySellSignal;
	}

	public void setBuySellSignal(int buySellSignal) {
		this.buySellSignal = buySellSignal;
	}

	public BigDecimal getStockPrice() {
		return this.stockPrice;
	}

	public void setStockPrice(BigDecimal stockPrice) {
		this.stockPrice = stockPrice;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Stock getStock() {
		return this.stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public StrategyStockQuote getStrategyStockQuote() {
		return this.strategyStockQuote;
	}

	public void setStrategyStockQuote(StrategyStockQuote strategyStockQuote) {
		this.strategyStockQuote = strategyStockQuote;
	}

}