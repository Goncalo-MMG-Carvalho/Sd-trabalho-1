package tukano.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URI;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * <p>A class interface to perform service discovery based on periodic 
 * announcements over multicast communication.</p>
 * 
 */

public interface Discovery {

	/**
	 * Used to announce the URI of the given service name.
	 * @param serviceName - the name of the service
	 * @param serviceURI - the uri of the service
	 */
	public void announce(String serviceName, String serviceURI);

	/**
	 * Get discovered URIs for a given service name
	 * @param serviceName - name of the service
	 * @param minReplies - minimum number of requested URIs. Blocks until the number is satisfied.
	 * @return array with the discovered URIs for the given service name.
	 */
	public URI[] knownUrisOf(String serviceName, int minReplies);

	/**
	 * Get the instance of the Discovery service
	 * @return the singleton instance of the Discovery service
	 */
	public static Discovery getInstance() {
		return DiscoveryImpl.getInstance();
	}
}

/**
 * Implementation of the multicast discovery service
 */
class DiscoveryImpl implements Discovery {
	
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	// The pre-aggreed multicast endpoint assigned to perform discovery.

	static final int DISCOVERY_RETRY_TIMEOUT = 5000;
	static final int DISCOVERY_ANNOUNCE_PERIOD = 1000;

	// Replace with appropriate values: allowed IP Multicast range: 224.0.0.1 - 239.255.255.255
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("225.0.0.1", 1001); //-1 is to simbolize that we dont need to specify a port
	//PORTS < 1000 normalmente protegidos por sistema

	// Used to separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	private static final int MAX_DATAGRAM_SIZE = 65536;

	//data structure to store the URIs of the services 
	private Map<String, Set<URI>> servicesAvailable  = new ConcurrentHashMap<>();;


	private static Discovery singleton;

	synchronized static Discovery getInstance() {
		if (singleton == null) {
			singleton = new DiscoveryImpl();
		}
		return singleton;
	}

	private DiscoveryImpl() {
		this.startListener();
	}

	@Override
	public void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s\n", DISCOVERY_ADDR, serviceName,
				serviceURI));

		var pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();
		var pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);

		// start thread to send periodic announcements
		new Thread(() -> {
			try (var ds = new DatagramSocket()) {
				while (true) {
					try {
						ds.send(pkt);
						Thread.sleep(DISCOVERY_ANNOUNCE_PERIOD);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}


	@Override
	public URI[] knownUrisOf(String serviceName, int minEntries) {
		while(true) {
			Set<URI> serviceUris = servicesAvailable.get(serviceName);
			int size = serviceUris.size();
			if (serviceUris == null ||  size < minEntries)
			{
				try {
					Thread.sleep(DISCOVERY_RETRY_TIMEOUT);
				} catch (Exception e) { //this exception will handle the cases in which the this thread is interrupted
					//i dont know what to put here
				}
			}
			else
			{
				return serviceUris.toArray(new URI[size]);
			}
		}
	}

	private void startListener() {
		Log.info(String.format("Starting discovery on multicast group: %s, port: %d\n", DISCOVERY_ADDR.getAddress(),
				DISCOVERY_ADDR.getPort()));

		new Thread(() -> {
			try (var ms = new MulticastSocket(DISCOVERY_ADDR.getPort())) {
				ms.joinGroup(DISCOVERY_ADDR, NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
				for (;;) {
					try {
						var pkt = new DatagramPacket(new byte[MAX_DATAGRAM_SIZE], MAX_DATAGRAM_SIZE);
						ms.receive(pkt);

						var msg = new String(pkt.getData(), 0, pkt.getLength());
						Log.info(String.format("Received: %s", msg));

						var parts = msg.split(DELIMITER);
						if (parts.length == 2) {
							var serviceName = parts[0];
							var uri = URI.create(parts[1]);

							// TO DO: complete by storing the decoded announcements...

							// 1 - get the services array that offer that service
							Set<URI> services = servicesAvailable.get(serviceName);

							// 2 - if there is no service of that type just create another URI data structure that will contain servers that offer that type of service
							if (services == null){
								Set<URI> newServicesUris = new HashSet<>(); 			//2.1 create a new data structure to store all the URIs that offer that new service
								newServicesUris.add(uri);								//2.2 add the new uri of the new service available
								servicesAvailable.put(serviceName, newServicesUris);	//2.3 add that new collection of URIs to the services data structure
								//servicesAvailable.put(serviceName,{uri});
							}
							else //3 - store that new URI into the corresponding service type data structure that it offers
							{
								//3.1 only put the new URI if its not already there
								if (!services.contains(uri))
									services.add(uri);
							}
						}

					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}).start();
	}
}