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
            Process sumoProcess = new ProcessBuilder(
                    sumoPath,
                    "-c", sumoConfig,
                    "--remote-port", String.valueOf(port)
            ).inheritIO() // Ausgabe in Konsole sichtbar
                    .start();

            // Kurze Pause, damit SUMO hochfahren kann
            Thread.sleep(2000);

            // TraaS-Verbindung aufbauen
            SumoTraciConnection conn = new SumoTraciConnection(port);
            conn.addOption("step-length", String.valueOf(stepLength));
            conn.runServer();

            // Map für Travel Time Tracking
            Map<String, Double> vehicleDepartureTimes = new HashMap<>();

            // Simulationsschritt
            conn.do_timestep();

            // 1. Durchschnittsgeschwindigkeit global in der gesamten Simulation
            List<String> vehicleIDs = (List<String>) conn.do_job_get(Vehicle.getIDList());
            double totalSpeed = 0;
            for (String vehID : vehicleIDs) {
                double speed = (double) conn.do_job_get(Vehicle.getSpeed(vehID));
                totalSpeed += speed;
                vehicleDepartureTimes.putIfAbsent(vehID, (double) conn.do_job_get(Simulation.getTime()));
            }
            double averageSpeed = vehicleIDs.isEmpty() ? 0 : totalSpeed / vehicleIDs.size();
            System.out.println("Average speed: " + averageSpeed);

            // 2. Fahrzeugdichte lokal auf der jeweiligen Edge
            List<String> edgeIDs = (List<String>) conn.do_job_get(Edge.getIDList());
            for (String edgeID : edgeIDs) {
                int numVehicles = (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID)); // Returns the number of vehicles currently on the edge
                System.out.println("Edge " + edgeID + " - Vehicles: " + numVehicles);
            }

            // 3. Verstopfung lokal (Geschwindigkeit weniger als 2.0)
            for (String edgeID : edgeIDs) {
                double edgeSpeed = (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID)); // Returns mean speed on the edge (sum of all lanes)
                if (edgeSpeed < 2.0) {
                    System.out.println("Congestion detected on edge " + edgeID);
                }
            }

            // 4. Reisezeit pro Vehicle (Vehicle Zeit = Gesamtzeit - Zeit seit Injection)
            vehicleDepartureTimes.keySet().removeIf(vehID -> {
                if (!vehicleIDs.contains(vehID)) {
                    double arrivalTime = 0;
                    try {
                        arrivalTime = (double) conn.do_job_get(Simulation.getTime());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    double travelTime = arrivalTime - vehicleDepartureTimes.get(vehID);
                    System.out.println("Vehicle " + vehID + " travel time: " + travelTime + " s");
                    return true;
                }
                return false;
            });

            // Verbindung schließen
            conn.close();

            // SUMO Prozess beenden
            sumoProcess.destroy();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
