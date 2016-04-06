package com.ws.async;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.commons.lang.StringUtils;

import com.ws.async.generated.AccountingService;
import com.ws.async.generated.AccountingService_Service;
import com.ws.async.generated.FaultAccountingServiceException;
import com.ws.async.generated.GetPeriodTotalExpensesResponse;

public class AccountingServiceClient {
	protected AccountingServiceClient	theClient			= null;
	protected String					wsdlUrl				= null;
	protected double					rate				= 0.0d;
	private DecimalFormat				df					= new DecimalFormat("###,###0.00");
	private AccountingService			accountingService	= null;

	public static void main(String args[]) throws MalformedURLException, InterruptedException {
		if (args.length != 1) {
			System.out.println("Usage java -cp <jarFile> com.ws.async.AccountingServiceEndpoint http://localhost:8282/accountingService");
			System.exit(-1);
		}
		AccountingServiceClient client = new AccountingServiceClient(args[0]);
		Thread.sleep(50000L);
	}

	public AccountingServiceClient(String urlStr) throws MalformedURLException {

		DatatypeFactory dataTypeFactory;
		try {
			dataTypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e1) {
			throw new RuntimeException(e1.getMessage(), e1);
		}
		XMLGregorianCalendar startDate = dataTypeFactory.newXMLGregorianCalendarDate(2016, 1, 1, DatatypeConstants.FIELD_UNDEFINED);
		XMLGregorianCalendar endDate = dataTypeFactory.newXMLGregorianCalendarDate(2016, 12, 1, DatatypeConstants.FIELD_UNDEFINED);

		theClient = this;
		wsdlUrl = urlStr;
		URL url = new URL(wsdlUrl);
		QName qname = new QName("http://async.ws.com/", "AccountingService");
		AccountingService_Service accountingService_service = new AccountingService_Service(url, qname);
		accountingService = accountingService_service.getAccountingServicePort();

		try {
			// synchronous:
			System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			System.out.println("░▒▓ »»» Synchronous Call                 ▓▒░");
			System.out.println("░▒▓ Retrieved synchronously, is: " + formatNumber(accountingService.getPeriodTotalExpenses(startDate, endDate)) + " ▓▒░");
			System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			System.out.println("\n\n");
		} catch (FaultAccountingServiceException fault) {
			System.out.println("Dear user, please check the error: " + fault.getFaultInfo().getCode() + "-" + fault.getFaultInfo().getDescription());
			throw new RuntimeException(fault.getMessage());
		}

		// asynchronous with polling:
		try {
			Response<GetPeriodTotalExpensesResponse> response = accountingService.getPeriodTotalExpensesAsync(startDate, endDate);
			System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			System.out.println("░▒▓ »»» Polling Call                                                                           ▓▒░");
			int count = 0;
			while (!response.isDone()) {
				Thread.sleep(2000L);
				System.out.println("░▒▓ --> Waiting 2 secs then ask if the response is ready. I could be doing something else.  " + (++count) + "  ▓▒░");
			}
			GetPeriodTotalExpensesResponse output = response.get();
			System.out.println(
					"░▒▓ --> retrieved via polling: " + output.getTotalExpensesPeriod() + "                                                          ▓▒░");
			System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
			System.out.println("\n\n");
		} catch (Exception exc) {
			System.out.println(exc.getClass().getName() + " polling for response: " + exc.getMessage());
		}

		// asynchronous with callback:
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("░▒▓ »»» CallBack Call                                                  ▓▒░");
		accountingService.getPeriodTotalExpensesAsync(startDate, endDate, new AsyncHandler<GetPeriodTotalExpensesResponse>() {
			public void handleResponse(Response<GetPeriodTotalExpensesResponse> response) {
				try {
					theClient.setTotalExpensesPeriod(response.get().getTotalExpensesPeriod());
				} catch (Exception exc) {
					System.out.println(exc.getClass().getName() + " using callback for response:" + exc.getMessage());
				}
			}
		});
		try {
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...    1   ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...    2   ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...    3   ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...    4   ▓▒░");
			Thread.sleep(3000);
			System.out.println("░▒▓ While no answer...  I will do something else in meantime...    5   ▓▒░");
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected void setTotalExpensesPeriod(double totalExpensesPeriod) {
		System.out.println("░▒▓ --> via callback, total expenses in period " + totalExpensesPeriod + "                  ▓▒░");
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("\n\n");
	}

	private String formatNumber(double value) {
		String number = df.format(value);
		return StringUtils.rightPad(number, 7);
	}
}
