package ca.mcgill.ecse321.eventregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
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
		Event e = new Event(name, date, startTime, endTime);
	    rm.addEvent(e);
	    PersistenceXStream.saveToXMLwithXStream(rm);
	    return e;
		}
}
