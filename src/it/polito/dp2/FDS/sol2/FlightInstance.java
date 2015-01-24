package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.Aircraft;
import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.FlightInstanceStatus;
import it.polito.dp2.FDS.FlightReader;
import it.polito.dp2.FDS.FlightMonitorException;
import it.polito.dp2.FDS.PassengerReader;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

public class FlightInstance implements FlightInstanceReader{

	private FlightInstanceStatus status;
	private Aircraft aircraft;
	private GregorianCalendar date;
	private int delay;
	private String departureGate;
	private FlightReader flight;
	private Set<Passenger> passengerSet;
	private InputValidator validator;
	
	public FlightInstance (FlightReader flight, GregorianCalendar date, int delay,
			Aircraft aircraft, FlightInstanceStatus status, String departureGate, Set<Passenger> passengerSet) throws FlightMonitorException
	{
		validator = new InputValidator();
		
		//Flight has been already validated by Flight.java
		this.flight=flight;
		
		if (validator.validateDate(date) == true)
			this.date=date;
		else
			throw new FlightMonitorException();
		
		if (validator.validateDelay(delay) == true)
			this.delay=delay;
		else
			throw new FlightMonitorException();
		
		if (validator.validateAircraft(aircraft) == true)
			this.aircraft=aircraft;
		else
			throw new FlightMonitorException();
		
		if (validator.validateStatus(status.toString()) == true)
			this.status=status;
		else
			throw new FlightMonitorException();
		
		if (departureGate.equals("-")==true)
			this.departureGate=null;
		else
			this.departureGate=departureGate;
		
		//Passenger Set has been already validated by Passenger.java
		this.passengerSet=passengerSet;
	}
	
	
	public FlightInstanceStatus getStatus()
	{
		return status;
	}
	
	public Aircraft getAircraft()
	{
		return aircraft;
	}
	
	public GregorianCalendar getDate()
	{
		return date;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public String getDepartureGate()
	{
		return departureGate;
	}
	
	public FlightReader getFlight()
	{
		return flight;
	}
	
	public Set<PassengerReader> getPassengerReaders(String namePrefix)
	{
		//Each passenger has been already validated by Passenger.java
		Set<PassengerReader> passengerReaderSet=new HashSet<PassengerReader>();
		for (Passenger p:passengerSet)
		{
			if (namePrefix==null)
			{
				PassengerReader pr=new PassengerReaderSol(p.isBoarded(), p.getName(), p.getSeat(), this);
				passengerReaderSet.add(pr);
			}else{
				if (p.getName().startsWith(namePrefix)==true)
				{
					PassengerReader pr=new PassengerReaderSol(p.isBoarded(), p.getName(), p.getSeat(), this);
					passengerReaderSet.add(pr);
				}
			}
		}
		return passengerReaderSet;
	}
	
}