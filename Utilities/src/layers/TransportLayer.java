package layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import mensajesSIP.SIPException;
import mensajesSIP.SIPMessage;

public abstract class TransportLayer {
	
	public DatagramSocket datagramSocket;
	public TransactionLayer transactionLayer;
	
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

	public abstract void recvFromNetwork();
	
	public void sendToNetwork(InetAddress address, int port, SIPMessage message) throws IOException {
		System.out.println("Sended Message:");
		System.out.println(message.toStringMessage());
		byte[] buf = message.toStringMessage().getBytes();
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		p.setAddress(address);
		p.setPort(port);
		datagramSocket.send(p);
	}

}
