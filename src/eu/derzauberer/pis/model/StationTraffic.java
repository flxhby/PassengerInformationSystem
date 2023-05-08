package eu.derzauberer.pis.model;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class StationTraffic implements Entity<StationTraffic> {
	
	private final String id;
	private final Map<Integer, SortedSet<StationTrafficEntry>> departures = new HashMap<>();
	private final Map<Integer, SortedSet<StationTrafficEntry>> arrivals = new HashMap<>();
	
	@ConstructorProperties({ "id" })
	private StationTraffic(String id) {
		this.id = id;
	}
	
	public StationTraffic(String stationId, LocalDate date) {
		this.id = createIdFormNameAndDate(stationId, date);
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void addDeparture(StationTrafficEntry entry) {
		departures.get(entry.getTime().getHour()).add(entry);
	}
	
	public void removeDeparture(StationTrafficEntry entry) {
		departures.get(entry.getTime().getHour()).remove(entry);
	}
	
	public SortedSet<StationTrafficEntry> getDeparturesInHour(int hour) {
		final SortedSet<StationTrafficEntry> departureSet = departures.get(hour);
		return Collections.unmodifiableSortedSet(departureSet != null ? departureSet : new TreeSet<>());
	}
	
	public SortedSet<StationTrafficEntry> getDeparturesSinceHour(int hour) {
		final SortedSet<StationTrafficEntry> departureSet = new TreeSet<>();
		for (int i = hour; i < 24; i++) {
			departureSet.addAll(arrivals.get(hour));
		}
		return Collections.unmodifiableSortedSet(departureSet);
	}
	
	public void addArrival(StationTrafficEntry entry) {
		arrivals.get(entry.getTime().getHour()).add(entry);
	}
	
	public void removeArrival(StationTrafficEntry entry) {
		arrivals.get(entry.getTime().getHour()).remove(entry);
	}
	
	public SortedSet<StationTrafficEntry> getArrivalsInHour(int hour) {
		final SortedSet<StationTrafficEntry> arrivalSet = arrivals.get(hour);
		return Collections.unmodifiableSortedSet(arrivalSet != null ? arrivalSet : new TreeSet<>());
	}
	
	public SortedSet<StationTrafficEntry> getArrivalsSinceHour(int hour) {
		final SortedSet<StationTrafficEntry> arrivalSet = new TreeSet<>();
		for (int i = hour; i <= 24; i++) {
			arrivalSet.addAll(arrivals.get(hour));
		}
		return Collections.unmodifiableSortedSet(arrivalSet);
	}
	
	public static String createIdFormNameAndDate(String stationId, LocalDate date) {
		return stationId + "_" + date.getYear() + date.getMonthValue() + date.getDayOfMonth();
	}

}
