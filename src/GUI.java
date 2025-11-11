package SumoTest;

import de.tudresden.sumo.cmd.Simulation; //Zum Abfragen von Simulationsdaten wie getTime()
import javax.swing.*;
import static SumoTest.TraciConnect.conn;

//TODO: for-each Schleife?

public class GUI {

    public static void main(String[] args) {

        try {
            TraciConnect.getConn();
            SwingUtilities.invokeLater(new Runnable() {
                public void run(){
                    GUI2 main = new GUI2();
                    main.show();
                }
            });
            for (int i = 0; i < 200000; i++) {
                conn.do_timestep();
            }

            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
