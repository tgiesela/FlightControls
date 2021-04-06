package controls;

import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;

import data.DataRefReader;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;

public class AutopilotControl extends JPanel {
	private JToggleButton btnAutopilot;
	private JToggleButton btnFlightDirector;
	private JCheckBox cbHeading;
	private JCheckBox cbALT;
	private JCheckBox cbBC;
	private JCheckBox cbNAV;
	private JCheckBox cbAPPR;
	private JCheckBox cbAirspeed;
	private JCheckBox cbVviClimb;
	private JCheckBox cbALTActive;
	private JCheckBox cbAPPRActive;
	private JCheckBox cbVerToga;
	private JCheckBox cbWingLeveler;
	private JCheckBox cbNAVActive;
	private JCheckBox cbHorToga;
	private DataRefReader reader;
	
	/* autopilot drefs */
	/* 
	 * Also see: http://www.xsquawkbox.net/xpsdk/mediawiki/Sim/cockpit/autopilot/autopilot_state#Changing_Dataref_Values
	 */
	private static String AUTOPILOT_MODE = "sim/cockpit/autopilot/autopilot_mode";
	private static String AUTOPILOT_STATE = "sim/cockpit/autopilot/autopilot_state";
	private static String AUTOPILOT_BACKCOURSE = "sim/cockpit/autopilot/backcourse_on";
	private static String AUTOPILOT_ALTITUDE = "sim/cockpit/autopilot/altitude";
	private static String AUTOPILOT_CURRENT_ALTITUDE = "sim/cockpit/autopilot/current_altitude";
	private static String AUTOPILOT_APPROACH_SELECTOR = "sim/cockpit/autopilot/approach_selector";
	private static String AUTOPILOT_AIRSPEED = "sim/cockpit/autopilot/airspeed";
	private static String AUTOPILOT_HEADING = "sim/cockpit/autopilot/heading";
	private static String AUTOPILOT_FD_MODE = "sim/cockpit2/autopilot/flight_director_mode";
	private static String AUTOPILOT_VVI_FPM = "sim/cockpit2/autopilot/vvi_dial_fpm";
	private static String AUTOPILOT_CURRENT_VVI_FPM = "sim/cockpit2/gauges/indicators/vvi_fpm_pilot";
	
	private enum Autopilot{
		AutothrottleEngage(0x0001),
		HeadingHoldEngage(0x0002),
		WingLevelerEngage(0x0004),
		AirspeedHoldWithPitchEngage(0x0008),
		VVIClimbEngage(0x0010),
		AltitudeHoldArm(0x0020),
		FlightLevelChangeEngage(0x0040),
		PitchSyncEngage(0x0080),
		HNAVArmed(0x0100),
		HNAVEngaged(0x0200),
		GlideslopeArmed(0x0400),
		GlideslopeEngaged(0x0800),
		FMSArmed(0x1000),
		FMSEnaged(0x2000),
		AltitudeHoldEngaged(0x4000),
		HorizontalTOGAEngaged(0x8000),
		VerticalTOGAEngaged(0x10000),
		VNAVArmed(0x20000),
		VNAVEngaged(0x40000);
		
		int val;
		Autopilot(int i) {
			val = i;
		}
		int getVal() { return val;};
		
	}

	private float currentState = 0;
	public AutopilotControl(DataRefReader reader) {
		this.reader = reader;
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		setBounds(0, 0, 400, 350);
		
		btnAutopilot = new JToggleButton("AP");
		btnAutopilot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnAutopilot.isSelected()) {
					reader.setValue(AUTOPILOT_MODE, 2);
				}else {
					reader.setValue(AUTOPILOT_MODE, 0);
				}
			}
		});
		
		btnFlightDirector = new JToggleButton("FD");
		btnFlightDirector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnAutopilot.isSelected()) { /* Cannot set Flight Director mode */
					;
				}else {
					if (btnFlightDirector.isSelected()){
						reader.setValue(AUTOPILOT_MODE, 1);
					}
				}
			}
		});
		
		JPanel panelLateral = new JPanel();
		panelLateral.setBorder(new TitledBorder(null, "Lateral", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel panelVertical = new JPanel();
		panelVertical.setLayout(null);
		panelVertical.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vertical", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(8)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(btnAutopilot, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnFlightDirector, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelVertical, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addGap(70)
							.addComponent(panelLateral, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(19)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnAutopilot)
							.addGap(8)
							.addComponent(btnFlightDirector))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(panelVertical, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelLateral, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(95, Short.MAX_VALUE))
		);

		cbAirspeed = new JCheckBox("Airspeed hold w. pitch");
		cbAirspeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.AirspeedHoldWithPitchEngage.getVal());
			}
		});
		cbAirspeed.setBounds(6, 20, 218, 20);
		panelVertical.add(cbAirspeed);
		
		cbVviClimb = new JCheckBox("VVI Climb");
		cbVviClimb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.VVIClimbEngage.getVal());
				reader.setValue(AUTOPILOT_VVI_FPM, Float.parseFloat(txtVVI.getText()));
			}
		});
		cbVviClimb.setBounds(6, 40, 97, 23);
		panelVertical.add(cbVviClimb);
		
		cbALT = new JCheckBox("ALT");
		cbALT.setBounds(6, 60, 48, 23);
		cbALT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.AltitudeHoldArm.getVal());
				reader.setValue(AUTOPILOT_ALTITUDE, Integer.parseInt(txtALT.getText()));
			}
		});
		panelVertical.add(cbALT);
		
		txtALT = new JTextField();
		txtALT.setBounds(127, 61, 61, 20);
		panelVertical.add(txtALT);
		txtALT.setColumns(5);
		
		txtCurrentALT = new JTextField();
		txtCurrentALT.setBounds(194, 61, 61, 20);
		panelVertical.add(txtCurrentALT);
		txtCurrentALT.setEditable(false);
		txtCurrentALT.setColumns(5);
		
		cbALTActive = new JCheckBox("Active");
		cbALTActive.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.AltitudeHoldEngaged.getVal());
			}
		});
		cbALTActive.setEnabled(false);
		cbALTActive.setBounds(56, 60, 67, 23);
		panelVertical.add(cbALTActive);
		
		cbAPPR = new JCheckBox("APR");
		cbAPPR.setBounds(6, 80, 54, 23);
		cbAPPR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.GlideslopeArmed.getVal());
				if (cbNAV.isSelected() || cbNAVActive.isSelected()) {
					/* no action required */
				}else {
					reader.setValue(AUTOPILOT_STATE, Autopilot.HNAVArmed.getVal());
				}
			}
		});
		panelVertical.add(cbAPPR);
		
		cbAPPRActive = new JCheckBox("Active");
		cbAPPRActive.setEnabled(false);
		cbAPPRActive.setBounds(56, 80, 67, 23);
		panelVertical.add(cbAPPRActive);
		
		cbVerToga = new JCheckBox("Ver. TOGA");
		cbVerToga.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.VerticalTOGAEngaged.getVal());
			}
		});
		cbVerToga.setBounds(6, 100, 97, 23);
		panelVertical.add(cbVerToga);
		
		txtVVI = new JTextField();
		txtVVI.setColumns(5);
		txtVVI.setBounds(127, 41, 61, 20);
		txtVVI.setText("500");
		panelVertical.add(txtVVI);
		
		txtCurrentVVI = new JTextField();
		txtCurrentVVI.setEditable(false);
		txtCurrentVVI.setColumns(5);
		txtCurrentVVI.setBounds(194, 41, 61, 20);
		panelVertical.add(txtCurrentVVI);
		panelLateral.setLayout(null);
		
		cbHeading = new JCheckBox("HDG");
		cbHeading.setBounds(6, 20, 49, 23);
		panelLateral.add(cbHeading);
		
		txtHDG = new JTextField();
		txtHDG.setBounds(60, 21, 35, 20);
		panelLateral.add(txtHDG);
		txtHDG.setColumns(3);
		
		cbWingLeveler = new JCheckBox("Wing");
		cbWingLeveler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.WingLevelerEngage.getVal());
			}
		});
		cbWingLeveler.setBounds(6, 40, 72, 23);
		panelLateral.add(cbWingLeveler);
		
		cbNAV = new JCheckBox("NAV");
		cbNAV.setBounds(6, 60, 49, 23);
		cbNAV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.HNAVArmed.getVal());
			}
		});
		panelLateral.add(cbNAV);
		
		cbNAVActive = new JCheckBox("Active");
		cbNAVActive.setEnabled(false);
		cbNAVActive.setBounds(63, 60, 67, 23);
		panelLateral.add(cbNAVActive);
		
		cbHorToga = new JCheckBox("Hor. TOGA");
		cbHorToga.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.HorizontalTOGAEngaged.getVal());
			}
		});
		cbHorToga.setBounds(6, 80, 89, 23);
		panelLateral.add(cbHorToga);
		
		cbBC = new JCheckBox("BC");
		cbBC.setBounds(123, 20, 48, 23);
		panelLateral.add(cbBC);
		cbBC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbBC.isSelected())
					reader.setValue(AUTOPILOT_BACKCOURSE, 1);
				else
					reader.setValue(AUTOPILOT_BACKCOURSE, 0);
			}
		});
		cbHeading.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reader.setValue(AUTOPILOT_STATE, Autopilot.HeadingHoldEngage.getVal());
				reader.setValue(AUTOPILOT_HEADING, Integer.parseInt(txtHDG.getText()));
			}
		});
		setLayout(groupLayout);
		
   		reader.subscribe(AUTOPILOT_MODE);
   		reader.subscribe(AUTOPILOT_STATE);
   		reader.subscribe(AUTOPILOT_BACKCOURSE);
   		reader.subscribe(AUTOPILOT_ALTITUDE);
   		reader.subscribe(AUTOPILOT_CURRENT_ALTITUDE);
   		reader.subscribe(AUTOPILOT_APPROACH_SELECTOR); 
   		reader.subscribe(AUTOPILOT_AIRSPEED);
   		reader.subscribe(AUTOPILOT_HEADING); 
   		reader.subscribe(AUTOPILOT_FD_MODE);
   		reader.subscribe(AUTOPILOT_CURRENT_VVI_FPM);
   		
   		/* test: see as many vvi as possible */
   		reader.subscribe(AUTOPILOT_VVI_FPM);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtHDG;
	private JTextField txtALT;
	private JTextField txtCurrentALT;
	private JTextField txtVVI;
	private JTextField txtCurrentVVI;
	
	private void setBC(float value) {
		switch ((int)value) {
		case 1: cbBC.setSelected(true);
				break;
		case 0: cbBC.setSelected(false);
				break;
		}
	}
	private void setAutopilotMode(float value) {
		switch ((int)value) {
		case 0:
			btnAutopilot.setSelected(false);
			btnFlightDirector.setSelected(false);
			break;
		case 1:
			btnAutopilot.setSelected(false);
			btnFlightDirector.setSelected(true);
			break;
		case 2:
			btnAutopilot.setSelected(true);
			btnFlightDirector.setSelected(false);
			break;
		}
	}
	private void setAutopilotState(float value) {
		int state = (int)value;
		cbHeading.setSelected((state & Autopilot.HeadingHoldEngage.getVal()) == Autopilot.HeadingHoldEngage.getVal());
		cbALT.setSelected((state & Autopilot.AltitudeHoldArm.getVal()) == Autopilot.AltitudeHoldArm.getVal());
		cbALTActive.setSelected((state & Autopilot.AltitudeHoldEngaged.getVal()) == Autopilot.AltitudeHoldEngaged.getVal());
		if (cbALTActive.isSelected()) {
			cbALTActive.setEnabled(true);
		}else {
			cbALTActive.setEnabled(false);
		}
		cbAPPR.setSelected((state & Autopilot.GlideslopeArmed.getVal()) == Autopilot.GlideslopeArmed.getVal());
		cbAPPRActive.setSelected((state & Autopilot.GlideslopeEngaged.getVal()) == Autopilot.GlideslopeEngaged.getVal());
		cbNAV.setSelected((state & Autopilot.HNAVArmed.getVal()) == Autopilot.HNAVArmed.getVal()); 
		cbNAVActive.setSelected((state & Autopilot.HNAVEngaged.getVal()) == Autopilot.HNAVEngaged.getVal());
		cbAirspeed.setSelected((state & Autopilot.AirspeedHoldWithPitchEngage.getVal()) == Autopilot.AirspeedHoldWithPitchEngage.getVal());
		cbHorToga.setSelected((state & Autopilot.HorizontalTOGAEngaged.getVal()) == Autopilot.HorizontalTOGAEngaged.getVal());
		cbVerToga.setSelected((state & Autopilot.VerticalTOGAEngaged.getVal()) == Autopilot.VerticalTOGAEngaged.getVal());
		cbVviClimb.setSelected((state & Autopilot.VVIClimbEngage.getVal()) == Autopilot.VVIClimbEngage.getVal());
		cbWingLeveler.setSelected((state & Autopilot.WingLevelerEngage.getVal()) == Autopilot.WingLevelerEngage.getVal());
		currentState = value;
	}
	private void setALT(float value) {
		if (cbALT.isSelected()) {
			txtALT.setText(Integer.toString((int)value));
		}
	}
	private void setHDG(float value) {
		if (cbHeading.isSelected()) {
			txtHDG.setText(Integer.toString((int)value));
		}
	}
	private void setCurrentALT(float value) {
		txtCurrentALT.setText(Integer.toString((int)value));
	}
	private void setCurrentVVI(float value) {
		txtCurrentVVI.setText(Integer.toString((int)value));
	}
	private void setAPPR(float value) {
		switch ((int)value) {
		case 1: cbAPPR.setSelected(true);
				break;
		case 0: cbAPPR.setSelected(false);
				break;
		}
	}
	public void onDrefData(String name, float value) {
		if (name.equals(AUTOPILOT_MODE)) {
			setAutopilotMode(value);
		}
		else
		if (name.equals(AUTOPILOT_STATE)) {
			System.out.println("Autopilot state: " + value);
			setAutopilotState(value);
		}
		else
		if (name.equals(AUTOPILOT_BACKCOURSE)) {
			setBC(value);
		}
		else
		if (name.equals(AUTOPILOT_ALTITUDE)) {
			setALT(value);
		}
		else
		if (name.equals(AUTOPILOT_CURRENT_ALTITUDE)) {
			setCurrentALT(value);
		}
		else
		if (name.equals(AUTOPILOT_APPROACH_SELECTOR)) {
			setAPPR(value);
		}
		else
		if (name.equals(AUTOPILOT_AIRSPEED)) {
			System.out.println("Airspeed: " + value);
		}
		else
		if (name.equals(AUTOPILOT_HEADING)) {
			setHDG(value);
		}
		else
		if (name.equals(AUTOPILOT_CURRENT_VVI_FPM)) {
			setCurrentVVI(value);
		}
	}
}
