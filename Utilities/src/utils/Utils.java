package utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Utils {
	
	
	public static InetAddress getMyAddress() throws SocketException {
		
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		List<InterfaceAddress> interfaceAddresses;
		List<InetAddress> addressesList = new ArrayList<InetAddress>();
		InetAddress address;
		
		while(networkInterfaces.hasMoreElements()) {
			interfaceAddresses = networkInterfaces.nextElement().getInterfaceAddresses();
			for (InterfaceAddress itfAddress : interfaceAddresses) {
				address = itfAddress.getAddress();
				if((address instanceof Inet4Address) && (!address.isLoopbackAddress())) {
					addressesList.add(address);
				}
			}
		}
		
		return addressesList.get(0);
		
	}
	
}
