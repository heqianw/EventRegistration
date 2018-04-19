package ca.mcgill.ecse321.eventregistration.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.eventregistration.dto.EventDto;
import ca.mcgill.ecse321.eventregistration.dto.ParticipantDto;
import ca.mcgill.ecse321.eventregistration.dto.RegistrationDto;
import ca.mcgill.ecse321.eventregistration.model.Event;
import ca.mcgill.ecse321.eventregistration.model.Participant;
import ca.mcgill.ecse321.eventregistration.model.Registration;
import ca.mcgill.ecse321.eventregistration.service.EventRegistrationService;
import ca.mcgill.ecse321.eventregistration.service.InvalidInputException;

@RestController
public class EventRegistrationRestController {
	@Autowired
	private EventRegistrationService service;

	@Autowired
	private ModelMapper modelMapper;

	@RequestMapping("/")
	public String index() {
		return "Event registration application root. Web-based frontend is a TODO. Use the REST API to manage events and participants.\n";
	}
	// Conversion methods (not part of the API)
	private EventDto convertToDto(Event e) {
		// In simple cases, the mapper service is convenient
		return modelMapper.map(e, EventDto.class);
	}

	private ParticipantDto convertToDto(Participant p) {
	  ParticipantDto participantDto = modelMapper.map(p, ParticipantDto.class);
	  participantDto.setEvents(createEventDtosForParticipant(p));
	  return participantDto;
	}

	private Participant convertToDomainObject(ParticipantDto pDto) {
		// Mapping DTO to the domain object without using the mapper
		List<Participant> allParticipants = service.findAllParticipants();
		for (Participant participant : allParticipants) {
			if (participant.getName().equals(pDto.getName())) {
				return participant;
			}
		}
		return null;
	}
	
	@PostMapping(value = { "/participants/{name}", "/participants/{name}/" })
	public ParticipantDto createParticipant(
			@PathVariable("name") String name) throws InvalidInputException {
		Participant participant = service.createParticipant(name);
		return convertToDto(participant);
	}
	
	@GetMapping(value = { "/participants", "/participants/" })
	public List<ParticipantDto> findAllParticipants() {
		List<ParticipantDto> participants = new ArrayList<ParticipantDto>();
		for (Participant participant : service.findAllParticipants()) {
			participants.add(convertToDto(participant));
		}
		return participants;
	}

	private List<EventDto> createEventDtosForParticipant(Participant p) {
		// TODO it is your task to add and implement the getEventsForParticipant service
		// within the EventRegistrationService class, since it is missing now
		List<Event> eventsForParticipant = service.getEventsForParticipant(p);
		List<EventDto> events = new ArrayList<EventDto>();
		for (Event event : eventsForParticipant) {
			events.add(convertToDto(event));
		}
		return events;
	}

	private RegistrationDto convertToDto(Registration r, Participant p, Event e) {
		// Now using the mapper would not help too much
		// RegistrationDto registrationDto = modelMapper.map(r, RegistrationDto.class);
		// Manual conversion instead
		EventDto eDto = convertToDto(e);
		ParticipantDto pDto = convertToDto(p);
		return new RegistrationDto(pDto, eDto);
	}
	
	@PostMapping(value = { "/events/{name}", "/events/{name}/" })
	public EventDto createEvent(
			@PathVariable ("name") String name,
			@RequestParam Date date,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern="HH:mm") LocalTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern="HH:mm") LocalTime endTime
		) throws InvalidInputException {
		@SuppressWarnings("deprecation")
		Time startTimeSql = new Time(startTime.getHour(),startTime.getMinute(), 0);
		@SuppressWarnings("deprecation")
		Time endTimeSql = new Time(endTime.getHour(),endTime.getMinute(), 0);
		Event event = service.createEvent(name, date, startTimeSql, endTimeSql);
		return convertToDto(event);
	}
	
	// TODO supply annotation for REST mapping to both GET on /events and GET on /events/
	@GetMapping(value = { "/events", "/events/" })
	public List<EventDto> findAllEvents() {
		// TODO use the service here and create a list of DTOs
		List<Event> everyEvents = service.findAllEvents();
		List<EventDto> events = new ArrayList<EventDto>();
		for (Event event : everyEvents) {
			events.add(convertToDto(event));
		}
		return events;
	}
	
	@PostMapping(value = { "/register", "/register/" })
	public RegistrationDto registerParticipantForEvent(
			@RequestParam (name = "participant") ParticipantDto pDto,
			@RequestParam (name = "event") EventDto eDto
		) throws InvalidInputException {
		// In this example application, we assumed that participants and events are identified by their names
		Participant p = service.getParticipantByName(pDto.getName());
		Event e = service.getEventByName(eDto.getName());
		Registration r = service.register(p, e);
		return convertToDto(r, p, e);
	}
	
	@GetMapping(value = { "/registrations/participant/{name}", "/registrations/participant/{name}/" })
	public List<EventDto> getEventsOfParticipant(@PathVariable("name") ParticipantDto pDto) {
		Participant p = convertToDomainObject(pDto);
		return createEventDtosForParticipant(p);
	}
	
}
