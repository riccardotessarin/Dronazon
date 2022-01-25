package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class GlobalStats {

	@XmlElement(name = "global_statistics")
	private List<GlobalStat> globalStats;

	private static GlobalStats instance;

	private GlobalStats() {
		globalStats = new ArrayList<GlobalStat>();
	}

	//With a singleton we ensure that only one list of drones
	//will be active, it calls the constructor once
	public synchronized static GlobalStats getInstance() {
		if (instance == null) {
			instance = new GlobalStats();
		}
		return instance;
	}

	public List<GlobalStat> getGlobalStats() {
		return new ArrayList<>(globalStats);
	}

	public void setGlobalStats(List<GlobalStat> globalStats) {
		this.globalStats = globalStats;
	}

	public synchronized void addGlobalStat(GlobalStat globalStat) {
		globalStats.add(globalStat);
	}

	public List<GlobalStat> getLastNStats(int n) {
		List<GlobalStat> globalStatsCopy = getGlobalStats();

		int statsNum = globalStatsCopy.size();

		if (statsNum < n) {
			return globalStatsCopy;
		} else {
			return globalStatsCopy.subList(statsNum - n, statsNum);
		}
	}

	public double getAverageDeliveries(long t1, long t2) {
		if (t1 < 0 || t2 < 0 || t2 < t1) {
			System.out.println("Timestamps inserted are negative or non-consequent");
			return -1;
		}

		List<GlobalStat> globalStatsCopy = getGlobalStats();
		if (globalStatsCopy.size() == 0) {
			return 0.0;
		}

		/*
		List<Double> reducedDeliveriesList = globalStatsCopy.stream()
				.filter(globalStat -> globalStat.getTimestamp() >= t1 && globalStat.getTimestamp() <= t2)
				.map(GlobalStat::getAverageDeliveriesNumber).collect(Collectors.toList());

		if (reducedDeliveriesList.size() == 0) {
			return 0.0;
		}

		return reducedDeliveriesList.stream().reduce(0.0, Double::sum) / reducedDeliveriesList.size();
		 */

		Double average = 0.0;
		int count = 0;
		for (GlobalStat globalStat : globalStatsCopy) {
			if (globalStat.getTimestamp() >= t1 && globalStat.getTimestamp() <= t2) {
				average += globalStat.getAverageDeliveriesNumber();
				count++;
			}
		}

		if (count == 0) {
			return 0.0;
		}

		return average / count;

	}


	public double getAverageTraveledKM(long t1, long t2) {
		if (t1 < 0 || t2 < 0 || t2 < t1) {
			System.out.println("Timestamps inserted are negative or non-consequent");
			return -1;
		}

		List<GlobalStat> globalStatsCopy = getGlobalStats();
		if (globalStatsCopy.size() == 0) {
			return 0.0;
		}

		/*
		List<Double> reducedTraveledKMList = globalStatsCopy.stream()
				.filter(globalStat -> globalStat.getTimestamp() >= t1 && globalStat.getTimestamp() <= t2)
				.map(GlobalStat::getAverageTraveledKM).collect(Collectors.toList());

		if (reducedTraveledKMList.size() == 0) {
			return 0.0;
		}

		return reducedTraveledKMList.stream().reduce(0.0, Double::sum) / reducedTraveledKMList.size();
		 */

		Double average = 0.0;
		int count = 0;
		for (GlobalStat globalStat : globalStatsCopy) {
			if (globalStat.getTimestamp() >= t1 && globalStat.getTimestamp() <= t2) {
				average += globalStat.getAverageTraveledKM();
				count++;
			}
		}

		if (count == 0) {
			return 0.0;
		}

		return average / count;
	}
}
