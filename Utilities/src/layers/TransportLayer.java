package layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import mensajesSIP.SIPException;
import mensajesSIP.SIPMessage;

public class TransportLayer {
	
	private DatagramSocket datagramSocket;
	private TransactionLayer transactionLayer;
	
	
	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	public void setDatagramSocket(DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}

	public TransactionLayer getTransactionLayer() {
		return transactionLayer;
	}

	public void setTransactionLayer(TransactionLayer transactionLayer) {
		this.transactionLayer = transactionLayer;
	}

	public void recvFromNetwork(DatagramPacket p) throws SIPException {
		SIPMessage message = SIPMessage.parseMessage(new String(p.getData()));
		transactionLayer.recvFromTransport(message);
	}
	
	public void sendToNetwork(DatagramPacket p) throws IOException {
		datagramSocket.send(p);
	}

}
