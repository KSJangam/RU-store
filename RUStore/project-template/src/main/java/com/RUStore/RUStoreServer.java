package com.RUStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/* any necessary Java packages here */

public class RUStoreServer {

	/* any necessary class members here */

	/* any necessary helper methods here */

	/**
	 * RUObjectServer Main(). Note: Accepts one argument -> port number
	 * @throws IOException 
	 */
	
	public static void main(String args[]) throws IOException{
		// Check if at least one argument that is potentially a port number
		if(args.length != 1) {
			System.out.println("Invalid number of arguments. You must provide a port number.");
			return;
		}
		System.out.println("Starting");
		// Try and parse port # from argument
		int port = Integer.parseInt(args[0]);
		ServerSocket svc = new ServerSocket(port, 5);
		for(;;) {
			HashMap<String, byte[]> objects = new HashMap<String, byte[]>();
			//objects.put("wah", new ByteArray("wahoo".getBytes()));
			Socket conn = svc.accept();	 // wait for a connection
	
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
			 
			DataInputStream fromClient = new DataInputStream(conn.getInputStream());
			String line;
			byte[] b= new byte["connecting".getBytes().length];
			fromClient.read(b);
			line = new String(b);	// read the data from the client
			System.out.println("got connection");	// show what we got

			String response = "accepted";	// do the work
			
			toClient.write(response.getBytes());	// send the result
			b=new byte[fromClient.readInt()];
			toClient.write("a".getBytes());
			while  (fromClient.read(b)!=-1) {	// read the data from the client
				line = new String(b);
				byte[] b2=new byte[1];
				String req=line.substring(0,3);
				if(req.equals("put")) {
					if(!objects.containsKey(line.substring(3))) {
						toClient.write("n".getBytes());
						byte[] b3 = new byte[fromClient.readInt()];
						toClient.write("a".getBytes());
						fromClient.read(b3);
						objects.put(line.substring(3), b3);
						toClient.write("a".getBytes());
					}
					else {
						toClient.write("e".getBytes());
					}
				}
				else if(req.equals("get")) {
					if(objects.containsKey(line.substring(3))) {
						toClient.write("e".getBytes());
						fromClient.read(b2);
						toClient.writeInt(objects.get(line.substring(3)).length);
						fromClient.read(b2);
						toClient.write(objects.get(line.substring(3)));
					}
					else
						toClient.write("n".getBytes());
				}
				else if(req.equals("lst")) {
					toClient.writeInt(objects.keySet().size());	
					fromClient.read(b2);
					for(String k:objects.keySet()) {
						toClient.writeInt(k.length());
						fromClient.read(b2);
						toClient.write(k.getBytes());
						fromClient.read(b2);
					}
					toClient.write("a".getBytes());
				}
				else if(req.equals("rem")) {
					if(objects.containsKey(line.substring(3))) {
						objects.remove(line.substring(3));
						toClient.write("e".getBytes());
					}
					else
						toClient.write("n".getBytes());
				}
				else if (req.equals("dsc")) {
					System.out.println("closing the connection");
					fromClient.close();
					toClient.close();
					conn.close();
					svc.close();
					break;
				}
				b=new byte[fromClient.readInt()];
				toClient.write("a".getBytes());
			}
			

			break;
			
		}
			//svc.close();// close connection
		

	}

}
