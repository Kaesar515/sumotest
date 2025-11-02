import it.polito.appeal.traci.SumoTraciConnection;

import de.tudresden.sumo.cmd.Vehicle;

import java.util.logging.SimpleFormatter;

public class simVehicle {
    private final String id;
    private final String routeId;
    private final String typeId;
    private final SumoTraciConnection conn;

    public simVehicle(String id, String routeId, String typeId, SumoTraciConnection conn) {
        this.id = id;
        this.routeId = routeId;
        this.typeId = typeId;
        this.conn = conn;
    }
    public String getId(){
        return id;

}








}
