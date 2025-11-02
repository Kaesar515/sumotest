// Import the core TraaS classes.
// These come from TraaS.jar which acts as a Java wrapper around SUMO’s TraCI protocol.

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Inductionloop;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.objects.SumoVehicleData;


public class TraaSWrapper {

    // The main() method runs automatically when you start the program.
    public static void main(String[] args) {
        // --- Configuration values ---
        // The SUMO binary (can be "sumo" or "sumo-gui")
        String sumo_bin = "sumo-gui";


        // Path to your SUMO configuration file (.sumocfg)
        // This file defines the network, routes, and simulation parameters.
        String config_file = "data/frankfurt.sumocfg";

        // Step length = time step in seconds (0.1 means 10 steps per second)
        double step_length = 0.1;
        // Optional: Allow command-line arguments to override defaults.
        // For example, you could run:
        // java Main sumo-gui data/myConfig.sumocfg
        if (args.length > 0) sumo_bin = args[0];
        if (args.length > 1) config_file = args[1];

        // --------------------------------------------------------------------
        // 2. Try connecting to SUMO
        // --------------------------------------------------------------------
        try {
            // Create a TraaS connection object.
            // It knows how to start SUMO and communicate using TraCI.
            SumoTraciConnection conn = new SumoTraciConnection(sumo_bin, config_file);

            // Add options that SUMO should use when starting.
            // These are the same as command-line arguments to sumo.exe.
            conn.addOption("step-length", step_length + ""); // time step
            conn.addOption("start", "true"); // start SUMO immediately

            // ----------------------------------------------------------------
            // 3. Launch SUMO and connect via TraCI
            // ----------------------------------------------------------------
            // This line starts SUMO (the executable above) as a background process
            // and opens a network socket so your Java program can control it.
            conn.runServer();

            // Order = priority of the client (used only when multiple clients connect).
            conn.setOrder(1);

            System.out.println("✅ SUMO started successfully!");
            System.out.println("Simulation is running...");

            // ----------------------------------------------------------------
            // 4. Run the simulation loop
            // ----------------------------------------------------------------
            // We'll simulate for 100 time steps (10 seconds total).
            for (int i = 0; i < 20000; i++) {

                // Advance the simulation by one time step (0.1 sec).
                // This sends a command to SUMO to move all cars forward by one tick.
                conn.do_timestep();

                // Get the current simulation time from SUMO (in seconds).
                // The return value of do_job_get must be casted (converted) to double.
                double timeSeconds = (double) conn.do_job_get(Simulation.getTime());

                // Print the current simulation time to the console.
                System.out.println("⏱ Simulation time: " + timeSeconds + " seconds");
            }

            // ----------------------------------------------------------------
            // 5. Clean up: close the connection
            // ----------------------------------------------------------------
            // Always close the connection when done, to safely terminate SUMO.
            conn.close();
            System.out.println("✅ SUMO simulation finished and closed.");

        } catch (Exception ex) {
            // If anything goes wrong (missing file, SUMO not installed, etc.)
            // this block will print the error message to help you debug.
            System.err.println("❌ Error while running SUMO simulation:");
            ex.printStackTrace();
        }
    }
}






