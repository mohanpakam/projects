package com.mpakam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mpakam.model.Customer;
import com.mpakam.model.StockQuote;

@Service
public class EmailService {
/*
//	@Autowired
	private JavaMailSender javaMailSender;


	public void sendMail(Customer cust, StockQuote quote) {

		System.out.println("Sending email...");

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(cust.getEmailId());
		message.setFrom("trade.alerts4u@gmail.com");
		message.setSubject(quote.getTrend()== -1?"SELL ":"BUY " + 
						quote.getStock().getTicker() + " @ " +quote.getClose().doubleValue());
		message.setText("Identified on " + quote.getQuoteDatetime() +"\t" +
						"Stoch RSI: " + quote.getStochRsi() +"\t" +
						"Trend: " +quote.getTrend() + "\t"
				);
		javaMailSender.send(message);

		System.out.println("Email Sent!");
	}
*/
}