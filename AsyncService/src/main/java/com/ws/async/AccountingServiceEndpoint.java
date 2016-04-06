package com.ws.async;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Endpoint;

import com.ws.async.generated.AccountingService;
import com.ws.async.generated.FaultAccountingService;
import com.ws.async.generated.FaultAccountingService_Exception;

@WebService(serviceName = "AccountingService", portName = "AccountingServicePort", endpointInterface = "com.ws.async.generated.AccountingService")
public class AccountingServiceEndpoint implements AccountingService {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java -cp <jarFile> com.ws.async.AccountingServiceEndpoint http://localhost:8282/accountingService");
			System.exit(-1);
		}
		AccountingServiceEndpoint wsInstance = new AccountingServiceEndpoint();
		Endpoint.publish(args[0], wsInstance);
		System.out.println("░▒▓▓▓██▓▒▒░░");
		System.out.println("░▒▓▓▓██▓ »»»» Published Endpoint at URL " + args[0]);
		System.out.println("░▒▓▓▓██▓▒▒░░\n\n");
	}

	@WebMethod
	@Override
	public double getPeriodTotalExpenses(XMLGregorianCalendar startDate, XMLGregorianCalendar endDate) throws FaultAccountingService_Exception {
		double result = 0;
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("░▒▓ »»» Start transaction            ▓▒░");
		System.out.println("░▒▓ Wait 15 seconds, calculating...  ▓▒░");
		try {
			LocalDate localDateStartDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.getDay());
			LocalDate localDateEndDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.getDay());

			if (localDateEndDate.isBefore(localDateStartDate)) {
				FaultAccountingService faultInfo = new FaultAccountingService();
				faultInfo.setCode(1);
				faultInfo.setDescription("The End Date is not after Start Date");
				System.out.println("░▒▓ Exception thrown!!!              ▓▒░");
				throw new FaultAccountingService_Exception("Problems with Period information", faultInfo);
			} else {
				long days = ChronoUnit.DAYS.between(localDateStartDate, localDateEndDate);
				result = 2.85 * days;
				// Simulates 15 seconds of processing
				Thread.sleep(15000);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		System.out.println("░▒▓ Answering now...                 ▓▒░");
		System.out.println("░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░");
		System.out.println("\n");

		return result;
	}
}
