package layers.Proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fsm.ClientFSM;
import fsm.ClientState;
import fsm.ServerState;
import layers.*;
import mensajesSIP.*;

public class TransactionLayerProxy extends TransactionLayer{
	
	ClientState client;
	ServerState server;
	Transaction currentTransaction;
	private Timer timerServer;
	private TimerTask taskServer;
	private Timer timerClient;
	private TimerTask taskClient;
	private String destination;
	private String myStringVias;


	public void setMyStringVias(String myStringVias) {
		this.myStringVias = myStringVias;
	}
	
	


	public String getDestination() {
		return destination;
	}




	public void setDestination(String destination) {
		this.destination = destination;
	}




	public String getMyStringVias() {
		return myStringVias;
	}




	public TransactionLayerProxy() {
		super();
		this.client = ClientState.TERMINATED;
		this.server = ServerState.TERMINATED;
		this.currentTransaction = Transaction.NO_TRANSACTION;
		this.timerServer = new Timer();
		this.timerClient = new Timer();
		this.taskServer = null;
		this.taskClient = null;
		this.callId = null;
		this.destination = null;
		this.myStringVias = null;
	}
	

	public void sendACK(SIPMessage error) {
		
		ACKMessage ack = new ACKMessage();
		
		ArrayList<String> vias = new ArrayList<String>();
		vias.add(myStringVias);
   	 	
   	 	ack.setDestination(destination);
   	 	ack.setVias(vias);
   	 	ack.setCallId(callId);
	 	ack.setToUri(error.getToUri());
	 	ack.setFromUri(error.getFromUri());
	 	ack.setcSeqStr("ACK");
	 	ack.setcSeqNumber("1");
	 	
	 	sendRequest(ack);
   	 	
   	 	if(taskClient == null) {
   	 		
   	 		ul.recvFromTransaction(error);
   	 		
   	 		taskClient = new TimerTask() {
			
				@Override
				public void run() {
					System.out.println("CLIENT: COMPLETED -> TERMINATED");
					client = ClientState.TERMINATED;
					resetLayer();
					taskClient.cancel();
					taskClient = null;
				}
   	 		};
		
			timerClient.schedule(taskClient, 1000);
   	 	}
   	 	
		
	}
	
	
	public void sendError(SIPMessage error) {
			
		if(taskServer == null) {
			
   	 		taskServer = new TimerTask() {
   	 			
   	 			int numTimes = 0;
			
				@Override
				public void run() {
					if(numTimes < 4) {
						sendResponse(error);
						numTimes++;
					}else {
						System.out.println("SERVER: COMPLETED -> TERMINATED");
						server = ServerState.TERMINATED;
						resetLayer();
						taskServer.cancel();
						taskServer = null;
					}
				}
   	 		};
		
			timerServer.schedule(taskServer, 0, 200);
   	 	}
			
	}
	
	public void cancelTimer() {
		if(taskServer != null) {
			taskServer.cancel();
			taskServer = null;
		}
	}
	
	

	@Override
	public void recvFromTransport(SIPMessage message) {
		
		if(message instanceof RegisterMessage) {
			SIPMessage response = ((UserLayerProxyAbstract)ul).registerUser((RegisterMessage)message);
			sendResponse(response);
			return;
		}
		
		switch (currentTransaction) {
		
			case INVITE_TRANSACTION:
				
				if(!message.getCallId().equals(callId)) {
					ServiceUnavailableMessage serviceUnavailable = (ServiceUnavailableMessage) SIPMessage.createResponse(
							SIPMessage._503_SERVICE_UNABAILABLE, message);
					sendResponse(serviceUnavailable);
				}else if(message instanceof ACKMessage) {
					System.out.println("Entro");
					server = server.processMessage(message, this);
				}else{
					client = client.processMessage(message, this);
				}
				
				resetLayer();
				
				break;
				
			case NO_TRANSACTION:
				
				if(message instanceof InviteMessage) {
					currentTransaction = Transaction.INVITE_TRANSACTION;
					callId = message.getCallId();
					destination = ((InviteMessage)message).getDestination();
					server = ServerState.PROCEEDING;
					server = server.processMessage(message, this);
				}else{
					ul.recvFromTransaction(message);
				}
				
				break;
				
			default:
				break;
				
		}
		
	}
	
	public void recvRequestFromUser(SIPMessage request, InetAddress requestAddress, int requestPort) {
		
		this.requestAddress = requestAddress;
		this.requestPort = requestPort;
		
		switch (currentTransaction) {
		
			case INVITE_TRANSACTION:
				client = ClientState.CALLING;
				client = client.processMessage(request, this);
				break;
				
			case NO_TRANSACTION:
				sendRequest(request);
				break;
				
			default:
				break;
		}
	}
	
	public void recvResponseFromUser(SIPMessage response) {
		
		
		switch (currentTransaction) {
		
			case INVITE_TRANSACTION:
				server = server.processMessage(response, this);
				resetLayer();
				break;
				
			case NO_TRANSACTION:
				sendResponse(response);
				break;
				
			default:
				break;
		}
	}


	@Override
	public void resetLayer() {
		if((client == ClientState.TERMINATED) && (server == ServerState.TERMINATED)) {
			currentTransaction = Transaction.NO_TRANSACTION;
			callId = null;
			destination = null;
		}
	}
		

}
