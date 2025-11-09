package SumoTest;

import it.polito.appeal.traci.SumoTraciConnection; //FÜr die Grundlegende Verbindung zu Sumo + Starten von Sumo
import de.tudresden.sumo.cmd.Simulation; //Zum Abfragen von Simulationsdaten wie getTime()
import de.tudresden.sumo.cmd.Vehicle; //https://sumo.dlr.de/javadoc/traas/de/tudresden/sumo/cmd/Vehicle.html -> Einfügen von Autos, stoppen lassen, weiterfahren lassen etc.
import de.tudresden.sumo.objects.SumoStopFlags; //Gibt eine Flag zurück, warum ein Auto stoppt

import javax.swing.*;

//TODO: for-each Schleife?

public class GUI {
    public static SumoTraciConnection conn;

    public static void main(String[] args) {

        String sumo_bin = "sumo-gui"; //Gibt Variable sumo_bin den Namen von Sumo-Gui
        String config_file = "SumoTest.sumocfg"; //Sumo Config Datei
        double step_length = 0.1;

        if (args.length > 0) {
            sumo_bin = args[0];
        }
        if (args.length > 1) {
            config_file = args[1];
        }

        try {
            conn = new SumoTraciConnection(sumo_bin, config_file); //Baut TraaCI Connection auf
            conn.addOption("step-length", step_length + "");
            conn.addOption("start", "true"); //Startet Sumo

            //start Traci Server
            conn.runServer();
            conn.setOrder(1);



            byte lane1 = 0;
            conn.do_job_set(Vehicle.add("id0", "car", "route1", 0, 0.0, 1.0, lane1)); //Fügt Lastwagen hinzu. ACHTUNG: VehicleType "ev"/"tr" muss in dem .rou.xml File definiert werden.
            SumoStopFlags id1 = new SumoStopFlags(true, false, false, false, false, false, false);

            SwingUtilities.invokeLater(new Runnable() {
                public void run(){
                    GUI2 main = new GUI2();
                    main.show();
                }
            });

            for (int i = 0; i < 200000; i++) {

                conn.do_timestep();
                double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
                double speed1 = (double)conn.do_job_get(Vehicle.getSpeed("id0")); //Gibt Geschwindigkeit von Auto "id0" aus
                //System.out.println("Current Time Stamp: " + timeSeconds);
                //System.out.println("Current speed of vehicle with id: id0: " + speed1);


                if (timeSeconds >= 150)
                {
                    conn.do_job_set(Vehicle.setStop("id0", "-139002482#19", 15.92, lane1, 100.0, id1, 15.92, 200.0)); //Stoppt "id0"
                }
                if (timeSeconds >= 300)
                {
                    conn.do_job_set(Vehicle.resume("id0"));
                }


            }

            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
