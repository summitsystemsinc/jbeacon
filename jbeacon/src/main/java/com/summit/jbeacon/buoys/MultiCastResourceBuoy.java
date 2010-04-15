/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.summit.jbeacon.buoys;

import com.summit.jbeacon.beacons.MultiCastResourceBeacon;
import com.summit.jbeacon.util.NetUtilitiesException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.summit.jbeacon.util.MultiCastConstants;
import com.summit.jbeacon.util.NetUtilities;
import com.summit.jbeacon.util.Resource;
import com.summit.jbeacon.util.ResourcePacket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 *  This class is going to listen on a multicast group for a request.
 * Once the given request is received, it will reply to the server sending the
 * request via a TCP socket.
 *
 * @author justin smith
 * @since 1.0
 */
public class MultiCastResourceBuoy {

    /**
     * Command we send to the beacon to ask if it is ready.
     */
    public static final String READY_COMMAND = "READY?";
    /**
     * Command we send to close the connection.
     */
    public static final String CLOSE_COMMAND = "BYE!";
    /**
     * Class Logger.
     */
    private Log log;
    /**
     * The multicast group, as www.xxx.yyy.zzz .
     */
    private String multiCastGroup = MultiCastConstants.DEFAULT_GROUP;
    /**
     * this is the trigger keyword that the thread will listen for.
     */
    private String listenString = MultiCastConstants.DEFAULT_LISTEN_TEXT;
    /**
     * Default thread sleep for the listen loop.<br/>
     * default <b>250</b> ms
     */
    private Long threadSleep = Long.valueOf(
	    MultiCastConstants.DEFAULT_THREAD_WAIT);
    //TODO link this commentto DEFAULT_DATAGRAM_SIZE.
    /**
     * 
     * defaults to 256.
     */
    private Integer dataGramSize = MultiCastConstants.DEFAULT_DATAGRAM_SIZE;
    /**
     * Map of resources available from the application that this beacon is
     * attached to.
     */
    private List<Resource> availableResources;
    //TODO link to DEFAULT_PORT
    /**
     * Listen port for the group.
     */
    private Integer groupPort = MultiCastConstants.DEFAULT_PORT;
    //TODO link to startTHread
    /**
     * This is the beacon thread, started with startReceiver().
     * stopped with stopReceiver().
     */
    private BeaconMultiCastReceiver beaconThread;
    /**
     * read timeout
     */
    private Integer readTimeout = MultiCastConstants.DEFAULT_READ_TIMEOUT;
    private String hostName = null;
    private String ip = null;

    /**
     * Default constructor, gets the logger...
     */
    public MultiCastResourceBuoy() {
	availableResources = new ArrayList<Resource>();
	log = LogFactory.getLog(MultiCastResourceBuoy.class);
    }

    /**
     * @return the availableResources
     */
    public final List<Resource> getAvailableResources() {
	return availableResources;
    }

    /**
     * @param resources the availableResources to set
     */
    public final void setAvailableResources(
	    final List<Resource> resources) {
	this.availableResources = resources;
    }

    /**
     * @return the dataGramSize
     */
    public final Integer getDataGramSize() {
	return dataGramSize;
    }

    /**
     * @param size the size to set
     */
    public final void setDataGramSize(final Integer size) {
	this.dataGramSize = size;
    }

    /**
     * @return the groupPort
     */
    public final Integer getGroupPort() {
	return groupPort;
    }

    /**
     * @param port the groupPort to set
     */
    public final void setGroupPort(final Integer port) {
	this.groupPort = port;
    }

    /**
     * thread init method.  This should be called after all the properties
     * have been set.
     *
     * This should also be the spring init-method.
     * @throws MultiCastResourceBuoyException if the thread fails to start.
     */
    public final void startReceiver() throws MultiCastResourceBuoyException {
	if (hostName == null) {
	    try {
		hostName = InetAddress.getLocalHost().getHostName();
	    } catch (UnknownHostException ex) {
		throw new MultiCastResourceBuoyException("Host name not" + " configured, and unable to determine; aborting.");
	    }
	}
	log.info("Hostname set to: " + hostName);
	//log.info("Multicast Group: " + get)
	beaconThread = new BeaconMultiCastReceiver();
	new Thread(beaconThread).start();
	//wait for thread to start running, or error out
	while (!beaconThread.isThreadRunning()
		&& !beaconThread.isThreadFailed()) {
	    try {
		Thread.sleep(getThreadSleep());
	    } catch (InterruptedException ex) {
		log.warn("Thread sleep interupted " + "while waiting on beacon start.");
	    }
	    if (beaconThread.isThreadFailed()) {
		throw new MultiCastResourceBuoyException(
			beaconThread.getThrownException().getMessage(),
			beaconThread.getThrownException());
	    }
	}
    }

    /**
     * thread stop method... This signals the beacon thread to stop, and blocks
     * until it stops...
     *
     * This should also be the spring-destroy method;
     */
    public final void stopReceiver() throws MultiCastResourceBuoyException {
	beaconThread.notifyThreadStop();
	while (beaconThread.isThreadRunning()) {
	    try {
		Thread.sleep(getThreadSleep());
	    } catch (InterruptedException ex) {
		log.warn("Unnexpected thread death " + "while waiting for receiver to stop...", ex);

	    }
	}
	if (beaconThread.isThreadFailed()) {
	    throw new MultiCastResourceBuoyException(
		    beaconThread.getThrownException().getMessage(),
		    beaconThread.getThrownException());
	}
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
     * @return the listenString
     */
    public String getListenString() {
	return listenString;
    }

    /**
     * @param listenString the listenString to set
     */
    public void setListenString(String listenString) {
	this.listenString = listenString;
    }

    /**
     * @return the threadSleep
     */
    public Long getThreadSleep() {
	return threadSleep;
    }

    /**
     * @param threadSleep the threadSleep to set
     */
    public void setThreadSleep(Long threadSleep) {
	this.threadSleep = threadSleep;
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
    private MulticastSocket s = null;

    /**
     * @return the ip
     */
    public String getIp() {
	return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
	this.ip = ip;
    }

    /**
     * This is the thread that will listen for the beacon request.
     */
    private class BeaconMultiCastReceiver implements Runnable {

	/**
	 * Is the thread running?
	 */
	private boolean running = false;
	/**
	 * should the thread be running?
	 */
	private boolean shouldBeRunning = false;
	/**
	 * Lets us knwo if the thread failed.
	 */
	private boolean threadFailed = false;
	private MultiCastResourceBuoyException thrownException = null;

	@Override
	public void run() {
	    try {
		shouldBeRunning = true;
		try {
		    log.info("Multicast port: " + getGroupPort());
		    s = new MulticastSocket(getGroupPort());
		} catch (IOException ex) {
		    throw new MultiCastResourceBuoyException(
			    "IOException was thrown while " + "creating multicast socket. ", ex);
		}
		InetAddress group = null;
		try {
		    log.info("Multicast group: " + multiCastGroup);
		    group = InetAddress.getByName(multiCastGroup);
		    log.debug("Group Details: ADDRESS=" + group.getHostAddress());
		} catch (UnknownHostException ex) {
		    throw new MultiCastResourceBuoyException(
			    "UnkownHostException when creating InetAddress...",
			    ex);
		}
		try {
		    s.joinGroup(group);
		} catch (IOException ex) {
		    throw new MultiCastResourceBuoyException(
			    "IOException thrown when joining group.",
			    ex);
		}
		
		try {
		    s.setSoTimeout(getReadTimeout());
		} catch (SocketException ex) {
		    throw new MultiCastResourceBuoyException(
			    "Error setting the read timeout.", ex);
		}
		running = true;
		while (shouldBeRunning) {
		    byte[] buffer = new byte[getDataGramSize()];
		    DatagramPacket pack =
			    new DatagramPacket(buffer, buffer.length);
		    try {
			s.receive(pack);
			String message = new String(pack.getData()).trim();
			log.info("Received Message \"" + message + "\"");

			//Break up the String...
			String[] parts = message.split(" : ");
			//if we hear what we are listening for, do stuff
			if (parts[0].equals(getListenString())) {
			    final String[] ipParts = parts[1].split(":");
			    if (ipParts.length < 3) {
				String msg = "Invalid message: " + message;
				log.error(msg);
				throw new MultiCastResourceBuoyException(msg);
			    }
			    log.info("Request to send information to: " + ipParts[0] + ":" + ipParts[1] + ":" + ipParts[2]);
			    final String ip = ipParts[0];
			    String hostname = ipParts[1];
			    final int port;
			    try {
				port = Integer.valueOf(ipParts[2]);
			    } catch (NumberFormatException ex) {
				log.error("Invalid message: " + message);
				throw new MultiCastResourceBuoyException(ex.getMessage(), ex);
			    }
			    if (ip.equals("") && hostname.equals("")) {
				String msg = "Invalid message, hostname and ip cannot be empty.";
				log.error(msg);
				throw new MultiCastResourceBuoyException(msg);
			    }

			    //Small inline thread for communication...
			    //This is not really testable...
			    //TODO refactor this to make testable.
			    //This could be done on the
			    //server side in the beacon
			    new Thread(new Runnable() {

				@Override
				public void run() {
				    Socket s = null;
				    try {
					if (ip.equals("")) {
					    log.warn("IP not set, using hostname...");
					    s = new Socket(InetAddress.getByName(
						    hostName),
						    port);
					} else {
                                            try{
					    s = new Socket(InetAddress.getByName(
						    ip),
						    port);
                                            }catch(IOException ex){
                                                log.error("Error connectiong to: " + ip, ex);
                                                return;
                                            }
					}
					s.setSoTimeout(getReadTimeout());
					InputStream inStream =
						s.getInputStream();
					OutputStream outStream =
						s.getOutputStream();

					PrintWriter outWriter =
						new PrintWriter(outStream);
					BufferedReader inReader =
						new BufferedReader(
						new InputStreamReader(
						inStream));
					ObjectOutputStream objectWriter =
						new ObjectOutputStream(
						outStream);
					outWriter.println(READY_COMMAND);
					outWriter.flush();

					String response = "";
					try {
					    response = inReader.readLine();
					} catch (SocketTimeoutException ex) {
					    log.warn("Socket Timeout " + "while reading, assume " + "connection dead.", ex);
					    s.close();
					    return;
					}
					if (response.equals(
						MultiCastResourceBeacon.ACK_COMMAND)) {
					} else {
					    log.warn("Invalid response \"" + response + "\"");
					    s.close();
					    return;
					}
					try {
					    objectWriter.writeObject(generateResourcePacket());
					} catch (MultiCastResourceBuoyException ex) {
					    log.error(ex.getMessage(), ex);
					}
					objectWriter.flush();
					if (response.equals(
						MultiCastResourceBeacon.ACK_COMMAND)) {
					} else {
					    log.warn("Invalid response \"" + response + "\"");
					    s.close();
					    return;
					}

					outWriter.println(CLOSE_COMMAND);
					outWriter.flush();
					try {
					    response = inReader.readLine();
					} catch (SocketTimeoutException ex) {
					    log.warn("Socket Timeout " + "while reading, assume " + "connection dead.", ex);
					    s.close();
					    return;
					}
					if (response.equals(
						MultiCastResourceBeacon.ACK_COMMAND)) {
					    log.info("Closing Connection.");
					    s.close();
					} else {
					    log.warn("Invalid response \"" + response + "\"");
					    s.close();
					    return;
					}
				    } catch (UnknownHostException ex) {
					log.error(ex.getMessage(), ex);
					return;
				    } catch (IOException ex) {

					log.error(ex.getMessage(), ex);
					return;
				    }
				}
			    }).start();
			} else {
			    String errorMessage = "Recieved invalid request: \"" + parts[0] + "\"";
			    log.warn(errorMessage);
			    throw new MultiCastResourceBuoyException(errorMessage);
			}
		    } catch (SocketTimeoutException ex) {
			log.debug("SocketTimeoutReached... Restarting.");
			continue;
		    } catch (IOException ex) {
			log.debug(ex.getMessage(), ex);
		    } catch (MultiCastResourceBuoyException ex) {
			log.error(ex.getMessage(), ex);
			continue;
		    }
		    try {
			Thread.sleep(getThreadSleep());
		    } catch (InterruptedException ex) {
			log.warn("Unexpected " + "thread death during sleep period.", ex);
		    }
		}
		try {
		    s.leaveGroup(group);
		} catch (IOException ex) {
		    throw new MultiCastResourceBuoyException(
			    "IOException thrown when leaving group.",
			    ex);
		}
		s.close();
	    } catch (MultiCastResourceBuoyException ex) {
		log.error(ex.getMessage(), ex);
		threadFailed = true;
		thrownException = ex;
	    } finally {
		running = false;
	    }
	}

	/**
	 * Notifies the thread to stop listening at the next sleep interval.
	 */
	public void notifyThreadStop() {
	    shouldBeRunning = false;
	}

	/**
	 *
	 * @return if the thread is currently running.
	 */
	public boolean isThreadRunning() {
	    return running;
	}

	/**
	 * @return the threadFailed
	 */
	public boolean isThreadFailed() {
	    return threadFailed;
	}

	/**
	 * @param threadFailed the threadFailed to set
	 */
	public void setThreadFailed(boolean threadFailed) {
	    this.threadFailed = threadFailed;
	}

	/**
	 * @return the thrownException
	 */
	public MultiCastResourceBuoyException getThrownException() {
	    return thrownException;
	}
    }

    private InetAddress guessHostAddress() throws MultiCastResourceBuoyException {

	if (hostName != null) {
	    try {
		return InetAddress.getByName(hostName);
	    } catch (UnknownHostException ex) {
		throw new MultiCastResourceBuoyException(ex.getMessage(), ex);
	    }
	} else {
	    if (s == null) {
		throw new MultiCastResourceBuoyException("Multicast socket "
			+ "not yet initialized, and hostname not set.");
	    }
	    InetAddress retVal = s.getInetAddress();
	    if (retVal != null) {
		return s.getInetAddress();
	    } else {
		log.warn("Failed to guess from the multicast socket... ");
		log.warn("Attempting to guess from FIRST network card with IP");
		log.warn("and the FIRST ip address assigned to the nic.");
		InetAddress guess = null;
		try {
		    guess = NetUtilities.guessLocalInetAddress();

		} catch (NetUtilitiesException ex) {
		    throw new MultiCastResourceBuoyException(ex.getMessage(), ex);
		}
		if (guess == null) {
		    throw new MultiCastResourceBuoyException("Unable to "
			    + "guess local address...");
		}
		return guess;
	    }
	}

    }

    private ResourcePacket generateResourcePacket() throws MultiCastResourceBuoyException {
	ResourcePacket retVal = new ResourcePacket();
	InetAddress guessedAddress = guessHostAddress();
	retVal.setDefaultHostName(guessedAddress.getHostName());
	retVal.setDefaultIp(guessedAddress.getHostAddress());
	for (int i = 0; i < availableResources.size(); i++) {
	    Resource r = availableResources.get(i);
	    retVal.getResources().add(r);
	}
	return retVal;
    }
}
