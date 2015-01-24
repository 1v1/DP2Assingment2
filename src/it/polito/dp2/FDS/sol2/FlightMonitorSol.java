package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.Aircraft;
import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.FlightInstanceStatus;
import it.polito.dp2.FDS.FlightMonitor;
import it.polito.dp2.FDS.FlightMonitorException;
import it.polito.dp2.FDS.FlightReader;
import it.polito.dp2.FDS.MalformedArgumentException;
import it.polito.dp2.FDS.Time;
import it.polito.dp2.FDS.sol2.jaxb.AircraftType;
import it.polito.dp2.FDS.sol2.jaxb.FlightInfoType;
import it.polito.dp2.FDS.sol2.jaxb.FlightInstanceType;
import it.polito.dp2.FDS.sol2.jaxb.FlightType;
import it.polito.dp2.FDS.sol2.jaxb.PassengerType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class FlightMonitorSol implements FlightMonitor{

	private FlightInfoType flightInfo;
	private InputValidator validator;

	private Set<Aircraft> aircraftSet;
	// This hash map is used to speed up the search of duplicates
	private HashMap<String, Aircraft> aircraftMap;
	private List<FlightReader> flightReaderList;
	private List<FlightInstanceReader> flightInstanceReaderList;

	public FlightMonitorSol()
	{
		final String filename = System.getProperty("it.polito.dp2.FDS.sol2.FlightInfo.file");
		validator = new InputValidator();
		try{
			File XMLfile = new File(filename);
			File XMLschema = new File("./xsd/FDSInfo.xsd");

			// Check if the XML file and XML schema exist and they are not directories
			if ((!XMLfile.exists()) || (XMLfile.isDirectory()) || (!XMLschema.exists()) || (XMLschema.isDirectory()))
				throw new FileNotFoundException("The requested file are invalid");

			// Check if the XML file is empty
			BufferedReader br = new BufferedReader(new FileReader(filename));     
			if (br.readLine() == null)
			{
				br.close();
				throw new EmptyFileException("The requested file is empty");
			}
			br.close();
			Source schemaSource = new StreamSource(XMLschema);
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaSource); 
			Source source = new StreamSource(XMLfile);
			JAXBContext jaxbContext = JAXBContext.newInstance("it.polito.dp2.FDS.sol2.jaxb");
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			jaxbUnmarshaller.setSchema(schema);
			jaxbUnmarshaller.setEventHandler(new MyValidationEventHandler());

			JAXBElement<FlightInfoType> root = jaxbUnmarshaller.unmarshal(source, FlightInfoType.class);
			flightInfo = root.getValue();

			// Create and fill the lists
			aircraftSet = new HashSet<Aircraft>();
			aircraftMap = new HashMap<String, Aircraft>();
			createAircrafts();

			flightReaderList = new ArrayList<FlightReader>();
			createFlights();

			flightInstanceReaderList = new ArrayList<FlightInstanceReader>();
			createFlightInstances();

		}catch (FileNotFoundException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}catch(EmptyFileException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (JAXBException e) {
			e.printStackTrace();
			System.exit(1);
		}
		catch (SAXException e)
		{
			e.printStackTrace();
			System.exit(1);
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}catch(FlightMonitorException e)
		{
			e.printStackTrace();
			System.exit(1);
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void createAircrafts() throws FlightMonitorException
	{
		List<AircraftType> aircraftList = flightInfo.getAircraftSet().getAircraft();

		for (AircraftType a: aircraftList)
			// Check if the aircraft has already been put in the HashMap
			if (!aircraftMap.containsKey(a.getAircraftModel()))
			{
				Boolean isInTheSet=false;
				Set<String> seatSet = new HashSet<String>();
				for (String s:a.getSeat())
				{
					// Check for each seat if the seat is already in the seatSet, to avoid duplicates
					for (String seat:seatSet)
						if (seat.equals(s))
						{
							isInTheSet=true;
							break;
						}

					if (!isInTheSet)
						seatSet.add(s);
				}
				Aircraft air = new Aircraft(a.getAircraftModel(), seatSet);
				// Check if this Aircraft is valid
				if (validator.validateAircraft(air))
				{
					aircraftMap.put(a.getAircraftModel(), air);
					aircraftSet.add(air);
				}
				else
					throw new FlightMonitorException("Invalid aircraft");
			}
	}

	private void createFlights() throws FlightMonitorException
	{
		List<FlightType> flightList = flightInfo.getFlight();

		for (FlightType fl:flightList)
		{
			//The departure and the arrival airport corresponds to the requested one
			int startHour = fl.getDepartureTime().getHour();
			int startMinute = fl.getDepartureTime().getMinute();
			Time t = new Time (startHour, startMinute);

			FlightReader f = new Flight
					(fl.getDepartureAirport(), t, fl.getDestinationAirport(), fl.getFlightID()); 

			flightReaderList.add(f);
		}
	}

	private void createFlightInstances() throws FlightMonitorException
	{
		List<FlightType> flightList = flightInfo.getFlight();

		try
		{
			for (FlightType fl:flightList)
			{
				List<FlightInstanceType> flightInstanceList = fl.getFlightInstance();
				for (FlightInstanceType flIn:flightInstanceList)
				{
					Aircraft aircraft;
					// This avoids the NullPointerException
					if (flIn.getAircraftModel()!=null)
					{
						aircraft = aircraftMap.get(flIn.getAircraftModel());
						if (aircraft == null)
							throw new FlightMonitorException("Invalid aircraft");
					}
					else
						throw new FlightMonitorException("Invalid aircraft model");

					FlightInstanceStatus flightStatus = FlightInstanceStatus.valueOf(flIn.getFlightStatus().toString());

					List<PassengerType> passengerList = flIn.getPassenger();
					Set<Passenger> passengerSet = new HashSet<Passenger>();

					//Get the passenger name and her values
					for (PassengerType p:passengerList)
					{
//						// Check if the seat assigned to the passenger is in the aircraft
//						boolean seatExists = false;
//						for (String s:aircraft.seats)
//							if (s.equals(p.getSeat()))
//							{
//								seatExists = true;
//								break;
//							}
//						if (!seatExists)
//							throw new FlightMonitorException("The seat assigned to the passenger does not exist in the aircraft");
//						
						if (!validator.validateSeat(p.getSeat()))
						{
							if (p.isBoarded())
								throw new FlightMonitorException("The passenger is boarded but has no seat");
//							if ( (!p.getSeat().isEmpty()) && (p.getSeat()!=null) )
//								throw new FlightMonitorException("The seat is not well formed");
						}
						
						Passenger passenger = new Passenger (p.getPassengerName(), p.isBoarded(), p.getSeat());
						passengerSet.add(passenger);
					}

					FlightInstanceReader flightInstance =
							new FlightInstance( getFlight(fl.getFlightID()), 
									flIn.getDepartureDate().toGregorianCalendar(), 
									flIn.getDelay(), aircraft, flightStatus, flIn.getDepartureGate(), passengerSet);
					flightInstanceReaderList.add(flightInstance);
				}
			}
		}catch (MalformedArgumentException e)
		{
			e.printStackTrace();
		}
	}


	public Set<Aircraft> getAircrafts()
	{
		return aircraftSet;
	}


	public FlightReader getFlight(String flightNumber) throws MalformedArgumentException
	{
		// Check if the flight number is valid
		if ( (validator.validateFlightNumber(flightNumber)==false) && (flightNumber != null) )
			throw new MalformedArgumentException("Invalid flight number");

		for (FlightReader f:flightReaderList)
			if (f.getNumber().equals(flightNumber))
				return f;

		return null;
	}

	public List<FlightReader> getFlights(String dep, String arr, Time startTime) throws MalformedArgumentException
	{
		// Check if the arguments are valid
		if ((validator.validateAirport(dep)==false)&&(dep!=null))
			throw new MalformedArgumentException("Invalid departure airport argument");
		if ((validator.validateAirport(arr)==false)&&(arr!=null))
			throw new MalformedArgumentException("Invalid destination airport argument");
		if ((startTime!=null) && (validator.validateTime(startTime) == false))
			throw new MalformedArgumentException("Invalid departure time argument");
		if ( (dep != null) && (arr != null) && (dep.equals(arr)) )
			throw new MalformedArgumentException("The destination airport is equal to the departure airport");

		List<FlightReader> returnList = new LinkedList<FlightReader>();
		returnList.clear();

		Time timeToSearch;
		if (startTime == null)
			timeToSearch = new Time(0,0);
		else
			timeToSearch = new Time (startTime.getHour(), startTime.getMinute());

		for (FlightReader fl:flightReaderList)
		{
			if ((dep == null) || (dep.equals(fl.getDepartureAirport()) )
					&& ((arr == null) || (arr.equals(fl.getDestinationAirport()))))
			{
				//The departure and the arrival airport corresponds to the requested one
				int startHour = fl.getDepartureTime().getHour();
				int startMinute = fl.getDepartureTime().getMinute();
				Time departureTime = new Time (startHour, startMinute);

				if ((timeToSearch.precedes(departureTime) == true) || 
						((startHour == timeToSearch.getHour())&&(startMinute == timeToSearch.getMinute())))
					// The departure time of the flight is not before the requested time
					returnList.add(fl);
			}
		}
		return returnList;
	}


	public FlightInstanceReader getFlightInstance(String flightNumber, GregorianCalendar date) throws MalformedArgumentException
	{
		// Check if the arguments are valid
		if ( (validator.validateFlightNumber(flightNumber)==false) || (validator.validateDate(date)==false) )
			throw new MalformedArgumentException();

		for (FlightInstanceReader fl:flightInstanceReaderList)
			//Check the flight ID and the departure date
			if (fl.getFlight().getNumber().equals(flightNumber) && (isEqual(fl.getDate(), date)))
				return fl;

		return null;
	}

	public List<FlightInstanceReader> getFlightInstances(String number, GregorianCalendar startDate, FlightInstanceStatus status) throws MalformedArgumentException
	{
		// Check if the arguments are valid
		if ( ((validator.validateFlightNumber(number)==false) && (number!=null) ) 
				|| ( (validator.validateDate(startDate)==false) && (startDate!=null) ) 
				|| ( (validator.validateStatus(status) == false) && (status!=null) ))
			throw new MalformedArgumentException();

		List<FlightInstanceReader> returnList = new LinkedList<FlightInstanceReader>();
		returnList.clear();
		
		for (FlightInstanceReader fl:flightInstanceReaderList)
		{
			if ((number == null)||(fl.getFlight().getNumber().equals(number)))
			{
				// This flight is acceptable according to its flightID
				FlightInstanceStatus flInStatus = fl.getStatus();

				if (((status==null)||(flInStatus==status))
						&& ((startDate == null) || ( isBefore(startDate, fl.getDate()))))
				{
					returnList.add(fl);
				}
			}
		}
		return returnList;
	}

	private boolean isBefore(GregorianCalendar startDate, GregorianCalendar flightDate)
	{
		/*
		 * Check if the startDate is before the flight date.
		 * 
		 * I consider to compare the two dates, using only DAY_OF_MONTH, MONTH, YEAR and the TIMEZONE.
		 * In order to do that, I reset all the meaningless fields of the two dates before comparing them.
		 * I use the method after to compare the two dates, and I return the complement of the operation, 
		 * to include also the dates that are equals.
		 * 
		 */
		
		flightDate.set(Calendar.HOUR, 0);
		flightDate.set(Calendar.MINUTE, 0);
		flightDate.set(Calendar.SECOND, 0);
		flightDate.set(Calendar.MILLISECOND, 0);
		
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		
		return !startDate.after(flightDate);
	}
	
	private boolean isEqual(GregorianCalendar flightDate, GregorianCalendar startDate)
	{
		if (flightDate.get(Calendar.YEAR) != startDate.get(Calendar.YEAR))
			return false;
		if (flightDate.get(Calendar.MONTH) != startDate.get(Calendar.MONTH))
			return false;
		if (flightDate.get(Calendar.DAY_OF_MONTH) != startDate.get(Calendar.DAY_OF_MONTH))
			return false;
		
		return true;
	}

}
