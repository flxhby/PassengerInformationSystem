package eu.derzauberer.pis.model;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Line implements Entity<Line>, NameEntity {
	
	private final String id;
	private final String routeId;
	private String name;
	private TransportationType type;
	private int number;
	private String operatorId;
	private String driver;
	private boolean cancelled;
	private Integer position;
	private List<LineStop> stops;
	private List<Information> informations;
	private ApiInformation api;
	
	private Line(String id, Route route, LocalDateTime departure) {
		this.id = id;
		this.routeId = route.getId();
		this.name = route.getName();
		this.type = route.getType();
		this.number = route.getNumber();
		this.operatorId = route.getOperatorId();
		this.stops = new ArrayList<>();
		for (RouteStop stop : route.getStops()) {
			final LineStop lineStop = new LineStop(
					stop.getStationId(), 
					stop.getPlatform(), 
					stop.getPlatfromArea(),
					departure.plusMinutes(stop.getArrivalMinutesSinceStart()),
					departure.plusMinutes(stop.getDepartureMinutesSinceStart()));
			stops.add(lineStop);
		}
	}
	
	@ConstructorProperties({ "id", "routeId", "type", "number" })
	private Line(String id, String routeId, TransportationType type, int number) {
		this.id = id;
		this.routeId = routeId;
		this.type = type;
		this.number = number;
	}

	@Override
	public String getId() {
		return id;
	}
	
	public String getRouteId() {
		return routeId;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public TransportationType getType() {
		return type;
	}

	public void setType(TransportationType type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public void setDriver(String driver) {
		this.driver = driver;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Integer getPosition() {
		return position;
	}
	
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	public void addStop(LineStop stop) {
		if (stops == null) stops = new ArrayList<>();
		stops.add(stop);
	}
	
	public void addStop(int position, LineStop stop) {
		if (stops == null) { 
			final List<LineStop> newStops = new ArrayList<>();
			newStops.set(position, stop);
			stops = newStops;
		} else {
			stops.set(position, stop);
		}
	}
	
	public void removeStop(LineStop stop) {
		if (stops.isEmpty()) return;
		stops.remove(stop);
		if (stops.isEmpty()) stop = null;
	}
	
	public void removeStop(String stationId) {
		if (stops.isEmpty()) return;
		stops.remove(getStop(stationId));
		if (stops.isEmpty()) stops = null;
	}
	
	public void removeStop(int position) {
		if (stops.isEmpty()) throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + 0);
		stops.remove(position);
		if (stops.isEmpty()) stops = null;
	}
	
	public LineStop getStop(String stationId) {
		return stops.stream().filter(stop -> stop.getStationId().equals(stationId)).findAny().orElse(null);
	}
	
	public LineStop getStop(int position) {
		return stops.get(position);
	}
	
	public int getAmountOfStops() {
		return stops != null ? stops.size() : 0;
	}
	
	public LineStop getFirstStop() {
		return stops != null && !stops.isEmpty() ? stops.get(0) : null;
	}
	
	public LineStop getLastStop() {
		return stops != null && !stops.isEmpty() ? stops.get(stops.size() - 1) : null;
	}
	
	public List<LineStop> getStops() {
		return Collections.unmodifiableList(stops != null ? stops : new ArrayList<>());
	}
	
	public void addInformation(Information information) {
		if (informations == null) informations = new ArrayList<>();
		informations.add(information);
	}
	
	public void removeInformation(Information information) {
		if (informations == null) return;
		informations.remove(information);
		if (informations.isEmpty()) informations = null;
	}
	
	public void removeInformation(int index) {
		if (informations == null) return;
		informations.remove(index);
		if (informations.isEmpty()) informations = null;
	}
	
	public Information getInformation(int index) {
		return informations.get(index);
	}
	
	public int getInformationIndex(Information information) {
		return informations.indexOf(information);
	}
	
	public List<Information> getInformations() {
		final List<Information> results = informations != null ? informations : new ArrayList<>();
		return results.stream().sorted().collect(Collectors.toUnmodifiableList());
	}
	
	public ApiInformation getApiInformation() {
		return api;
	}
	
	public ApiInformation getOrCreateApiInformtion() {
		if (api == null) api = new ApiInformation();
		return api;
	}
	
	public void setApiInformation(ApiInformation api) {
		this.api = api;
	}

}
