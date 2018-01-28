package com.mpakam.controller;

import java.util.LinkedList;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mpakam.dao.CustomerTickerTrackerDao;
import com.mpakam.dao.StockDao;
import com.mpakam.dao.StrategyDao;
import com.mpakam.dao.StrategyStockQuoteDao;
import com.mpakam.model.CustomerTickerTracker;
import com.mpakam.model.StrategyStockQuote;
import com.mpakam.service.IStockQuoteService;
import com.mpakam.util.IQuoteDataProviderService;
import com.mpakam.util.UIToolsService;

@Controller
public class StockQuoteController {
	
	@Autowired
	IQuoteDataProviderService quoteProvider;
	
	@Autowired
	IStockQuoteService quoteService;
	
	@Autowired
	StockDao stockDao;
	
	@Autowired
	UIToolsService uiSvc;
	
	@Autowired
	StrategyDao strategyDao;
	
	@Autowired
	CustomerTickerTrackerDao trackerDao;
	
	@Autowired
	StrategyStockQuoteDao strategyQuotesDao;
	
	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	/*@RequestMapping(value="/stockQuote/{symbl}")
	public @ResponseBody ResponseEntity<TreeSet<StockQuote>> retreiveStockQuote(@PathVariable("symbl") int symbolNum) throws Exception{
		Stock stock = stockDao.findBy(symbolNum);
		TreeSet<StockQuote> quoteList =quoteService.syncBySymbol(stock);
		return new ResponseEntity<TreeSet<StockQuote>> (quoteList,HttpStatus.FOUND);
	}

	@RequestMapping(value = "/users/login", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Customer> login(@RequestParam("username") String username, @RequestParam("password") String password) 
			throws NoSuchAlgorithmException, AuthenticationFailedException {
		Customer customer = customerService.authentication(username, password);
		return new ResponseEntity<Customer> (customer, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Void> addCustomer(@RequestBody Customer customer, HttpServletRequest request) 
			throws URISyntaxException, NoSuchAlgorithmException {
		Long id = customerService.addCustomer(customer);
		HttpHeaders header = new HttpHeaders();
		header.setLocation(new URI(request.getRequestURL() + "/" + id.toString()));
		return new ResponseEntity<Void>(header, HttpStatus.CREATED);
	}*/
	
	@Transactional //TODO: Not the best place
	@RequestMapping("/heikenAshi")
    public String heikenAshiChart(@RequestParam(value="custTrackerId", required=true) int custTrackerId, Model model) throws Exception {
		
//		Strategy stratagy = strategyDao.findBy(strategyId);
		CustomerTickerTracker custTrkr = trackerDao.findById(custTrackerId);
		
		LinkedList<StrategyStockQuote> quoteList = 
				strategyQuotesDao.retrieveQuotesByStockNumStrategyId(custTrkr.getStock().getStocknum(),
						custTrkr.getStrategyId());
		
		model.addAttribute("quotes", uiSvc.convertToHACandleData(quoteList));
		model.addAttribute("rsidata", uiSvc.stochRsiFromHA(quoteList));
//		model.addAttribute("rsidata", rsiTmp);
        return "heikenAshiChart";
    }
	
}
