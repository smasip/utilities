package layers;

import java.io.IOException;
import java.net.DatagramPacket;

import fsm.ClientStateUA;
import fsm.ServerStateUA;
import mensajesSIP.InviteMessage;
import mensajesSIP.SIPMessage;

public class TransactionLayerUA extends TransactionLayer{
	
	DatagramPacket pProxy;
	ClientStateUA client;
	ServerStateUA server;
	String currentCallID;
	
	public String getCurrentCallID() {
		return currentCallID;
	}

	public void setCurrentCallID(String currentCallID) {
		this.currentCallID = currentCallID;
	}

	public TransactionLayerUA() {
		super();
		this.client = null;
		this.server = null;
	}

	public DatagramPacket getpProxy() {
		return pProxy;
	}

	public void setpProxy(DatagramPacket pProxy) {
		this.pProxy = pProxy;
	}

	@Override
	public void recvFromTransport(SIPMessage message) {
		if(client == null && server == null) {
			if (message instanceof InviteMessage) {
				server = ServerStateUA.PROCEEDING;
				server.processMessage(message, this);
			}
		}else if(server == null) {
			if (!(message instanceof InviteMessage)) {
				client.processMessage(message, this);
			}
		}
	}

	public void recvFromUser(SIPMessage message) {
		if(client == null && server == null) {
			if (message instanceof InviteMessage) {
				client = ClientStateUA.CALLING;
				client.processMessage(message, this);
			}
		}else if(client == null) {
			if (!(message instanceof InviteMessage)) {
				server.processMessage(message, this);
			}
		}
	}
	
	public void sendToTransport(SIPMessage message) throws IOException {
		byte[] buf = message.toStringMessage().getBytes();
		pProxy.setData(buf, 0, buf.length);
		transportLayer.sendToNetwork(pProxy);
	}


}
