package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.Aircraft;
import it.polito.dp2.FDS.FlightInstanceStatus;
import it.polito.dp2.FDS.Time;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

public class InputValidator {
	
	//private static final String TIME24HOURS_PATTERN = "([1]?[0-9]|2[0-3]):[0-5][0-9]";
	private static final String FLIGHTID_PATTERN = "^[A-Z]{2}[0-9]{1,4}$"; //two char and three numbers
	private static final String	AIRPORT_PATTERN = "^[A-Z]{3}$"; //three letters
	private static final String DELAY_PATTERN = "\\d+"; //numbers only
	private static final String SEAT_PATTERN = "^[0-9]{1,}[A-Z]{1}$"; //at least one digit and only one letter
	
	public boolean validateFile(String filename)
	{
		File f=new File(filename);
		if(f.exists() && !f.isDirectory())
			return true;
		else
			return false;
	}
	
	public boolean validateFlightNumber(String flightID)
	{
		if ((flightID==null) || (flightID.isEmpty()))
			return false;
		return flightID.matches(FLIGHTID_PATTERN);
	}
	
	public boolean validateDate(GregorianCalendar gc)
	{
		if (gc==null)
			return false;
		
		try
		{
			Date date = gc.getTime();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy z");
		    df.format(date);
		}catch (IllegalArgumentException e) {
			return false;
		}catch (Exception e)
		{
			return false;
		}
		
		return true;
	}
	
/*
	public boolean validateHour(String time)
	{
		return time.matches(TIME24HOURS_PATTERN);
	}
*/
	
	public boolean validateAirport(String airportCode)
	{
		if ((airportCode==null)||(airportCode.isEmpty()))
			return false;
		return airportCode.matches(AIRPORT_PATTERN);
	}
	
	public boolean validateDelay(int delay)
	{
		String d = String.valueOf(delay);
		return d.matches(DELAY_PATTERN);
	}
	
	public boolean validateStatus(FlightInstanceStatus Status)
	{
		if (Status == null)
			return false;
		
		String status = Status.toString();
		
		if (status.equals("ARRIVED"))
			return true;
		if (status.equals("BOARDING"))
			return true;
		if (status.equals("BOOKING"))
			return true;
		if (status.equals("CANCELLED"))
			return true;
		if (status.equals("CHECKINGIN"))
			return true;
		if (status.equals("DEPARTED"))
			return true;
		return false;
		
	}
	
	//overloading
	public boolean validateStatus(String status)
	{
		//Check if status is equal to one of these (ARRIVED|BOARDING|BOOKING|CANCELLED|CHECKINGIN|DEPARTED)
		if (status.equals("ARRIVED"))
			return true;
		if (status.equals("BOARDING"))
			return true;
		if (status.equals("BOOKING"))
			return true;
		if (status.equals("CANCELLED"))
			return true;
		if (status.equals("CHECKINGIN"))
			return true;
		if (status.equals("DEPARTED"))
			return true;
		return false;
	}
	
	public boolean validateTime(Time time)
	{
		if ((time != null) && (time.getMinute() < 60) && (time.getMinute() >= 0) &&
				(time.getHour()>=0) && (time.getHour() < 24))
		{
			/*
			int Hour = time.getHour();
			String hourStr = Integer.toString(Hour);
			System.out.println("Hour="+hourStr);
			*/
			return true;
		}else
			return false;
			
	}
	
	public boolean validatePassengerName (String name)
	{
		if ( (name != null) && (! name.isEmpty()) ) 
			return true;
		else
			return false;
	}
	
	public boolean validateAircraft (Aircraft aircraft)
	{
		if ( (aircraft.model == null) || (aircraft.seats == null) || (aircraft.model.isEmpty()))
			return false;
		
		Set<String> seatSet = aircraft.seats;
		
		for (String s:seatSet)
			if ( (s==null) || (! s.matches(SEAT_PATTERN)) ||(s.isEmpty()))
				return false;
		
		return true;
	}
	
	public boolean validateSeat(String s)
	{
		if ( (s==null) || (s.isEmpty()))
			return false;
		if (s.matches(SEAT_PATTERN))
			return true;
		return false;
		
	}
	
}
