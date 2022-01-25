package amministratore;

import beans.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("amministratore")
public class ServerAmministratore {

	// This function
	@Path("insert")
	@POST
	@Consumes({"application/json", "application/xml"})
	public Response insertDrone(DroneInfo dInfo) {
		System.out.println(dInfo.toString());
		/*
		// It may be better to just send the whole list after drone insert
		List<DroneInfo> dronesInNetwork = DroneInfos.getInstance().getDronesInfo();
		 */
		if(DroneInfos.getInstance().addDroneInfo(dInfo)) {
			System.out.println("Drone placed in the smart city.");
			List<DroneInfo> dronesInNetwork = DroneInfos.getInstance().getDronesInfo();
			InitDroneInfo initDroneInfo = new InitDroneInfo(dronesInNetwork);
			return Response.ok(initDroneInfo).build();
		}
		return Response.status(Response.Status.CONFLICT).build();
	}

	//La put potrebbe servire se faccio la recharge per metterlo a 0,0 e 100%
	//tieni d'occhio

	@Path("update/{drone}")
	@PUT
	@Consumes({"application/json", "application/xml"})
	public Response updateDrone(/*@PathParam("drone") int id, Drone droneToUpdate*/) {
		return Response.ok().build();
	}

	// This removes a drone from the smart city
	@Path("remove")
	@DELETE
	@Produces({"application/json", "application/xml"})
	public Response removeDrone(int droneID) {
		System.out.println("Server is removing drone " + droneID);
		if(DroneInfos.getInstance().removeDroneInfo(droneID)) {
			System.out.println("Drone successfully removed from smart city.");
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}

	@Path("send_stat")
	@POST
	@Consumes({"application/json", "application/xml"})
	public Response sendStat(GlobalStat globalStat) {
		// There's no need to check this time, the add will never fail
		GlobalStats.getInstance().addGlobalStat(globalStat);
		return Response.ok().build();
	}

	@GET
	@Produces({"application/json", "application/xml"})
	public Response getDronesStat() {
		return Response.ok(DroneInfos.getInstance()).build();
	}

	@Path("stats/{n}")
	@GET
	@Produces({"application/json", "application/xml"})
	public Response getLastNStats(@PathParam("n") int n) {
		if (n < 1) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		}
		List<GlobalStat> globalStats = GlobalStats.getInstance().getLastNStats(n);
		String lastStats = new Gson().toJson(globalStats, new TypeToken<List<GlobalStat>>() {}.getType());
		return Response.ok(lastStats).build();
	}

	@Path("deliveries/{t1}/{t2}")
	@GET
	@Produces({"application/json", "application/xml"})
	public Response getAverageDeliveries(@PathParam("t1") int t1, @PathParam("t2") int t2) {
		if (t1 < 0 || t2 < 0) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		} else if (t2 < t1) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.ok(GlobalStats.getInstance().getAverageDeliveries(t1, t2)).build();
	}

	@Path("traveled/{t1}/{t2}")
	@GET
	@Produces({"application/json", "application/xml"})
	public Response getAverageTraveledKM(@PathParam("t1") int t1, @PathParam("t2") int t2) {
		if (t1 < 0 || t2 < 0) {
			return Response.status(Response.Status.NOT_ACCEPTABLE).build();
		} else if (t2 < t1) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.ok(GlobalStats.getInstance().getAverageTraveledKM(t1, t2)).build();
	}
}
