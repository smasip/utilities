package layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import fsm.ClientStateUA;
import fsm.ServerStateUA;
import mensajesSIP.InviteMessage;
import mensajesSIP.SIPMessage;

public class TransactionLayerUA extends TransactionLayer{
	
	InetAddress addressProxy;
	int portProxy;
	ClientStateUA client;
	ServerStateUA server;
	String currentCallID;
	Transaction currentTransaction;
	
	public TransactionLayerUA() {
		super();
		this.client = ClientStateUA.TERMINATED;
		this.server = ServerStateUA.TERMINATED;
	}
	
	
	public String getCurrentCallID() {
		return currentCallID;
	}

	public void setCurrentCallID(String currentCallID) {
		this.currentCallID = currentCallID;
	}
	
	
	public InetAddress getAddressProxy() {
		return addressProxy;
	}


	public void setAddressProxy(InetAddress addressProxy) {
		this.addressProxy = addressProxy;
	}


	public int getPortProxy() {
		return portProxy;
	}


	public void setPortProxy(int portProxy) {
		this.portProxy = portProxy;
	}


	@Override
	public void recvFromTransport(SIPMessage message) {
		
		switch (currentTransaction) {
		case REGISTER_TRANSACTION:
			
			break;
			
		case INVITE_TRANSACTION:
			break;
			
		case NO_TRANSACTION:
			
			break;
		default:
			break;
		}
		
		if(client == ClientStateUA.TERMINATED && 
		   server == ServerStateUA.TERMINATED) 
		{
			if (message instanceof InviteMessage) {
				server = ServerStateUA.PROCEEDING;
				server.processMessage(message, this);
				//currentCallID=
			}
		}
		else if(server == ServerStateUA.TERMINATED) 
		{
			if (!(message instanceof InviteMessage)) {
				client.processMessage(message, this);
			}
		}
	}

	public void recvFromUser(SIPMessage message) {
		if(client == ClientStateUA.TERMINATED && 
		   server == ServerStateUA.TERMINATED) 
		{
			if (message instanceof InviteMessage) {
				client = ClientStateUA.CALLING;
				client.processMessage(message, this);
				//currentCallID =
			}
		}
		else if(client == ClientStateUA.TERMINATED) 
		{
			if (!(message instanceof InviteMessage)) {
				server.processMessage(message, this);
			}
		}
	}
	
	public void sendToTransport(SIPMessage message) throws IOException {
		transportLayer.sendToNetwork(addressProxy, portProxy, message);
	}
	
	
	public void resetLayer() {
		client = ClientStateUA.TERMINATED;
		server  = ServerStateUA.TERMINATED;
		currentCallID = null;
	}


}
