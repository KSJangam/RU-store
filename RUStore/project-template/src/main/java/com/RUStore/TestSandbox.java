package com.RUStore;

import java.io.IOException;

/**
 * This TestSandbox is meant for you to implement and extend to 
 * test your object store as you slowly implement both the client and server.
 * 
 * If you need more information on how an RUStorageClient is used
 * take a look at the RUStoreClient.java source as well as 
 * TestSample.java which includes sample usages of the client.
 */
public class TestSandbox{

	public static void main(String[] args) {
		int i=-1;
		// Create a new RUStoreClient
		RUStoreClient client = new RUStoreClient("localhost", 12345);

		// Open a connection to a remote service
		System.out.println("Connecting to object server...");
		try {
			client.connect();
			System.out.println("Established connection to server.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to connect to server.");
		}
		
		try {
			i = client.put("wah", "wahoo".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i==0) {
			System.out.println("wah Success");
		}
		try {
			i = client.put("wah", "wahoo".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i!=0)System.out.println("wah already exists");
		try {
			i = client.put("woh", "wahoo".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i==0)System.out.println("woh success");
		try {
			i = client.remove("wadh");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i!=0)System.out.println("rem fail");
		try {
			i = client.put("wah", "wahoo".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i==0) {
			System.out.println("wah Success again");
		}
		try {
			String r = new String (client.get("wah"));
			if(r!=null)
				System.out.println(r);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println("doesnt exist");
			e.printStackTrace();
		}
		
		
		/*
		try {
			i = client.remove("wah");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(i==0) {
			System.out.println("Success");
		}
		try {
			String[] keys = client.list();
			System.out.println("read "+keys.length+" keys");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		try {
			
			client.disconnect();
			System.out.println("disconnected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Dc failed");
			e.printStackTrace();
		}
		
	}
	

}
