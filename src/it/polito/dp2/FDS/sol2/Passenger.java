package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.FlightMonitorException;

public class Passenger {
	
	private String name;
	private boolean boarded;
	private String seat;
	private InputValidator validator;
	
	public Passenger(String name, boolean boarded, String seat) throws FlightMonitorException
	{
		validator = new InputValidator();
		
		if (validator.validatePassengerName(name) == true)
			this.name=name;
		else
			throw new FlightMonitorException("Invalid passenger name");
		
		this.boarded=boarded;
		
		if (((seat == null) || (seat.isEmpty())) && (boarded == true) && (validator.validateSeat(seat) == false))
			throw new FlightMonitorException("Invalid seat");
		else
			this.seat = seat;
			
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getSeat()
	{
		return seat;
	}
	
	public boolean isBoarded()
	{
		return boarded;
	}
}
