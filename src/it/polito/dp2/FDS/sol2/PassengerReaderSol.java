package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.PassengerReader;

public class PassengerReaderSol implements PassengerReader{
	
	private boolean boarded;
	private String name;
	private String seat;
	private FlightInstanceReader flightInstance;
	
	
	public PassengerReaderSol(boolean boarded, String name, String seat, FlightInstanceReader flightInstance)
	{
		//Each passenger has been already validated by Passenger.java
		// Passenger Reader is instantiated by the method "getPassenger" of FlightInstance class
		this.boarded=boarded;
		this.name=name;
		this.seat=seat;
		this.flightInstance=flightInstance;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getSeat()
	{
		return seat;
	}
	
	public boolean boarded()
	{
		return boarded;
	}

	public FlightInstanceReader getFlightInstance()
	{
		return flightInstance;
	}
}
