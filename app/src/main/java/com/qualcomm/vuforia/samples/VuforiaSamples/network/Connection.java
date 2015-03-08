package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Connection class:
 * 		Class used to continue communication to another client.
 */
public class Connection extends Thread {
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Client client;
	private boolean isRunning = true;
	
	Connection(Socket connection, Client client) {
		this.client = client;
		this.socket = connection;
	}
	
	Connection(InetAddress inet, Client client) {
		this.client = client;
		try {
			socket = new Socket(inet, Config.ANDROIDPORT);
		
		} catch (IOException e) {
			Log.e("Connection", "Constructor", e);
		}
	}
	
	@Override
	public void run() {
		try {
			// Fetches InputStream from connection
			InputStream serverInputStream = this.socket.getInputStream();
			// Fetches OutputStream from connect
			OutputStream serverOutputStream = this.socket.getOutputStream();
			// Create ObjectOutputStream
			this.oos = new ObjectOutputStream(serverOutputStream);
			// Create InputObjectStream
			this.ois = new ObjectInputStream(serverInputStream);
			Log.d("Connection", "Connection to " + socket.getLocalAddress().toString() + " is ready.");
			// While-loop to ensure continuation of reading in-coming messages
			while (this.socket.isConnected() && isRunning) {
				try {
					//Receive object from client
					Object obj = this.ois.readObject();
					this.client.receive(obj);
				} catch (ClassNotFoundException e) {
					Log.e("Connection", "run", e);
				}
			}

			// Close buffers and socket
			this.ois.close();
			this.oos.close();
			serverOutputStream.close();
			serverInputStream.close();
			this.socket.close();

		} catch (IOException e1) {
			Log.e("Connection", "IOException", e1);
			this.client.removeConnection(this);
		}
	}
	
	/**
	 * Used to send given object parameter to the connection established.
	 * @param obj Object to be sent through socket connection.
	 */
	protected void send(Object obj) {
		try {
			this.oos.writeObject(obj);
			this.oos.flush();
		} catch (IOException e) {
			Log.e("Connection", "send", e);
		}
	}
	
	/**
	 * Get IP of socket established.
	 * @return
	 */
	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}

	/**
	 * Close established connection.
	 */
	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			Log.e("Connection", "closeConnection", e);
		}
		
	}
}
