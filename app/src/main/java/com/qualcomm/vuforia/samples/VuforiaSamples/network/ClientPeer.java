package com.qualcomm.vuforia.samples.VuforiaSamples.network;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Establishes connection to another clients by listening on
 * an ServerScokcet.
 *
 */
public class ClientPeer extends Thread {
	
	private Client client;
	private ServerSocket peerSocket;

	public ClientPeer(ServerSocket peerSocket, Client client) {
		this.client = client;
		this.peerSocket = peerSocket;
	}
	
	@Override
	public void run() {
		try {
			// Listen to ServerSocket and accept in-coming connections.
			Log.d("ClientPeer", "Listening on " + peerSocket.getLocalSocketAddress() + ":" + peerSocket.getLocalPort());
			Socket clientPeer;
			while (true) {
				clientPeer = peerSocket.accept();
				Connection clientConnection = new Connection(clientPeer, this.client);
				clientConnection.start();
				this.client.addConnection(clientConnection);
				Log.d("ClientPeer", "Connection received from " + peerSocket.getInetAddress().toString() + ".");
			}
		} catch (IOException e) {
			Log.e("ClientPeer", "run", e);
		}
	}

	/**
	 * Get InetAddress of ServerSocket.
	 * @return
	 */
	public InetAddress getInetAddress() {
		return this.peerSocket.getInetAddress();
	}
	
	/**
	 * Get port number of ServerSocket.
	 * @return
	 */
	public int getPort() {
		return this.peerSocket.getLocalPort();
	}

}
