package SumoTest;

import it.polito.appeal.traci.SumoTraciConnection;

public class TraciConnect {

    static SumoTraciConnection conn;

    private TraciConnect(){}

    //synchronized wegen thread

    public static SumoTraciConnection getConn() throws Exception{
        if(conn == null){
            conn = new SumoTraciConnection(MapSumoConfig.sumo_bin, MapSumoConfig.config_file);
            conn.addOption("step-length",String.valueOf(MapSumoConfig.step_length));
            conn.addOption("start","true");
            conn.runServer();
            conn.setOrder(1);
        }
        return conn;
    }

}
