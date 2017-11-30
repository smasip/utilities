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
	
	DatagramPacket pClient;
	DatagramPacket pServer;
	ClientStateProxy client;
	ServerStateProxy server;
	String currentCallID;
	
	
	
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
					address = InetAddress.getByName(s[0]);
					int port = Integer.valueOf(s[1]);
					byte[] buf = new byte[1024];
					pServer = new DatagramPacket(buf, buf.length, address, port);
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

	public void recvFromUser(SIPMessage message, DatagramPacket p) {
		if(client == ClientStateProxy.TERMINATED && 
		   server != ServerStateProxy.TERMINATED) 
		{
			if (message instanceof InviteMessage) {
				pClient = p;
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
		byte[] buf = message.toStringMessage().getBytes();
		pClient.setData(buf, 0, buf.length);
		transportLayer.sendToNetwork(pClient);
	}
	
	public void sendToTransportServer(SIPMessage message) throws IOException {
		byte[] buf = message.toStringMessage().getBytes();
		pServer.setData(buf, 0, buf.length);
		transportLayer.sendToNetwork(pServer);
	}

}
