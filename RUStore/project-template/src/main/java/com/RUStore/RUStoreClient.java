package com.RUStore;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;

/* any necessary Java packages here */

public class RUStoreClient {

	/* any necessary class members here */

	/**
	 * RUStoreClient Constructor, initializes default values
	 * for class members
	 *
	 * @param host	host url
	 * @param port	port number
	 */
	private Socket sock;
	private String host;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private int port;
	public RUStoreClient(String host, int port) {
		this.host=host;
		this.port=port;
		// Implement here

	}

	/**
	 * Opens a socket and establish a connection to the object store server
	 * running on a given host and port.
	 *
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 */
	public void connect() throws IOException {
	
		sock = new Socket(host, port);
		String line = "test2";
		
		toServer = new DataOutputStream(sock.getOutputStream());
		fromServer = new DataInputStream(sock.getInputStream());
		
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		System.out.println(response);	
	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT be 
	 * overwritten
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param data	byte array representing arbitrary data object
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, byte[] data) throws IOException {
		System.out.println("putting");
		String line = "put"+key;
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		
		// Implement here
		if(response.equals("e")) {
			return 1;
		}
		else {
			toServer.write(data);
			return 0;
		}

	}

	/**
	 * Sends an arbitrary data object to the object store server. If an 
	 * object with the same key already exists, the object should NOT 
	 * be overwritten.
	 * 
	 * @param key	key to be used as the unique identifier for the object
	 * @param file_path	path of file data to transfer
	 * 
	 * @return		0 upon success
	 *        		1 if key already exists
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int put(String key, String file_path) throws IOException {
		DataInputStream fromFile = new DataInputStream(new FileInputStream(file_path));
		System.out.println("putting");
		String line = "put"+key;
		
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		
		if(response.equals("e")) {
			fromFile.close();
			return 1;
		}
		else {
			toServer.write(fromFile.readAllBytes());
			fromFile.close();
			return 0;
		}
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server.
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		object data as a byte array, null if key doesn't exist.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 */
	public byte[] get(String key) throws IOException {
		System.out.println("getting");
		String line = "get"+key;
		System.out.println(line);
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		System.out.println(response);
		// Implement here
		if(response.equals("ne")) {
			return null;
		}
		else {
			toServer.writeBytes("ack");
			return fromServer.readAllBytes();
		}
	}

	/**
	 * Downloads arbitrary data object associated with a given key
	 * from the object store server and places it in a file. 
	 * 
	 * @param key	key associated with the object
	 * @param	file_path	output file path
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int get(String key, String file_path) throws IOException {
		DataOutputStream toFile = new DataOutputStream(new FileOutputStream(file_path));
		System.out.println("getting");
		String line = "get"+key;
		
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		
		// Implement here
		if(response.equals("ne")) {
			toFile.close();
			return 1;
		}
		else { 
			toServer.writeBytes("ack");
			toFile.write(fromServer.readAllBytes());
			toFile.close();
			return 1;
		}
		
	}

	/**
	 * Removes data object associated with a given key 
	 * from the object store server. Note: No need to download the data object, 
	 * simply invoke the object store server to remove object on server side
	 * 
	 * @param key	key associated with the object
	 * 
	 * @return		0 upon success
	 *        		1 if key doesn't exist
	 *        		Throw an exception otherwise
	 * @throws IOException 
	 */
	public int remove(String key) throws IOException {

		System.out.println("removing");
		String line = "rem"+key;
		toServer.writeBytes(line);		// send the line to the server
		String response = new String(fromServer.readAllBytes());	// read a one-line result
		
		// Implement here
		if(response.equals("ne")) {
			return 1;
		}
		else {
			return 0;
		}

	}

	/**
	 * Retrieves of list of object keys from the object store server
	 * 
	 * @return		List of keys as string array, null if there are no keys.
	 *        		Throw an exception if any other issues occur.
	 * @throws IOException 
	 */
	public String[] list() throws IOException {
		System.out.println("list");
		
		toServer.writeBytes("lst"+'\n');	
		int size = new BigInteger(fromServer.readAllBytes()).intValue();	
		System.out.println("size: "+size);
		if(size == 0) return null;
		String[] keys = new String[size];
		for(int i=0; i<size; i++) {
			keys[i]=new String(fromServer.readAllBytes());
			toServer.writeBytes("ack");
			System.out.println(i+": "+keys[i]);
		}
		//first send lst message
		//then get size of array and initialize array
		//use a for loop to read in all the Strings
		return keys;

	}

	/**
	 * Signals to server to close connection before closes 
	 * the client socket.
	 * 
	 * @return		n/a, however throw an exception if any issues occur
	 * @throws IOException 
	 */
	public void disconnect() throws IOException {
		toServer.writeBytes("dsc"+'\n');	
		toServer.close();
		fromServer.close();
		sock.close();
		// Implement here

	}

}
