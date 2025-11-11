package SumoTest;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;

public class VehiclesMangagement {
    private static int Idcounter = 1;
    private VehiclesMangagement (){}

    public static void injectCar(String givenRoutId, String givenTypeId) throws Exception{
        SumoTraciConnection conn = TraciConnect.getConn();
        String vehId = "V" + (Idcounter++);

        String defaultType = "testcar";
        String defaultRoute = "testroute ";


        String typeId;
        if (givenTypeId == null){
            typeId = defaultType;
        } else if (givenTypeId.isBlank()) {
            typeId = defaultType;

        }else {
            typeId = givenTypeId;
        }


        String routeId;
        if (givenRoutId == null){
            routeId = defaultRoute;
        } else if (givenRoutId.isBlank()) {
            routeId= defaultRoute;

        } else {
            routeId = givenRoutId;
        }

        int depart= (int) (double) conn.do_job_get(de.tudresden.sumo.cmd.Simulation.getTime());
        double pos = 0;
        double speed= 0;
        byte lane = 0;
        conn.do_job_set(Vehicle.add(vehId,typeId,routeId,depart,pos,speed,lane));
        System.out.println("Injected vehicle: " +vehId +", route: " + routeId );


    }


}
