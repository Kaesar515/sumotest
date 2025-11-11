package SumoTest;

import de.tudresden.sumo.cmd.Vehicle; //https://sumo.dlr.de/javadoc/traas/de/tudresden/sumo/cmd/Vehicle.html -> Einfügen von Autos, stoppen lassen, weiterfahren lassen etc.
import de.tudresden.sumo.cmd.Gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static SumoTest.TraciConnect.conn;


public class GUI2 {

    private JFrame window;
    private JButton button;
    private JButton button2;
    public GUI2() {
        window = new JFrame();
        window.setTitle("Gui Test");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 500);
        window.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout (FlowLayout.CENTER, 10, 5));
        panel.setBackground(Color.gray);
        panel.setPreferredSize(new Dimension(250, 250));
        window.add(panel, BorderLayout.NORTH);

        button = createButton();
        button2 = createButton2();
        panel.add(button);
        panel.add(button2);
    }

    private JButton createButton() {
        int id = 1;
        String idf = "id"+id;
        JButton button = new JButton("Inject Car");
        button.setFocusable(false); //Removes little box around the text.
        button.setToolTipText("Inject a new Car into the Sumo Simulation"); //Text apperaring, if you hover over the Button.
        ImageIcon carIcon = new ImageIcon("proxy-image.png"); //creating an instance from Class ImageIcon, to create an Icon for the Button.
        button.setIcon(carIcon); //Set the Icon on the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte lane1 = 0;
                try {
                    conn.do_job_set(Vehicle.add(idf, "car", "route1", 0, 0.0, 1.0, lane1)); //Fügt Lastwagen hinzu. ACHTUNG: VehicleType "ev"/"tr" muss in dem .rou.xml File definiert werden.
                    conn.do_job_set(Gui.trackVehicle("View #0", idf));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });


        return button;
    }

    private JButton createButton2() {
        JButton button2 = new JButton("Start Sumo GUI");
        button2.setFocusable(false); //Removes little box around the text.
        button2.setToolTipText("Start the Sumo Gui"); //Text apperaring, if you hover over the Button.
        ImageIcon sumoIcon = new ImageIcon("proxy-image2.png"); //creating an instance from Class ImageIcon, to create an Icon for the Button.
        button2.setIcon(sumoIcon); //Set the Icon on the button
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    conn.addOption("start", "true"); //Startet Sumo
//                    conn.runServer();
//                    conn.setOrder(1);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//        });

        return button2;
    }

    public void show(){
        window.setVisible(true);
    }
}
