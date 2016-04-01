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
		Thread.sleep(5000L);
	}

	public ExchangeRateClient(String urlStr) throws MalformedURLException {
		theClient = this;
		wsdlUrl = urlStr;
		URL url = new URL(wsdlUrl);
		QName qname = new QName("http://async.ws.com/", "ExchangeRateService");
		ExchangeRateService exchangeRateService = new ExchangeRateService(url, qname);
		excRate = exchangeRateService.getExchangeRatePort();

		// synchronous:
		System.out.println("Airstrip One / Ganymede exchange rate, retrieved synchronously, is: " + excRate.getExchangeRate("AS1", "GMD"));

		// asynchronous with polling:
		try {
			Response<GetExchangeRateResponse> response = excRate.getExchangeRateAsync("AS1", "GMD");
			Thread.sleep(2000L);
			GetExchangeRateResponse output = response.get();
			System.out.println("--> retrieved via polling: " + output.getReturn());
		} catch (Exception exc) {
			System.out.println(exc.getClass().getName() + " polling for response: " + exc.getMessage());
		}

		// asynchronous with callback:
		excRate.getExchangeRateAsync("AS1", "GMD", new AsyncHandler<GetExchangeRateResponse>() {
			public void handleResponse(Response<GetExchangeRateResponse> response) {
				System.out.println("In AsyncHandler");
				try {
					theClient.setCurrencyExchangeRate(response.get().getReturn());
				} catch (Exception exc) {
					System.out.println(exc.getClass().getName() + " using callback for response:" + exc.getMessage());
				}
			}
		});
	}

	protected void setCurrencyExchangeRate(double newRate) {
		rate = newRate;
		System.out.println("--> via callback, updated exchange rate to " + rate);
	}
}
