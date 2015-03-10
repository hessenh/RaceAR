package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Client extends Thread {

	/** Peer-to-peer client **/
	private static Client client;
	private ArrayList<Connection> clients;
	private ClientPeer peerConnection;
	private PacketHandler packetHandler;
	private String ip;

	public Client(PacketHandler packetHandler, String ip) {
		this.packetHandler = packetHandler;
		this.ip = ip;
		clients = new ArrayList<Connection>();
		client = this;
	}


	public static synchronized Client getInstance(PacketHandler packetHandler, String ip){
		if(client==null){
			client = new Client(packetHandler, ip);
			return client;
		} else {
			client.setPacketHandler(packetHandler);
		}
		return client;
	}

	public void run() {
		System.out.println("Trying to connect to server!");
		try {
			ServerSocket peerSocket = new ServerSocket(Config.ANDROIDPORT, 50, InetAddress.getByName(ip));
			this.peerConnection = new ClientPeer(peerSocket, this);
			this.peerConnection.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfConnections(){
		return this.clients.size();
	}
	
	/**
	 * Sends object through a connection
	 * @param obj Object to be sent through a connection
	 */
	protected void send(Object obj, Connection connection) {
		connection.send(obj);
	}
	
	/**
	 * Sends object to all other clients.
	 * Should be used during game.
	 * @param obj Object to be sent.
	 */
	public void sendAll(Object obj) {
		//Log.d("Client", "Sending object to all clients");
		for(Connection connection : this.clients) {
			connection.send(obj);
		}
	}
	
	/**
	 * Objects which is received are handled here.
	 * @param obj
	 */
	protected void receive(Object obj) {
		if(obj instanceof CarPacket) {
			if(packetHandler != null)
				packetHandler.carPacketHandler((CarPacket) obj);
		} else if(obj instanceof TrackPacket) {
			if(packetHandler != null)
				packetHandler.trackPacketHandler((TrackPacket) obj);
		}

	}
	
	/**
	 * Adds connection to connection list.
	 * @param client Connection to client.
	 */
	protected void addConnection(Connection client) {
		Log.d("Client", "Connection to a client added");
		this.clients.add(client);
		if(packetHandler != null)
			packetHandler.newConnectionHandler(client);
	}

	protected void removeConnection(Connection client) {
		Log.d("Client", "Connection to a client removed");
		this.clients.remove(client);
	}

	protected void closeAllConnections() {
		for(Connection connection : this.clients) {
			connection.closeConnection();
		}
	}

	public void connect(String ip) {
		try {
			Connection connection = new Connection(InetAddress.getByName(ip), this);
			connection.start();
			addConnection(connection);
		} catch(IOException e) {
			Log.e("Client", "connect", e);
		}
	}

	public void disconnect() {
		for(Connection connection : clients)
			connection.closeConnection();
	}

	public void setPacketHandler(PacketHandler handler) {
		packetHandler = handler;
	}

}
