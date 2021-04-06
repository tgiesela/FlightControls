package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import interfaces.DrefEventListener;

public class DataRefReader {
	/*
	 * See: https://developer.x-plane.com/datarefs/
	 */

	private DatagramSocket datarefsocket;
	private InetAddress xPlaneIPAddress;

	private byte[] data = new byte[4096];
	private static int XPLANE_HEADERLEN = 5;
	private List<DataRefDef> datatypes = new ArrayList<>();
	private List<DataRefDef> subscriptions = new ArrayList<>();
	enum DataRefType{
		INTEGER,
		FLOAT,
		BYTE,
		DOUBLE,
		INTEGER_ARRAY,
		FLOAT_ARRAY,
		BYTE_ARRAY,
		DOUBLE_ARRAY;
	};
	enum State{
		ENABLED,
		DISABLED;
	}
	private class DataRefDef{
		String name;
		DataRefType type;
		int id;
		boolean writeable;
		int occurs;
	}
	
	private DrefEventListener mListener = null;

	public DataRefReader() {
		try {
			//socket = new DatagramSocket(49010);
			try {
				datarefsocket = new DatagramSocket(49004);
				xPlaneIPAddress = InetAddress.getByName("10.56.57.65");
				readDataRefs();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				return;
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	public void registerDrefEventListener(DrefEventListener listener) {
		this.mListener = listener;
	}
	public void doRead() {
		boolean running = true;
		DataRefDef dref;
		while (running) {
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				datarefsocket.receive(packet);
				String msgheader = new String(data,0,XPLANE_HEADERLEN-1); 
				if (msgheader.equals("RREF")) {

					ByteBuffer bb = ByteBuffer.wrap(packet.getData(),0, packet.getLength());
					bb.order(ByteOrder.LITTLE_ENDIAN);
					bb.rewind();
					bb.position(5);
					while (bb.hasRemaining()) {
					    int id = bb.getInt();
					    dref = lookupDataRefDef(id);
					    if (dref != null) {
					        if (dref.type == DataRefType.INTEGER_ARRAY) {
					        	if (mListener != null) {
					        		mListener.onDrefData(dref.name, bb.getInt());
					        	}else {
					        		System.out.println(dref.name + ": " + bb.getInt());
					        	}
					        } else if (dref.type == DataRefType.FLOAT_ARRAY ||
					        		   dref.type == DataRefType.INTEGER ||
					        		   dref.type == DataRefType.FLOAT) {
					        	if (mListener != null) {
					        		mListener.onDrefData(dref.name, bb.getFloat());
					        	}else {
					        		System.out.println(dref.name + ": " + bb.getFloat());
					        	}
					        } else if (dref.type == DataRefType.BYTE_ARRAY ||
					        		   dref.type == DataRefType.BYTE) {
					        	if (mListener != null) {
					        		mListener.onDrefData(dref.name, bb.get());
					        	}else {
					        		System.out.println(dref.name + ": " + bb.get());
					        	}
					        } else if (dref.type == DataRefType.DOUBLE_ARRAY ||
					        		   dref.type == DataRefType.DOUBLE) {
					        	if (mListener != null) {
					        		mListener.onDrefData(dref.name, bb.getDouble());
					        	}else {
					        		System.out.println(dref.name + ": " + bb.getDouble());
					        	}
					        } else {
					        	System.out.println(dref.name + ": Don't know how to handle datatype!!");
					        }
					    }
					}
				}
			} catch (IOException e) {
				running = false;
				e.printStackTrace();
			}
		}
	}
	private void setDataRefs(DataRefDef dref, State state) {
		/*
		So this one is cool: Send in the 5 chars RREF (null-terminated!) plus this struct:
			struct dref_struct_in
			{
			    xint dref_freq        ;
			    xint dref_en        ;
			    xchr dref_string[400]    ;
			};
		*/
		byte[] buf = new byte[413];
		byte zero = 0;
		ByteBuffer bb = ByteBuffer.allocate(buf.length);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put("RREF".getBytes());
		bb.put(zero);
		if (state == State.ENABLED)
			bb.putInt(5); /* 2 time(s) per second */
		else {
			bb.putInt(0);
		}
		bb.putInt(dref.id); /* Code as reference */
		bb.put(dref.name.getBytes());
		buf = bb.array();
		DatagramPacket packet = new DatagramPacket(buf, buf.length,xPlaneIPAddress, 49000); /* send to port 49000 */
		try {
			datarefsocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readDataRefs() {
		String name;
		StringTokenizer st;
		DataRefDef type;
		String datatype;
		int typecount=0;
		String writable;
		try  
		{  
			File file=new File("E:\\Games\\X Plane 11\\Resources\\plugins\\DataRefs.txt"); //creates a new file instance  
			FileReader fr=new FileReader(file);   //reads the file  
			BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
			String line;  
			while((line=br.readLine())!=null)  
			{  
				type = new DataRefDef();
				st = new StringTokenizer(line);
				if (st.hasMoreTokens())
					name = st.nextToken();
				else
					name = "";
				if (name.startsWith("sim/")) {
					typecount++;
				    type.name = name;
				    datatype = st.nextToken();
				    writable = st.nextToken();
				    if (datatype.startsWith("int[")) {
				    	type.type = DataRefType.INTEGER_ARRAY;
				    	type.occurs = getOccurs(datatype);
				    }
				    else if (datatype.startsWith("float[")) {
				    	type.type = DataRefType.FLOAT_ARRAY;
				    	type.occurs = getOccurs(datatype);
				    }
				    else if (datatype.startsWith("byte[")) { 
				    	type.type = DataRefType.BYTE_ARRAY;
				    	type.occurs = getOccurs(datatype);
				    }
				    else if (datatype.startsWith("double[")) { 
				    	type.type = DataRefType.DOUBLE_ARRAY;
				    	type.occurs = getOccurs(datatype);
					}
				    else if (datatype.equals("int")) 
				    	type.type = DataRefType.INTEGER;
				    else if (datatype.equals("float")) 
				    	type.type = DataRefType.FLOAT;
				    else if (datatype.equals("byte")) 
				    	type.type = DataRefType.BYTE;
				    else if (datatype.equals("double")) 
				    	type.type = DataRefType.DOUBLE;
				    else {
				    	System.out.println("Unexpected datatype: " + datatype);
				    	System.out.println("In: " + name);
				    	System.exit(-1);
				    }
				    type.id = typecount;
				    if (writable.equals("y")) {
				    	type.writeable = true;
				    }else {
				    	type.writeable = false;
				    }
				    datatypes.add(type);
				}
			}  
			fr.close();    //closes the stream and release the resources  
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
	}
	private int getOccurs(String s) {
		int occurs = 0;
		int open = s.indexOf('[');
		int close = s.indexOf(']');
		String substring;
    	try {
    		occurs = Integer.parseInt(s.substring(open+1,close));
    		/* check for multidimensional array */
    		substring = s.substring(close+1);
    		open = substring.indexOf('[');
    		if (open > -1) {
    			close = substring.indexOf(']');
        		occurs = occurs * Integer.parseInt(substring.substring(open+1,close));
    		}
    	} catch (NumberFormatException e) {
    		System.out.println("NumberFormatException: " + e.getMessage());
    	}

		return occurs;
	}
	public void subscribe(String item) {
		String itemname;
		int bracket = item.indexOf('[');
		if (bracket > 0) {
			itemname = item.substring(0, bracket);
		}else {
			itemname = item;
		}
		for (DataRefDef dref: datatypes){
			if (dref.name.equalsIgnoreCase(itemname)) {
				subscriptions.add(dref);
				setDataRefs(dref, State.ENABLED);
				break;
			}
		}
	}
	private DataRefDef lookupDataRefDef(int id) {
		for (DataRefDef dref: subscriptions) {
			if (dref.id == id) {
				return dref;
			}
		}
		return null;
	}
	public void unsubscribe(String item) {
		String itemname;
		int bracket = item.indexOf('[');
		if (bracket > 0) {
			itemname = item.substring(0, bracket);
		}else {
			itemname = item;
		}
		DataRefDef toremove = null;
		for (DataRefDef dref: subscriptions){
			if (dref.name.equalsIgnoreCase(itemname)) {
				subscriptions.add(dref);
				setDataRefs(dref, State.DISABLED);
				toremove = dref;
				break;
			}
		}
		subscriptions.remove(toremove);
	}
	public void unsubscribeAll() {
		for (DataRefDef dref: subscriptions){
			setDataRefs(dref, State.DISABLED);
		}
		subscriptions.clear();
	}
	private void discoverXplane() {
		MulticastSocket socket = null;
		byte[] buf = new byte[256];
		try {
        	System.out.println("Waiting for X-Plane instance...");
			socket = new MulticastSocket(49707);
			InetAddress group = InetAddress.getByName("239.255.1.1");
			socket.joinGroup(group);
			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            String received = new String(packet.getData(), 0, packet.getLength());
				if (received.startsWith("BECN")) {

					ByteBuffer bb = ByteBuffer.wrap(packet.getData(),0, packet.getLength());
					bb.order(ByteOrder.LITTLE_ENDIAN);
					bb.rewind();
					bb.position(5);
					byte beacon_major_version = bb.get();
					byte beacon_minor_version = bb.get();
					int application_host_id = bb.getInt();
					int xplane_version_nr = bb.getInt();
					int role = bb.getInt();
					int port = bb.getShort() & 0x0000FFFF;
					byte[] bytename = new byte[bb.remaining()];
					bb.get(bytename);
					String name = new String(bytename,0,bytename.length);
					
	            	System.out.println("X-Plane instance found on: " + name + ":" + port);
	            	break;
				}
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void setValue(String item, float value) {
		/* See instructions folder of X-PLANE*/
		byte[] buf = new byte[509];
		byte zero = 0;
		ByteBuffer bb = ByteBuffer.allocate(buf.length);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.put("DREF".getBytes());
		bb.put(zero);
		bb.putFloat(value); 
		bb.put(item.getBytes());
		bb.put(zero);
		buf = bb.array();
		DatagramPacket packet = new DatagramPacket(buf, buf.length,xPlaneIPAddress, 49000); /* send to port 49000 */
		try {
			datarefsocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		DataRefReader reader = new DataRefReader();
		reader.discoverXplane();
//		reader.subscribe("sim/cockpit2/radios/actuators/audio_selection_com1");
		reader.subscribe("sim/cockpit/gyros/the_ind_ahars_pilot_deg"); //pitch (omhoog/omlaag)
		reader.subscribe("sim/cockpit/gyros/phi_ind_ahars_pilot_deg"); //roll (links/rechts)
		reader.subscribe("sim/flightmodel/position/beta"); // heading relative to flown path
//		reader.subscribe("sim/flightmodel2/gear/on_ground");
//		reader.subscribe("sim/aircraft/view/acf_descrip");
		reader.doRead();
//		reader.unsubscribe("sim/cockpit2/radios/actuators/audio_selection_com1");
		reader.unsubscribeAll();
	}
	
}
