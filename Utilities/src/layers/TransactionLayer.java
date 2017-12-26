package layers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import mensajesSIP.SIPMessage;


public abstract class TransactionLayer {
	
	public UserLayer ul;
	public TransportLayer transportLayer;
	public InetAddress requestAddress;
	public int requestPort;

	
	public void setUl(UserLayer ul) {
		this.ul = ul;
	}

	public void setTransportLayer(TransportLayer transportLayer) {
		this.transportLayer = transportLayer;
	}

	public abstract void recvFromTransport(SIPMessage message);
	
	public void sendToUser(SIPMessage message) {
		ul.recvFromTransaction(message);
	}
	
	public abstract void recvRequestFromUser(SIPMessage request, InetAddress requestAddress, int requestPort);
	
	public abstract void recvResponseFromUser(SIPMessage response);
	
	public abstract void sendACK(SIPMessage error);
	
	public abstract void sendError(SIPMessage error);
	
	public abstract void cancelTimer();
	
	
	public void sendRequest(SIPMessage message){
		try {
			transportLayer.sendToNetwork(requestAddress, requestPort, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendResponse(SIPMessage message){
		String s[] = message.getVias().get(0).split(":");
		try {
			transportLayer.sendToNetwork(InetAddress.getByName(s[0]), Integer.valueOf(s[1]), message);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
