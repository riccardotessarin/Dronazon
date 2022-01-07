package amministratore;

import beans.DroneInfo;
import beans.DroneInfos;
import beans.InitDroneInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("amministratore")
public class ServerAmministratore {

	// This function gives statistics about the drones
	// this includes ID, global stats, average number of shippings and average number
	// of kilometers travelled between two timestamps
	@GET
	@Produces({"application/json", "application/xml"})
	public Response getDronesStat() {
		return Response.ok(/*Drones.getInstance().getStat()*/).build();
	}

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
	public Response removeDrone(DroneInfo dInfo) {
		System.out.println(dInfo.toString());
		if(DroneInfos.getInstance().removeDroneInfo(dInfo)) {
			System.out.println("Drone successfully removed from smart city.");
			return Response.ok().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}
