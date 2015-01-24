package it.polito.dp2.FDS.sol2;

import it.polito.dp2.FDS.Aircraft;
import it.polito.dp2.FDS.FlightInstanceReader;
import it.polito.dp2.FDS.FlightMonitor;
import it.polito.dp2.FDS.FlightMonitorException;
import it.polito.dp2.FDS.FlightMonitorFactory;
import it.polito.dp2.FDS.FlightReader;
import it.polito.dp2.FDS.MalformedArgumentException;
import it.polito.dp2.FDS.PassengerReader;
import it.polito.dp2.FDS.sol2.jaxb.AircraftSetType;
import it.polito.dp2.FDS.sol2.jaxb.AircraftType;
import it.polito.dp2.FDS.sol2.jaxb.FlightInfoType;
import it.polito.dp2.FDS.sol2.jaxb.FlightInstanceType;
import it.polito.dp2.FDS.sol2.jaxb.FlightStatusType;
import it.polito.dp2.FDS.sol2.jaxb.FlightType;
import it.polito.dp2.FDS.sol2.jaxb.ObjectFactory;
import it.polito.dp2.FDS.sol2.jaxb.PassengerType;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;



public class FDSInfoSerializer {

	private FlightMonitor monitor;
	private Map<String, FlightType> id2Flight = new HashMap<String, FlightType>();

	private FDSInfoSerializer() throws FlightMonitorException {
		FlightMonitorFactory factory = FlightMonitorFactory.newInstance();
		monitor = factory.newFlightMonitor();
	}

	public static void main(String[] args) {
		FDSInfoSerializer f;

		try{
			if (args.length!=1)
			{
				System.out.println("usage: input file");
				System.exit(1);
			}
			final String filename=args[0];
			if (filename == null)
			{
				System.out.println("Invlaid filename");
				System.exit(1);
			}
			f=new FDSInfoSerializer();
			f.createXML(filename);
		}catch(ArrayIndexOutOfBoundsException aiobe)
		{
			aiobe.getMessage();
		}catch(FlightMonitorException fme)
		{
			System.err.println("Could not instantiate flight monitor object");
			fme.printStackTrace();
			System.exit(1);
		}

	}

	private void createXML(String filename)
	{
		try {
			// Create the document
			File file = new File(filename);
			
			JAXBContext jaxbContext = JAXBContext.newInstance("it.polito.dp2.FDS.sol2.jaxb");
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			FlightInfoType flightInfo = new FlightInfoType();
			flightInfo.getFlight().addAll(printFlights());
			flightInfo.setAircraftSet(printAircrafts());
			
			

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.example.org/FDSInfo ./xsd/FDSInfo.xsd");

			JAXBElement<FlightInfoType> flightInfoElement = (new ObjectFactory()).createFlightInfo(flightInfo);
			jaxbMarshaller.marshal(flightInfoElement, file);
		}
		catch (JAXBException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		catch (Exception e)
		{
			System.out.println("Unexpected Error during serialization");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private List<FlightType> printFlights()
	{
		List<FlightType> flightList = new ArrayList<FlightType>();
		try {
			List<FlightReader> l = monitor.getFlights(null, null, null);

			for (FlightReader f:l) {

				FlightType flight = new FlightType();

				//Set the Attributes of flight
				flight.setFlightID(f.getNumber());
				flight.setDepartureAirport(f.getDepartureAirport());
				flight.setDestinationAirport(f.getDestinationAirport());

				//Convert Time into a human-readable format
				GregorianCalendar c = new GregorianCalendar();
				c.set(GregorianCalendar.HOUR_OF_DAY, f.getDepartureTime().getHour());
				c.set(GregorianCalendar.MINUTE, f.getDepartureTime().getMinute());	
				c.set(GregorianCalendar.SECOND, 0);
				c.set(GregorianCalendar.MILLISECOND, 0);
				XMLGregorianCalendar g = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				flight.setDepartureTime(g);
				id2Flight.put(f.getNumber(), flight);
				flight.getFlightInstance().addAll(printFlightInstances(f.getNumber()));
				flightList.add(flight);
			}
		}catch (MalformedArgumentException e) {
			// this exception will never be thrown because getFlights is called with null arguments
			System.err.println("Unexpected exception");
			e.printStackTrace();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return flightList;
	}

	private AircraftSetType printAircrafts()
	{
		List<AircraftType> aircraftList = new LinkedList<AircraftType>();
		Set<Aircraft> aircraft = monitor.getAircrafts();
		for (Aircraft a:aircraft)
		{
			AircraftType air = new AircraftType();
			air.setAircraftModel(a.model);
			air.getSeat().addAll(a.seats);
			aircraftList.add(air);
		}
		
		AircraftSetType aircraftSet = new AircraftSetType();
		aircraftSet.getAircraft().addAll(aircraftList);
		return aircraftSet;
	}
	
	
	private List<FlightInstanceType> printFlightInstances (String flightID)
	{
		List<FlightInstanceType> flightInstanceList = new ArrayList<FlightInstanceType>();
		try{
			List<FlightInstanceReader> l = monitor.getFlightInstances(flightID, null, null);

			for (FlightInstanceReader f:l)
			{
				FlightInstanceType flightInstance = new FlightInstanceType();

				//flightInstance.getAircraft().addAll(printAircraft());
				GregorianCalendar c = f.getDate();
				XMLGregorianCalendar g = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				flightInstance.setDepartureDate(g);
				flightInstance.setDepartureGate(f.getDepartureGate());
				String status=f.getStatus().toString();
				FlightStatusType fsType=FlightStatusType.valueOf(status);
				flightInstance.setFlightStatus(fsType);
				flightInstance.setDelay(f.getDelay());
				flightInstance.getPassenger().addAll(printPassengers(f));
				FlightType flight = id2Flight.get(f.getFlight().getNumber());
				flightInstance.setFlightIDRef(flight);

				flightInstance.setAircraftModel(f.getAircraft().model);

				flightInstanceList.add(flightInstance);
			}
		}catch (MalformedArgumentException e) {
			// this exception will never be thrown because getFlights is called with null arguments
			System.err.println("Unexpected exception");
			e.printStackTrace();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return flightInstanceList;
	}

	private List<PassengerType> printPassengers(FlightInstanceReader f)
	{
		List<PassengerType> passengerList = new ArrayList<PassengerType>();
		try{
			Set<PassengerReader> s = f.getPassengerReaders(null);
			for (PassengerReader p:s)
			{
				PassengerType passenger = new PassengerType();
				passenger.setBoarded(p.boarded());
				GregorianCalendar c = f.getDate();
				XMLGregorianCalendar g = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				passenger.setDepartureDate(g);
				passenger.setPassengerName(p.getName());
				passenger.setSeat(p.getSeat());
				FlightType flight = id2Flight.get(f.getFlight().getNumber());
				passenger.setFlightIDRef(flight);
				passengerList.add(passenger);
			}

		}catch (Exception e)
		{
			e.printStackTrace();
		}

		return passengerList;
	}
}
