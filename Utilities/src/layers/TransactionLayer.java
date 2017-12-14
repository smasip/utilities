package layers;

import java.net.DatagramPacket;

import mensajesSIP.SIPMessage;


public abstract class TransactionLayer {
	
	public UserLayer ul;
	public TransportLayer transportLayer;

	public UserLayer getUl() {
		return ul;
	}

	public void setUl(UserLayer ul) {
		this.ul = ul;
	}

	public TransportLayer getTransportLayer() {
		return transportLayer;
	}

	public void setTransportLayer(TransportLayer transportLayer) {
		this.transportLayer = transportLayer;
	}

	public abstract void recvFromTransport(SIPMessage message);
	
	public void sendToUser(SIPMessage message) {
		ul.recvFromTransaction(message);
	}
	
	public abstract void sendACK(SIPMessage error);
	
	public abstract void sendError(SIPMessage error);
	
	public abstract void cancelTimer();

}
