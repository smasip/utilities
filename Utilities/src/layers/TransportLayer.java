package layers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import mensajesSIP.OKMessage;
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

	public void recvFromNetwork(){

		byte[] buf = new byte[2048];
    	DatagramPacket p = new DatagramPacket(buf, buf.length);
    	SIPMessage message;
    	while(true) {
    		try {
    			datagramSocket.receive(p);
    			message = SIPMessage.parseMessage(new String(p.getData()));
    			System.out.println();
    			System.out.println("Received Message:");
    			if (message instanceof OKMessage) {
    				((OKMessage)message).setSdp(null);
    			}
    			System.out.println(message.toStringMessage());
    			System.out.println();
    			transactionLayer.recvFromTransport(message);
				p.setData(buf, 0, buf.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SIPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		
	}
	
	public void sendToNetwork(InetAddress address, int port, SIPMessage message) throws IOException {
		System.out.println();
		System.out.println("Sended Message:");
		System.out.println(message.toStringMessage());
		System.out.println();
		byte[] buf = message.toStringMessage().getBytes();
		DatagramPacket p = new DatagramPacket(buf, buf.length);
		p.setAddress(address);
		p.setPort(port);
		datagramSocket.send(p);
	}

}
