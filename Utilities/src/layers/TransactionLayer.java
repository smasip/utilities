package layers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fsm.ClientFSM;
import fsm.ServerFSM;
import mensajesSIP.SIPMessage;


public abstract class TransactionLayer {
	
	protected ClientFSM client;
	protected ServerFSM server;
	protected UserLayer ul;
	protected TransportLayer transportLayer;
	protected InetAddress requestAddress;
	protected int requestPort;
	protected Transaction currentTransaction;
	protected String callId;
	protected ArrayList<String> myVias;
	protected String destination;
	
	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	public ArrayList<String> getMyVias() {
		return myVias;
	}

	public void setMyVias(ArrayList<String> myVias) {
		this.myVias = myVias;
	}

	public void setUl(UserLayer ul) {
		this.ul = ul;
	}

	public void setTransportLayer(TransportLayer transportLayer) {
		this.transportLayer = transportLayer;
	}
	
	public void setRequestAddress(InetAddress requestAddress) {
		this.requestAddress = requestAddress;
	}

	public void setRequestPort(int requestPort) {
		this.requestPort = requestPort;
	}

	public void sendToUser(SIPMessage message) {
		ul.recvFromTransaction(message);
	}

	public void recvResponseFromUser(SIPMessage response) {
			
		switch (currentTransaction) {
		
			case INVITE_TRANSACTION:
				server.processMessage(response);
				break;
				
			case NO_TRANSACTION:
				sendResponse(response);
				break;
				
			default:
				break;
		
		}
		
	}
	
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
	
	public abstract void resetLayer();
	
	public abstract void recvFromTransport(SIPMessage message);
	
	public abstract void recvRequestFromUser(SIPMessage request);

}
