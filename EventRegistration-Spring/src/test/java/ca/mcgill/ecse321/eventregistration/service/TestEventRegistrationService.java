package ca.mcgill.ecse321.eventregistration.service;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceXStream;

public class TestEventRegistrationService {
	private RegistrationManager rm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistenceXStream.initializeModelManager("output"+File.separator+"data.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		rm = new RegistrationManager();
	}

	@After
	public void tearDown() throws Exception {
		rm.delete();
	}

	@Test
	public void testCreateParticipant() {
		  assertEquals(0, rm.getParticipants().size());

		  String name = "Oscar";

		  EventRegistrationService erc = new EventRegistrationService(rm);
		  try {
			erc.createParticipant(name);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			 fail();
		}
		  RegistrationManager rm1 = rm;
		  checkResultParticipant(name, rm1);

		  RegistrationManager rm2 = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();
		  checkResultParticipant(name, rm2);

		  rm2.delete();
	}
	@Test
	public void testCreateParticipantEmpty() {
	  assertEquals(0, rm.getParticipants().size());

	  String name = "";

	  String error = null;
	  EventRegistrationService erc = new EventRegistrationService(rm);
	  try {
	    erc.createParticipant(name);
	  } catch (InvalidInputException e) {
	    error = e.getMessage();
	  }

	  // check error
	  assertEquals("Participant name cannot be empty!", error);

	  // check no change in memory
	  assertEquals(0, rm.getParticipants().size());
	}
	@Test
	public void testCreateParticipantSpaces() {
	    assertEquals(0, rm.getParticipants().size());

	    String name = " ";

	    String error = null;
	    EventRegistrationService erc = new EventRegistrationService(rm);
	    try {
	        erc.createParticipant(name);
	    } catch (InvalidInputException e) {
	        error = e.getMessage();
	    }

	    // check error
	    assertEquals("Participant name cannot be empty!", error);

	    // check no change in memory
	    assertEquals(0, rm.getParticipants().size());
	}
	
	@Test
	public void testFindAllParticipants()
	{
	    assertEquals(0, rm.getParticipants().size());

	    String[] names = { "John Doe", "Foo Bar" };

	    EventRegistrationService erc = new EventRegistrationService(rm);
	    for (String name : names) {
	        try {
	            erc.createParticipant(name);
	        } catch (InvalidInputException e) {
	            // Check that no error occured
	            fail();
	        }
	    }

	    List<Participant> registeredParticipants = erc.findAllParticipants();

	    // check number of registered participants
	    assertEquals(2, registeredParticipants.size());

	    // check each participant
	    for (int i = 0; i < names.length; i++) {
	        assertEquals(names[i], registeredParticipants.get(i).getName());
	    }

	}
	
	@Test
	public void testCreateEvent() {
	  RegistrationManager rm = new RegistrationManager();
	  assertEquals(0, rm.getEvents().size());

	  String name = "Soccer Game";
	  Calendar c = Calendar.getInstance();
	  c.set(2017, Calendar.MARCH, 16, 9, 0, 0);
	  Date eventDate = new Date(c.getTimeInMillis());
	  Time startTime = new Time(c.getTimeInMillis());
	  c.set(2017, Calendar.MARCH, 16, 10, 30, 0);
	  Time endTime = new Time(c.getTimeInMillis());
	  // test model in memory
	  EventRegistrationService erc = new EventRegistrationService(rm);
	  try {
	    erc.createEvent(name, eventDate, startTime, endTime);
	  } catch (InvalidInputException e) {
	    fail();
	  }
	  checkResultEvent(name, eventDate, startTime, endTime, rm);
	  // test file
	  RegistrationManager rm2 = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();
	  checkResultEvent(name, eventDate, startTime, endTime, rm2);
	  rm2.delete();
	}

	private void checkResultEvent(String name, Date eventDate, Time startTime, Time endTime, RegistrationManager rm2)   {
		  assertEquals(0, rm2.getParticipants().size());
		  assertEquals(1, rm2.getEvents().size());
		  assertEquals(name, rm2.getEvent(0).getName());
		  assertEquals(eventDate.toString(), rm2.getEvent(0).getEventDate().toString());
		  assertEquals(startTime.toString(), rm2.getEvent(0).getStartTime().toString());
		  assertEquals(endTime.toString(), rm2.getEvent(0).getEndTime().toString());
		  assertEquals(0, rm2.getRegistrations().size());
		}
	
	private void checkResultParticipant(String name, RegistrationManager rm2) {
		assertEquals(1, rm.getParticipants().size());
		  assertEquals(name, rm.getParticipant(0).getName());
		  assertEquals(0, rm.getEvents().size());
		  assertEquals(0, rm.getRegistrations().size());
	}

}
