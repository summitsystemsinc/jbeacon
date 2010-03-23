/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summit.jbeacon.beacons;

import com.summit.jbeacon.buoys.MultiCastResourceBuoy;
import com.summit.jbeacon.util.MultiCastConstants;
import com.summit.jbeacon.util.ResourcePacket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author justin smith
 */
public class MultiCastResourceBeacon {

    public static final String ACK_COMMAND = "GO";
    /**
     * class logger.
     */
    private Log log;

    /**
     * Default constructor, sets up the logger.
     */
    public MultiCastResourceBeacon() {
	availableResources = new HashSet<ResourcePacket>();
	log = LogFactory.getLog(MultiCastResourceBeacon.class);
    }
    /**
     * Available resources, updated on refresh.
     */
    private Set<ResourcePacket> availableResources;
    /**
     * Group port.
     */
    private int groupPort = MultiCastConstants.DEFAULT_PORT;
    /**
     * group address, must be valid multicast address...
     */
    private String groupAddress = MultiCastConstants.DEFAULT_GROUP;
    /**
     * the text to broadcast for listening.
     */
    private String broadcastText = MultiCastConstants.DEFAULT_LISTEN_TEXT;
    /**
     * Socket listening for buoy responses.
     */
    private ServerSocket listeningSocket = null;
    /**
     * Listen port for incoming connections.
     */
    private int listenPort = MultiCastConstants.DEFAULT_LISTENER_PORT;
    private Integer readTimeout = MultiCastConstants.DEFAULT_READ_TIMEOUT;
    private Integer threadSleep = MultiCastConstants.DEFAULT_THREAD_WAIT;
    /**
     * Listener thread.
     */
    private MultiCastResourceListener listenerThread = null;
    private String hostName = null;

    /**
     * refresh the resources that are available.
     * @throws MultiCastResourceBeaconException if
     * the data fails to be refreshed
     */
    public final void refreshData() throws MultiCastResourceBeaconException {
	while (!listenerThread.isRunning()) {
	    try {
		log.debug("Waiting for server to start.");
		Thread.sleep(threadSleep);
	    } catch (InterruptedException ex) {
		log.warn("Thread death while waiting for listener to run.");
	    }

	}
	log.info("Attempting to refresh data.");
	if (listeningSocket == null) {
	    throw new MultiCastResourceBeaconException("Beacon is not " + "listening for responses. Initialize the " + "listener first!");
	}
	MulticastSocket s = null;
	try {
	    s = new MulticastSocket();
	} catch (IOException ex) {
	    throw new MultiCastResourceBeaconException(
		    "Error creating multicast socket...", ex);
	}

	String broadcastMessage = getBroadcastText() + " : " + this.hostName + ":" + listeningSocket.getLocalPort();
	byte[] buf = broadcastMessage.getBytes();
	DatagramPacket pack = null;
	try {
	    log.info("Multicast port: " + getGroupPort());
	    log.info("Multicast group: " + getGroupAddress());
	    pack = new DatagramPacket(buf, buf.length,
		    InetAddress.getByName(getGroupAddress()), getGroupPort());
	} catch (UnknownHostException ex) {
	    throw new MultiCastResourceBeaconException(
		    "Error creating datagram.", ex);
	}
	try {
	    s.send(pack);
	} catch (IOException ex) {
	    throw new MultiCastResourceBeaconException(
		    "Error sending message.", ex);
	}
	s.close();
    }

    public void startListening() throws MultiCastResourceBeaconException {
	if (hostName == null) {
	    try {
		hostName = InetAddress.getLocalHost().getHostName();
	    } catch (UnknownHostException ex) {
		throw new MultiCastResourceBeaconException("Host name not" + " configured, and unable to determine; aborting.");
	    }
	}
	try {
	    listeningSocket = new ServerSocket(getListenPort(),
		    0,
		    InetAddress.getByName(hostName));
	    listenerThread = new MultiCastResourceListener(listeningSocket);
	    new Thread(listenerThread).start();
	} catch (IOException ex) {
	    throw new MultiCastResourceBeaconException(
		    ex.getMessage(), ex);
	}
    }

    public void stopListening() throws MultiCastResourceBeaconException {

	listenerThread.setShouldBeRunning(false);
	while (listenerThread.isRunning()) {
	    try {
		Thread.sleep(threadSleep);
	    } catch (InterruptedException ex) {
		log.warn("Thread was interrupted while trying to close");
	    }
	}
	if (listenerThread.isThreadFailed()) {
	    throw new MultiCastResourceBeaconException(
		    listenerThread.getExceptionThrown().getMessage(),
		    listenerThread.getExceptionThrown());
	}
    }

    /**
     * @return the groupPort
     */
    public int getGroupPort() {
	return groupPort;
    }

    /**
     * @param groupPort the groupPort to set
     */
    public void setGroupPort(int groupPort) {
	this.groupPort = groupPort;
    }

    /**
     * @return the groupAddress
     */
    public String getGroupAddress() {
	return groupAddress;
    }

    /**
     * @param groupAddress the groupAddress to set
     */
    public void setGroupAddress(String groupAddress) {
	this.groupAddress = groupAddress;
    }

    /**
     * @return the broadcastText
     */
    public String getBroadcastText() {
	return broadcastText;
    }

    /**
     * @param broadcastText the broadcastText to set
     */
    public void setBroadcastText(String broadcastText) {
	this.broadcastText = broadcastText;
    }

    /**
     * @return the listenPort
     */
    public int getListenPort() {
	return listenPort;
    }

    /**
     * @param listenPort the listenPort to set
     */
    public void setListenPort(int listenPort) {
	this.listenPort = listenPort;
    }

    /**
     * @return the readTimeout
     */
    public Integer getReadTimeout() {
	return readTimeout;
    }

    /**
     * @param readTimeout the readTimeout to set
     */
    public void setReadTimeout(Integer readTimeout) {
	this.readTimeout = readTimeout;
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
	return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
	this.hostName = hostName;
    }

    /**
     * @return the availableResources
     */
    public Set<ResourcePacket> getAvailableResources() {
	return availableResources;
    }

    /**
     * @param availableResources the availableResources to set
     */
    public void setAvailableResources(Set<ResourcePacket> availableResources) {
	this.availableResources = availableResources;
    }

    private class MultiCastResourceListener implements Runnable {

	private ServerSocket listeningSocket;
	private boolean running = false;
	private boolean shouldBeRunning = false;
	private boolean threadFailed = false;
	private MultiCastResourceBeaconException exceptionThrown = null;

	public MultiCastResourceListener(ServerSocket s) {
	    listeningSocket = s;
	}

	@Override
	public void run() {
	    shouldBeRunning = true;
	    try {
		try {
		    log.info("Listening for connections on: " + listeningSocket.getInetAddress() + ":" + listenPort);
		    listeningSocket.setSoTimeout(getReadTimeout());
		} catch (SocketException ex) {
		    throw new MultiCastResourceBeaconException(
			    ex.getMessage(), ex);
		}
		log.info("Listening thread running.");
		running = true;
		while (isShouldBeRunning()) {
		    try {
			Socket s = listeningSocket.accept();
			new Thread(new MultiCastResourceConnection(s)).start();
		    } catch (SocketTimeoutException ex) {
			log.debug("Timeout waiting for connection, retrying.");
			continue;
		    } catch (IOException ex) {
			throw new MultiCastResourceBeaconException(
				"Error making connection.", ex);
		    }
		    try {
			Thread.sleep(readTimeout);
		    } catch (InterruptedException ex) {
			log.warn("Thread death while waiting to connect.");
		    }
		}
		try {
		    listeningSocket.close();
		} catch (IOException ex) {
		    throw new MultiCastResourceBeaconException("Error "
			    + "closing the server socket.", ex);
		}
	    } catch (MultiCastResourceBeaconException ex) {
		threadFailed = true;
		exceptionThrown = ex;
	    } finally {
		running = false;
	    }

	}

	public boolean isRunning() {
	    return running;
	}

	/**
	 * @return the threadFailed
	 */
	public boolean isThreadFailed() {
	    return threadFailed;
	}

	/**
	 * @return the exceptionThrown
	 */
	public MultiCastResourceBeaconException getExceptionThrown() {
	    return exceptionThrown;
	}

	/**
	 * @return the shouldBeRunning
	 */
	public boolean isShouldBeRunning() {
	    return shouldBeRunning;
	}

	/**
	 * @param shouldBeRunning the shouldBeRunning to set
	 */
	public void setShouldBeRunning(boolean shouldBeRunning) {
	    this.shouldBeRunning = shouldBeRunning;
	}
    }

    private class MultiCastResourceConnection implements Runnable {

	private Socket connection;

	public MultiCastResourceConnection(Socket s) {
	    connection = s;
	}

	@Override
	public void run() {
	    try {
		connection.setSoTimeout(getReadTimeout());
		InputStream inStream =
			connection.getInputStream();
		OutputStream outStream =
			connection.getOutputStream();

		PrintWriter outWriter =
			new PrintWriter(outStream);
		BufferedReader inReader =
			new BufferedReader(
			new InputStreamReader(
			inStream));
		ObjectInputStream objectReader =
			new ObjectInputStream(inStream);
		String response = "";
		try {
		    response = inReader.readLine();
		    log.debug("Received: " + response);
		} catch (SocketTimeoutException ex) {
		    log.warn("Timeout waiting for response, aborting.");
		    connection.close();
		    return;
		}
		if (response.equals(MultiCastResourceBuoy.READY_COMMAND)) {
		    log.debug("Reaceived ready command, acknowledging.");
		    outWriter.println(ACK_COMMAND);
		    outWriter.flush();
		} else {
		    log.warn("Invalid response: " + response);
		    connection.close();
		    return;
		}
		try {
		    //TODO Recieve objects...
		    ResourcePacket packet = (ResourcePacket) objectReader.readObject();
		    if (getAvailableResources().contains(packet)) {
			getAvailableResources().remove(packet);
		    }
		    log.info("Received resources from: "
			    + packet.getDefaultHostName());
		    getAvailableResources().add(packet);
		    outWriter.println(ACK_COMMAND);
		    outWriter.flush();
		} catch (ClassNotFoundException ex) {
		    log.error("Invalid response, not a valid "
			    + "resourcePacket: " + ex.getMessage(), ex);
		    connection.close();
		}
		try {
		    response = inReader.readLine();
		    log.debug("Received: " + response);
		} catch (SocketTimeoutException ex) {
		    log.warn("Timeout waiting for close response, aborting.");
		    connection.close();
		    return;
		}
		if (response.equals(MultiCastResourceBuoy.CLOSE_COMMAND)) {
		    outWriter.println(ACK_COMMAND);
		    outWriter.flush();
		    connection.close();
		} else {
		    log.warn("Invalid response: " + response);
		    connection.close();
		    return;
		}
	    } catch (IOException ex) {
		log.error(ex.getMessage(), ex);
	    }
	}
    }
}
