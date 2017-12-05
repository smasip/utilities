package layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fsm.ClientStateProxy;
import fsm.ClientStateUA;
import fsm.ServerStateProxy;
import fsm.ServerStateUA;
import mensajesSIP.InviteMessage;
import mensajesSIP.SIPMessage;

public class TransactionLayerProxy extends TransactionLayer{
	
	InetAddress addressClient;
	InetAddress addressServer;
	int portClient;
	int portServer;
	ClientStateProxy client;
	ServerStateProxy server;
	String currentCallID;
	
	public InetAddress getAddressClient() {
		return addressClient;
	}

	public void setAddressClient(InetAddress addressClient) {
		this.addressClient = addressClient;
	}

	public int getPortClient() {
		return portClient;
	}

	public void setPortClient(int portClient) {
		this.portClient = portClient;
	}

	public TransactionLayerProxy() {
		super();
		this.client = ClientStateProxy.TERMINATED;
		this.server = ServerStateProxy.TERMINATED;
	}

	@Override
	public void recvFromTransport(SIPMessage message) {
		if(client == ClientStateProxy.TERMINATED && 
		   server == ServerStateProxy.TERMINATED) 
		{
			if (message instanceof InviteMessage) {
				String[] s = message.getVias().get(0).split(":");
				InetAddress address;
				try {
					addressServer = InetAddress.getByName(s[0]);
					portServer = Integer.valueOf(s[1]);
					server = ServerStateProxy.PROCEEDING;
					server.processMessage(message, this);
					//currentCallID =
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(client != ClientStateProxy.TERMINATED && 
				server != ServerStateProxy.TERMINATED) 
		{
			client.processMessage(message, this);
		}
	}

	public void recvFromUser(SIPMessage message) {
		if(client == ClientStateProxy.TERMINATED && 
		   server != ServerStateProxy.TERMINATED) 
		{
			if (message instanceof InviteMessage) {
				client = ClientStateProxy.CALLING;
				client.processMessage(message, this);
			}
		}
		else if(client != ClientStateProxy.TERMINATED && 
				server != ServerStateProxy.TERMINATED) 
		{
			server.processMessage(message, this);
		}
	}
	
	
	
	public void sendToTransportClient(SIPMessage message) throws IOException {
		transportLayer.sendToNetwork(addressClient, portClient, message);
	}
	
	public void sendToTransportServer(SIPMessage message) throws IOException {
		transportLayer.sendToNetwork(addressServer, portServer, message);
	}

}
