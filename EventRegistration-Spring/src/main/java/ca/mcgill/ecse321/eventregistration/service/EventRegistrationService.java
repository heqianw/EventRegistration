package ca.mcgill.ecse321.eventregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.Registration;
import ca.mcgill.ecse321.eventregistration.model.RegistrationManager;
import ca.mcgill.ecse321.eventregistration.persistence.PersistenceXStream;

@Service
public class EventRegistrationService {
	
	private RegistrationManager rm;
	
	public EventRegistrationService(RegistrationManager rm)
	{
	  this.rm = rm;
	}

	public Participant createParticipant(String name) throws InvalidInputException
	{
		if (name == null || name.trim().length() == 0) {
		      throw new InvalidInputException("Participant name cannot be empty!");
		  }
		Participant p = new Participant(name);
		rm.addParticipant(p);
		PersistenceXStream.saveToXMLwithXStream(rm);
		return p;
	}
	
	public List<Participant> findAllParticipants()
	{
	  // return the participants by calling the function
		return rm.getParticipants();
	}
	
	//custom function to return all events
	public List<Event> findAllEvents()
	{
	  // return the events by calling the function
		return rm.getEvents();
	}

	public List<Event> getEventsForParticipant(Participant p) {
		// this method is to getEventsForParticipants
		// TODO Auto-generated method stub
		//if there are registrations, then parse them one by one to find the ones we want
		List<Event> eventsParticipant = new ArrayList<Event>(); 
		if(rm.hasRegistrations()) {
			for(int i = 0; i < rm.numberOfRegistrations(); i++) {
				if(rm.getRegistration(i).getParticipant().getName() == p.getName()) {
					eventsParticipant.add(rm.getRegistration(i).getEvent());
				}
			}
		}
		return eventsParticipant;
	}
	
	public Event createEvent(String name, Date date, Time startTime, Time endTime) throws InvalidInputException {
		if (name == null || date == null || startTime == null || endTime == null) {
		      throw new InvalidInputException("Event name cannot be empty! Event date cannot be empty! Event start time cannot be empty! Event end time cannot be empty!");
		}
		else if (name.equals("") || name.equals(" ")) {
		      throw new InvalidInputException("Event name cannot be empty!");
		}
		else if (endTime.compareTo(startTime) < 0) {
		      throw new InvalidInputException("Event end time cannot be before event start time!");
		}
		
		Event e = new Event(name, date, startTime, endTime);
	    rm.addEvent(e);
	    PersistenceXStream.saveToXMLwithXStream(rm);
	    return e;
		}
	
	public Registration register(Participant participant, Event event) throws InvalidInputException {
		 // To be completed
		if (participant == null || event == null) {
		      throw new InvalidInputException("Participant needs to be selected for registration! Event needs to be selected for registration!");
		}
		else if (rm.numberOfParticipants() == 0 || rm.numberOfEvents() == 0) {
		      throw new InvalidInputException("Participant does not exist! Event does not exist!");
		}
		Registration nReg = new Registration(participant, event);
		rm.addRegistration(nReg);
		PersistenceXStream.saveToXMLwithXStream(rm);
		return nReg;
		}
	//function to parse list of participants and check the names of each object participant
	public Participant getParticipantByName(String name) {
		List<Participant> listParticipant = rm.getParticipants();
		for(int i = 0; i < rm.numberOfParticipants(); i++) {
			if(listParticipant.get(i).getName().equals(name))
				return listParticipant.get(i);
		}
		return null;
	}
	//method to parse list of events and check names of each object event
	public Event getEventByName(String name) {
		List<Event> listEvent = rm.getEvents();
		for(int i = 0; i < rm.numberOfEvents(); i++) {
			if(listEvent.get(i).getName().equals(name))
				return listEvent.get(i);
		}
		return null;
	}
}
