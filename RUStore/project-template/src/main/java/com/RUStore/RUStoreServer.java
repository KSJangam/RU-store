package com.RUStore;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
		HashMap<String, byte[]> objects = new HashMap<String, byte[]>();
		// Check if at least one argument that is potentially a port number
		if(args.length != 1) {
			System.out.println("Invalid number of arguments. You must provide a port number.");
			return;
		}

		// Try and parse port # from argument
		int port = Integer.parseInt(args[0]);
		ServerSocket svc = new ServerSocket(port, 5);
		
			Socket conn = svc.accept();	 // wait for a connection
	
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());
			DataInputStream dataFromClient = new DataInputStream(conn.getInputStream());
			String line;
			line = fromClient.readLine();	// read the data from the client
			System.out.println("got line \"" + line + "\"");	// show what we got

			String response = "Acknowledged : " + line + '\n';	// do the work

			toClient.writeBytes(response);	// send the result
			
			while  ((line = fromClient.readLine()) != null) {	// read the data from the client
				System.out.println("got requiest " + line.substring(0,3));	// show what we got
				String req=line.substring(0,3);
				if(req.equals("put")) {
					if(objects.containsKey(line.substring(3))) {
						toClient.writeBytes("e");
						objects.put(line.substring(3), dataFromClient.readAllBytes());
					}
					else {
						toClient.writeBytes("ne");
					}
				}
				else if(req.equals("get")) {
					if(objects.containsKey(line.substring(3))) {
						toClient.writeBytes("e");
						toClient.write(objects.get(line.substring(3)));
					}
					else
						toClient.writeBytes("ne");
				}
				else if(req.equals("lst")) {
					toClient.writeInt(objects.keySet().size());	// send the result
					for(String k:objects.keySet()) {
						toClient.writeBytes(k);
					}
				}
				else if(req.equals("rem")) {
					if(objects.containsKey(line.substring(3))) {

						toClient.writeBytes("e");
						objects.remove(line.substring(3));
					}
					else
						toClient.writeBytes("ne");
				}
				
			}
			

			System.out.println("closing the connection");
			conn.close();
			
			
			svc.close();// close connection
		

	}

}
