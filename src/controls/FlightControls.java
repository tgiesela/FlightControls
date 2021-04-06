package controls;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import data.DataRefReader;
import interfaces.DrefEventListener;

public class FlightControls extends JFrame implements DrefEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private AttitudeControl attitudeControl;
    private AutopilotControl autopilotControl;
    final static DataRefReader reader = new DataRefReader();
    /* attitude drefs */
	private static String COCKPIT_GYROS_PITCH = "sim/cockpit/gyros/the_ind_ahars_pilot_deg"; //pitch (omhoog/omlaag)
	private static String COCKPIT_GYROS_ROLL = "sim/cockpit/gyros/phi_ind_ahars_pilot_deg"; //roll (links/rechts)
	private static String FLIGHTMODEL_POS_DELTA = "sim/flightmodel/position/beta"; // heading relative to flown path

	public FlightControls() {
        initUI();
    }

    private void initUI() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	reader.unsubscribeAll();
                //Timer timer = surface.getTimer();
                //timer.stop();
            }
        });

        this.setTitle("Flight instruments");
        this.setSize(650, 350);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(null);
    	attitudeControl = new AttitudeControl();
    	attitudeControl.setBounds(0, 0, 300, 350);
        getContentPane().add(attitudeControl);
        
        reader.subscribe(COCKPIT_GYROS_PITCH);
		reader.subscribe(COCKPIT_GYROS_ROLL);
		reader.subscribe(FLIGHTMODEL_POS_DELTA);

		autopilotControl = new AutopilotControl(reader);
    	autopilotControl.setBounds(300, 0, 400, 350);
        getContentPane().add(autopilotControl);

   		reader.registerDrefEventListener(this);
    
    }
    public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

            	FlightControls ex = new FlightControls();
                ex.setVisible(true);
            }
        });
        reader.doRead();
	}
	@Override
	public void onDrefData(String name, float value) {
		if (name.equals(COCKPIT_GYROS_PITCH)) {
			attitudeControl.setPitch(value);
		} 
		else 
		if (name.equals(COCKPIT_GYROS_ROLL)) {
			attitudeControl.setRoll(value);
		}
		else 
		if (name.equals(FLIGHTMODEL_POS_DELTA)) {
		};
		autopilotControl.onDrefData(name, value);
	}

	@Override
	public void onDrefData(String name, double value) {
		
	}

}
