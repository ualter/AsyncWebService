package com.ws.async;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import com.ws.async.generated.ExchangeRate;

@WebService(serviceName = "ExchangeRateService", portName = "ExchangeRatePort", endpointInterface = "com.ws.async.generated.ExchangeRate")
public class ExchangeRateEndpoint implements ExchangeRate {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java -cp <jarFile> com.ws.async.ExchangeRateEndpoint publishURL");
			System.exit(-1);
		}
		ExchangeRateEndpoint wsInstance = new ExchangeRateEndpoint();
		Endpoint.publish(args[0], wsInstance);
		System.out.println("░▒▓▓▓██▓▒▒░░");
		System.out.println("░▒▓▓▓██▓ »»»» Published endpoint at URL " + args[0]);
		System.out.println("░▒▓▓▓██▓▒▒░░\n\n");
	}

	@WebMethod
	public double getExchangeRate(String fromCurrency, String toCurrency) {
		
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("░▒▓ »»» Start transaction            ▓▒░");
		System.out.println("░▒▓ Waiting 15 seconds before answer ▓▒░");
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		System.out.println("░▒▓ Answering now...                 ▓▒░");
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("\n");
		
		if (fromCurrency.equals("AS1") && toCurrency.equals("GMD")) {
			return 2.78;
		} else {
			return 0.0;
		}
	}
}
