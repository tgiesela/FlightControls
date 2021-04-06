package data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataReader {

	private DatagramSocket socket;
	private byte[] data = new byte[4096];
	private static int XPLANE_MESSAGELEN = 36;
	private static int XPLANE_HEADERLEN = 5;
	private static int XPLANE_DREFNAMELEN = 400;
	
	class Angular_velocities{
		float pitchrate;
		float rollrate;
		float yawrate;
	}
	class Pitch_roll_headings{
		float pitch;
		float roll;
		float hdg;
		float maghdg;
	}
	Angular_velocities s_angular_velcoties = new Angular_velocities();
	Pitch_roll_headings s_pitch_roll_headings = new Pitch_roll_headings();
	
	public DataReader() {
		try {
			socket = new DatagramSocket(49003);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	public void doRead() {
	/*
		 The first five bytes of each UDP packet you receive should be constant. 
		 The first four bytes should form the ASCII string "DATA". The fifth byte can simply be ignored. 
		 What's left should be a multiple of 36 bytes. 
		 Each 36 byte record corresponds to a single entry from the "Data Set" screen.

		 Each 36 byte record can be broken down into a series of 9 items, each of which is 4 bytes long (9 * 4 = 36). 
		 The first four bytes are an integer. 
		 This integer corresponds to the number beside each item on the "Data Set" screen. 
		 The remaining 4-byte items in each record (all 8 of them) are the actual data from the flight simulator. 
		 Here is the complete packet format:

			(Note that all data is in network-byte order!)

			"DATA" (4-byte ASCII string) | pad character (1-byte) | record #1 (36 bytes) | record #2 (36 bytes) | record #3...

		 And the format of each record:

			data identifier (4-byte integer) | data item #1 (4 bytes) | data item #2 (4 bytes) | ... | data item #8 (4 bytes)
	*/
		boolean running = true;
		while (running) {
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				//InetAddress address = packet.getAddress();
				//int port = packet.getPort();
				String msgheader = new String(data,0,XPLANE_HEADERLEN-1); 
				if (msgheader.equals("DATA")) {
					//String messages = new String(data,XPLANE_HEADERLEN,data.length-XPLANE_HEADERLEN);
					ByteBuffer bb = ByteBuffer.wrap(data);
					bb.rewind();
					bb.position(5);
					for (int i=0;i<(data.length-XPLANE_HEADERLEN)/XPLANE_MESSAGELEN;i++) {
						//String message = new String(data,XPLANE_HEADERLEN+i*XPLANE_MESSAGELEN,XPLANE_MESSAGELEN);
						byte[] bytemessage = new byte[XPLANE_MESSAGELEN];
						bb.get(bytemessage,0,XPLANE_MESSAGELEN);
						parseMessage(ByteBuffer.wrap(bytemessage));
						//System.out.println(message);
					}
					//System.out.println("Packet received from :" + address.getHostAddress() + ":" + port);
					//System.out.println(new String(packet.getData()));
				} else {
					if (msgheader.equals("DREF")) {
						ByteBuffer bb = ByteBuffer.wrap(data);
						bb.rewind();
						bb.position(5);
						bb.order(ByteOrder.LITTLE_ENDIAN);
						int val = bb.getInt();
						//String message = new String(data,XPLANE_HEADERLEN+i*XPLANE_MESSAGELEN,XPLANE_MESSAGELEN);
						byte[] bytemessage = new byte[XPLANE_DREFNAMELEN];
						bb.get(bytemessage,0,XPLANE_DREFNAMELEN);
						System.out.println(new String(bytemessage).trim() + ": " + val);
						//System.out.println(message);
					}
				}
			} catch (IOException e) {
				running = false;
				e.printStackTrace();
			}
           
		}
	}
	private void parseMessage(ByteBuffer bbuf) {
		int type;
		bbuf.order(ByteOrder.LITTLE_ENDIAN);
		type = bbuf.getInt();
		switch (type) {
		case 16:	angular_velocities(bbuf);
					break;
		case 17:	pitch_roll_headings(bbuf);
					break;
		case 96:	com1_com2_freq(bbuf);
					break;
		case 97:	nav1_nav2_freq(bbuf);
					break;
		case 98:	nav1_nav2_obs(bbuf);
					break;
		case 99:	nav1_deflections(bbuf);
					break;
		case 100:	nav2_deflections(bbuf);
					break;
		case 116:	autopilot_armed_status(bbuf);
					break;
		case 117:	autopilot_modes(bbuf);
					break;
		case 118:	autopilot_values(bbuf);
					break;
		default:
					//System.out.println("Type: " + type);
					break;
		}
		/* 16: angular velocities (Q, P, R) */
		/* 17: pitch, roll, headings (pitch, roll,*/
	}
	private void nav2_deflections(ByteBuffer bbuf) {
		// TODO Auto-generated method stub
		
	}
	private void nav1_deflections(ByteBuffer bbuf) {
		// TODO Auto-generated method stub
		
	}
	private void nav1_nav2_obs(ByteBuffer bbuf) {
		// TODO Auto-generated method stub
		
	}
	private void nav1_nav2_freq(ByteBuffer bbuf) {
		// TODO Auto-generated method stub
		
	}
	private void com1_com2_freq(ByteBuffer bbuf) {
		// TODO Auto-generated method stub
		
	}
	private void autopilot_values(ByteBuffer bbuf) {
		/*
		 * set, speed	The desired speed setting for the autopilot.
		 * set, hding	The desired heading setting for the autopilot.
		 * set, vvi		The desired vertical velocity setting for the autopilot.
		 * dial, alt	
		 * vnav, alt	
	     * use, alt	
		 * sync, roll	
		 * sync, pitch	
		 */
		float set_speed	 = bbuf.getFloat();
		float set_hding	 = bbuf.getFloat();
		float set_vvi	 = bbuf.getFloat();
		float dial_alt	 = bbuf.getFloat();
		float vnav_alt	 = bbuf.getFloat();
		float use_alt	 = bbuf.getFloat();
		float sync_roll	 = bbuf.getFloat();
		float sync_pitch = bbuf.getFloat();
		System.out.println("set_speed: " +	set_speed	);
		System.out.println("set_hding: " +	set_hding	);
		System.out.println("set_vvi: " +   set_vvi	    );
		System.out.println("dial_alt: " +	dial_alt	);
		System.out.println("vnav_alt: " +	vnav_alt	);
		System.out.println("use_alt: " +   use_alt	    );
	    System.out.println("sync_roll: " +	sync_roll	);
		System.out.println("sync_pitch: " + sync_pitch  );
		
	}
	private void autopilot_modes(ByteBuffer bbuf) {
		/*
		 * auto, throt
		 * mode, hding
		 * mode, alt	
		 * bac, 0/1	 (backcourse on/off)
		 * app,	
		 * sync, butn	
		 */
		 float auto_throt =   bbuf.getFloat();
		 float mode_hding =   bbuf.getFloat();
		 float mode_alt	  =   bbuf.getFloat();
		 float notused1	  =   bbuf.getFloat();
		 float bac_0_1	  =   bbuf.getFloat();
		 float app	      =   bbuf.getFloat();
		 float notused2	  =   bbuf.getFloat();
		 float sync_butn  =   bbuf.getFloat();
		 
		 System.out.println("auto_throt: " + auto_throt); 
		 System.out.println("mode_hding: " + mode_hding); 
		 System.out.println("mode_alt:	"  + mode_alt); 
		 System.out.println("bac_0_1:	"  + bac_0_1); 
		 System.out.println("app: "        + app); 
		 System.out.println("sync_butn : " + sync_butn);	
	}
	private void autopilot_armed_status(ByteBuffer bbuf) {
		/*
		 * nav, arm	•
         * alt, arm	•
         * app, arm	•
         * vnav, enab	•
         * vnav, arm	•
         * vnav, time	•
         * gp, enabl	•
         * app, typ	•
		 */
		 float nav_arm = bbuf.getFloat();
         float alt_arm = bbuf.getFloat();
         float app_arm = bbuf.getFloat();
         float vnav_enab = bbuf.getFloat();
         float vnav_arm = bbuf.getFloat();
         float vnav_time = bbuf.getFloat();
         float gp_enabl = bbuf.getFloat();
         float app_typ = bbuf.getFloat();
		
		 System.out.println("nav_arm: "   + nav_arm );
         System.out.println("alt_arm: "   + alt_arm);
         System.out.println("app_arm: "   + app_arm);
         System.out.println("vnav_enab: " + vnav_enab);
         System.out.println("vnav_arm: "  + vnav_arm);
         System.out.println("vnav_time: " + vnav_time);
         System.out.println("gp_enabl: "  + gp_enabl);
         System.out.println("app_typ: "   + app_typ);
	}
	private void pitch_roll_headings(ByteBuffer bbuf) {
	/*
		pitch, deg	The aircraft’s pitch, measured in body-axis Euler angles.
		roll, deg	The aircraft’s roll, measured in body-axis Euler angles.
		hding, true	The aircraft’s true heading, measured in body-axis Euler angles.
		hding, mag	The aircraft’s magnetic heading, in degrees.	
	*/
		float pitch = bbuf.getFloat();
		float roll = bbuf.getFloat();
		float hdg = bbuf.getFloat();
		float maghdg = bbuf.getFloat();

		if ((Math.abs((pitch - s_pitch_roll_headings.pitch)) > 0.05) ||
			(Math.abs((roll - s_pitch_roll_headings.roll)) > 0.05) ||	
			(Math.abs((hdg - s_pitch_roll_headings.hdg)) > 0.05) ||
			(Math.abs((maghdg - s_pitch_roll_headings.maghdg)) > 0.05)) {
			System.out.println("Pitch, deg: " + pitch);
			System.out.println("Roll, deg: " + roll);
			System.out.println("Hding, true: " + hdg);
			System.out.println("Hding, mag: " + maghdg);
		}
		
		s_pitch_roll_headings.pitch = pitch;
		s_pitch_roll_headings.roll = roll;
		s_pitch_roll_headings.hdg = hdg;
		s_pitch_roll_headings.maghdg = maghdg;
	}
	private void angular_velocities(ByteBuffer bbuf) {
		/* 
		 * Q, rad/s	Pitch rate, measured in body-axes (when all is working as it should).
		   P, rad/s	Roll rate, measured in body-axes (when all is working as it should).
		   R, rad/s	Yaw rate, measured in body-axes (when all is working as it should). 
		*/
		float pitchrate = bbuf.getFloat();
		float rollrate = bbuf.getFloat();
		float yawrate = bbuf.getFloat();
		if ((Math.abs((pitchrate - s_angular_velcoties.pitchrate)) > 0.05) ||
			(Math.abs((rollrate - s_angular_velcoties.rollrate)) > 0.05) ||	
			(Math.abs((yawrate - s_angular_velcoties.yawrate)) > 0.05)) {
			System.out.println("Q, rad/s: " + pitchrate);
			System.out.println("P, rad/s: " + rollrate );
			System.out.println("R, rad/s: " + yawrate);
		}
		s_angular_velcoties.pitchrate = pitchrate;
		s_angular_velcoties.rollrate = rollrate;
		s_angular_velcoties.yawrate = yawrate;
	}
	public static void main(String[] args) {
		DataReader reader = new DataReader();
		reader.doRead();
	}

}
