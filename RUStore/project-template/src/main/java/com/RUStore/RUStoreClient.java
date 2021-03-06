package com.RUStore;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

/* any necessary Java paages here */

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
		byte[] b = new byte["accepted".getBytes().length];
		sock = new Socket(host, port);
		String line = "connecting";
		
		toServer = new DataOutputStream(sock.getOutputStream());
		fromServer = new DataInputStream(sock.getInputStream());
		toServer.write(line.getBytes());
		fromServer.read(b);
		String response = new String(b);
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
		byte[] b = new byte[1];
		String line = "put"+key;
		toServer.writeInt(line.length());
		fromServer.read(b);
		toServer.write(line.getBytes());		
		fromServer.read(b);
		String response = new String(b);
		System.out.println("got response "+response);
		if(response.equals("e")) {
			return 1;
		}
		else {
			toServer.writeInt(data.length);
			fromServer.read(b);
			toServer.write(data);
			fromServer.read(b);
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
		byte[] b = new byte[1];
		String line = "put"+key;
		toServer.writeInt(line.length());
		fromServer.read(b);
		toServer.write(line.getBytes());		// send the line to the server
		fromServer.read(b);
		String response = new String(b);
		if(response.equals("e")) {
			fromFile.close();
			return 1;
		}
		else {
			toServer.writeInt((int)new File(file_path).length());
			byte[]b2=new byte[(int) new File(file_path).length()];
			fromServer.read(b);
			fromFile.read(b2);
			toServer.write(b2);
			fromServer.read(b);
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
		byte[] b = new byte[1];
		String line = "get"+key;
		System.out.println(line);
		toServer.writeInt(line.length());
		fromServer.read(b);
		toServer.write(line.getBytes());		// send the line to the server
		fromServer.read(b);
		String response = new String(b);
		if(response.equals("n")) {
			return null;
		}
		else {
			toServer.write("a".getBytes());
			byte[] b2 = new byte[fromServer.readInt()];
			toServer.write("a".getBytes());
			fromServer.read(b2);
			return b2;
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
		byte[] b = new byte[1];
		File file = new File(file_path);
		file.createNewFile();
		DataOutputStream toFile = new DataOutputStream(new FileOutputStream(file));
		String line = "get"+key;
		System.out.println(line);
		toServer.writeInt(line.length());
		fromServer.read(b);
		toServer.write(line.getBytes());		// send the line to the server
		fromServer.read(b);
		String response = new String(b);
		if(response.equals("n")) {
			toFile.close();
			return 1;
		}
		else {
			toServer.write("a".getBytes());
			byte[] b2 = new byte[fromServer.readInt()];
			toServer.write("a".getBytes());
			fromServer.read(b2);
			toFile.write(b2);
			toFile.close();
			return 0;
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
		byte[] b= new byte[1];
		String line = "rem"+key;
		toServer.writeInt(line.length());
		fromServer.read(b);
		toServer.write(line.getBytes());		// send the line to the server
		fromServer.read(b);
		String response = new String(b);
		// Implement here
		if(response.equals("n")) {
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
		byte[]b=new byte[1];
		toServer.writeInt(3);
		fromServer.read(b);
		toServer.write("lst".getBytes());	
		int size = fromServer.readInt();
		toServer.write("a".getBytes());
		System.out.println("size: "+size);
		if(size == 0) return null;
		String[] keys = new String[size];
		for(int i=0; i<size; i++) {
			b=new byte[fromServer.readInt()];

			toServer.write("a".getBytes());
			fromServer.read(b);
			keys[i] = new String(b);

			toServer.write("a".getBytes());
			System.out.println(i+": "+keys[i]);
		}
		fromServer.read(b);
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
		byte[]b = new byte[1];
		toServer.writeInt(3);
		fromServer.read(b);
		toServer.write("dsc".getBytes());	
		toServer.close();
		fromServer.close();
		sock.close();
		// Implement here

	}

}
