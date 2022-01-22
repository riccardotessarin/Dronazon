package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
}
