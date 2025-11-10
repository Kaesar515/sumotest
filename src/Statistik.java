import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Simulation;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistik {

    public static void main(String[] args) {

        // Pfade zu SUMO GUI und Circles-Konfiguration
        String sumoPath = "C:\\Program Files (x86)\\Eclipse\\Sumo\\bin\\sumo-gui.exe";
        String sumoConfig = "C:\\Program Files (x86)\\Eclipse\\Sumo\\doc\\tutorial\\circles\\data\\circles.sumocfg";
        int port = 8873;      // TraCI Port
        double stepLength = 0.1;

        try {
            // SUMO starten
            Process sumoProcess = new ProcessBuilder(
                    sumoPath,
                    "-c", sumoConfig,
                    "--remote-port", String.valueOf(port)
            ).inheritIO() // Ausgabe in Konsole sichtbar
                    .start();

            // Kurze Pause, damit SUMO hochfahren kann
            Thread.sleep(2000);

            // TraCI-Verbindung aufbauen
            SumoTraciConnection conn = new SumoTraciConnection(port);
            conn.addOption("step-length", String.valueOf(stepLength));
            conn.runServer();

            // Map für Travel Time Tracking
            Map<String, Double> vehicleDepartureTimes = new HashMap<>();

            // Simulation so lange laufen lassen, bis keine Fahrzeuge mehr erwartet werden
            while ((int) conn.do_job_get(Simulation.getMinExpectedNumber()) > 0) {

                // Einen Zeitschritt ausführen
                conn.do_timestep();

                // 1. Durchschnittsgeschwindigkeit global in der gesamten Simulation
                List<String> vehicleIDs = (List<String>) conn.do_job_get(Vehicle.getIDList());
                double totalSpeed = 0;
                for (String vehID : vehicleIDs) {
                    double speed = (double) conn.do_job_get(Vehicle.getSpeed(vehID));
                    totalSpeed += speed;
                    // Abfahrtszeit merken (falls neu)
                    vehicleDepartureTimes.putIfAbsent(vehID, (double) conn.do_job_get(Simulation.getTime()));
                }
                double averageSpeed = vehicleIDs.isEmpty() ? 0 : totalSpeed / vehicleIDs.size();
                System.out.println("Average speed: " + averageSpeed);

                // 2. Fahrzeugdichte pro Kante
                List<String> edgeIDs = (List<String>) conn.do_job_get(Edge.getIDList());
                for (String edgeID : edgeIDs) {
                    int numVehicles = (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
                    System.out.println("Edge " + edgeID + " - Vehicles: " + numVehicles);
                }

                // 3. Stau-Erkennung (Hotspots) ab Geschwindigkeit weniger als 2.0
                for (String edgeID : edgeIDs) {
                    double edgeSpeed = (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID));
                    if (edgeSpeed < 2.0) {
                        System.out.println("Congestion detected on edge " + edgeID);
                    }
                }

                // 4. Reisezeiten (wenn Fahrzeuge verschwinden)
                vehicleDepartureTimes.keySet().removeIf(vehID -> {
                    if (!vehicleIDs.contains(vehID)) {
                        try {
                            double arrivalTime = (double) conn.do_job_get(Simulation.getTime());
                            double travelTime = arrivalTime - vehicleDepartureTimes.get(vehID);
                            System.out.println("Vehicle " + vehID + " travel time: " + travelTime + " s");
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                });

                // Kurze Pause für GUI-Update
                Thread.sleep(50);
            }

            // Verbindung schließen
            conn.close();

            // SUMO beenden
            sumoProcess.destroy();

            System.out.println("Simulation abgeschlossen.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
