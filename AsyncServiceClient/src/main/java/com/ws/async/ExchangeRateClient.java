package com.ws.async;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import com.ws.async.generated.ExchangeRate;
import com.ws.async.generated.ExchangeRateService;
import com.ws.async.generated.GetExchangeRateResponse;

public class ExchangeRateClient {
	protected ExchangeRateClient	theClient	= null;
	protected String				wsdlUrl		= null;
	protected double				rate		= 0.0d;
	ExchangeRate					excRate		= null;

	public static void main(String args[]) throws MalformedURLException, InterruptedException {
		if (args.length != 1) {
			System.out.println("Usage java -cp <jarFile> com.ws.async.ExchangeRateClient serviceWsdlUrl");
			System.exit(-1);
		}
		ExchangeRateClient client = new ExchangeRateClient(args[0]);
		Thread.sleep(50000L);
	}

	public ExchangeRateClient(String urlStr) throws MalformedURLException {
		theClient = this;
		wsdlUrl = urlStr;
		URL url = new URL(wsdlUrl);
		QName qname = new QName("http://async.ws.com/", "ExchangeRateService");
		ExchangeRateService exchangeRateService = new ExchangeRateService(url, qname);
		excRate = exchangeRateService.getExchangeRatePort();

		// synchronous:
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("░▒▓ »»» Synchronous Call              ▓▒░");
		System.out.println("░▒▓ Retrieved synchronously, is: " + excRate.getExchangeRate("AS1", "GMD") + " ▓▒░");
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("\n\n");

		// asynchronous with polling:
		try {
			Response<GetExchangeRateResponse> response = excRate.getExchangeRateAsync("AS1", "GMD");
			    System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			    System.out.println("░▒▓ »»» Polling Call                                                                         ▓▒░");
			int count = 0;    
			while ( !response.isDone() ) {
				Thread.sleep(2000L);
				System.out.println("░▒▓ --> Waiting 2 secs then ask if the response is ready. I could be doing something else. " + (++count) + " ▓▒░");
			}
			GetExchangeRateResponse output = response.get();
			System.out.println("░▒▓ --> retrieved via polling: " + output.getReturn() + "                                                          ▓▒░");
			System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			System.out.println("\n\n");
		} catch (Exception exc) {
			System.out.println(exc.getClass().getName() + " polling for response: " + exc.getMessage());
		}

		// asynchronous with callback:
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("░▒▓ »»» CallBack Call                                                ▓▒░");
		excRate.getExchangeRateAsync("AS1", "GMD", new AsyncHandler<GetExchangeRateResponse>() {
			public void handleResponse(Response<GetExchangeRateResponse> response) {
				try {
					theClient.setCurrencyExchangeRate(response.get().getReturn());
				} catch (Exception exc) {
					System.out.println(exc.getClass().getName() + " using callback for response:" + exc.getMessage());
				}
			}
		});
		try {
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...   1  ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...   2  ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...   3  ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...   4  ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...   5  ▓▒░");
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected void setCurrencyExchangeRate(double newRate) {
		rate = newRate;
		System.out.println("░▒▓ --> via callback, updated exchange rate to " + rate + "                  ▓▒░");
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("\n\n");
	}
}
