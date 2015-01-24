package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.FlightReader;
import it.polito.dp2.FDS.FlightMonitorException;
import it.polito.dp2.FDS.Time;

public class Flight implements FlightReader{

	private String departureAirport;
	private Time departureTime;
	private String destinationAirport;
	private String number;
	
	public Flight(String departureAirport, Time departureTime, String destinationAirport, String number) throws FlightMonitorException
	{
		InputValidator validator = new InputValidator();
		if (validator.validateFlightNumber(number) == true)
			this.number = number;
		else
			throw new FlightMonitorException("Invalid flight number");
		
		if (validator.validateAirport(destinationAirport) == true)
			this.destinationAirport = destinationAirport;
		else
			throw new FlightMonitorException("Invalid destination airport");
		
		if (validator.validateAirport(departureAirport) == true)
			this.departureAirport = departureAirport;
		else
			throw new FlightMonitorException("Invalid departure airport");
		
		if ((departureTime != null) && (validator.validateTime(departureTime) == true))
			this.departureTime = departureTime;
		else
			throw new FlightMonitorException("Invalid departure time");
	}
	
	public Time getDepartureTime()
	{
		return departureTime;
	}
	
	public String getNumber()
	{
		return number;
	}
	
	public String getDepartureAirport()
	{
		return departureAirport;
	}
	
	public String getDestinationAirport()
	{
		return destinationAirport;
	}
}
