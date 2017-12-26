package fsm;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import layers.TransactionLayer;
import layers.Proxy.TransactionLayerProxy;
import layers.UA.TransactionLayerUA;
import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.ProxyAuthenticationMessage;
import mensajesSIP.RequestTimeoutMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.ServiceUnavailableMessage;
import mensajesSIP.TryingMessage;

public class ClientFSM {
	
	public static enum ClientState {CALLING, PROCEEDING, COMPLETED, TERMINATED}
	public ClientState currentState;
	public TransactionLayer transactionLayer;
	private Timer timer;
	private TimerTask task;
	
	public ClientFSM(TransactionLayer transactionLayer) {
		super();
		this.transactionLayer = transactionLayer;
		this.currentState = ClientState.TERMINATED;
		this.timer = new Timer();
		this.task = null;
	}
	
	public void sendACK(SIPMessage error) {
		
		ACKMessage ack = new ACKMessage();
		ArrayList<String> myVias;
		String destiantion;
   	 	
		if(transactionLayer instanceof TransactionLayerProxy) {
			destiantion = ((TransactionLayerProxy)transactionLayer).getDestination();
			ack.setDestination(destiantion);
			myVias =  new ArrayList<String>();
			myVias.add(((TransactionLayerProxy)transactionLayer).getMyStringVias());
			ack.setVias(myVias);
		}else if(transactionLayer instanceof TransactionLayerUA) {
			destiantion = "sip:proxy@dominio.es";
			ack.setDestination(destiantion);
			myVias = ((TransactionLayerUA)transactionLayer).getMyVias();
			ack.setVias(myVias);
		}
   	 	
   	 	ack.setCallId(transactionLayer.getCallId());
	 	ack.setToUri(error.getToUri());
	 	ack.setFromUri(error.getFromUri());
	 	ack.setcSeqNumber("1");
	 	ack.setcSeqStr("ACK");
	 	
	 	transactionLayer.sendRequest(ack);
   	 	
   	 	if(task == null) {
   	 		
   	 		transactionLayer.sendToUser(error);
   	 		
   	 		task = new TimerTask() {
			
				@Override
				public void run() { 
					System.out.println("CLIENT: COMPLETED -> TERMINATED");
					currentState = ClientState.TERMINATED;
					transactionLayer.resetLayer();
					task.cancel();
					task = null;
				}
				
   	 		};
		
			timer.schedule(task, 1000);
   	 	}
   	 	
		
	}
	

	public void setTransactionLayer(TransactionLayer transactionLayer) {
		this.transactionLayer = transactionLayer;
	}
	
	public void processMessage(SIPMessage message) {
		switch (currentState) {
		
			case TERMINATED:
				
				if(message instanceof InviteMessage) {
					System.out.println("CLIENT: CALLING -> CALLING");
					currentState = ClientState.CALLING;
					transactionLayer.sendRequest(message);
				}
				
				break;
			
			case CALLING:
				
				if (message instanceof TryingMessage || 
				    message instanceof RingingMessage) {
					System.out.println("CLIENT: CALLING -> PROCEEDING");
					currentState = ClientState.PROCEEDING;
					transactionLayer.sendToUser(message);
				}else if (message instanceof OKMessage) {
					System.out.println("CLIENT: CALLING -> TERMINATED");
					currentState = ClientState.TERMINATED;
					transactionLayer.sendToUser(message);
				}else if (message instanceof NotFoundMessage || 
						  message instanceof ProxyAuthenticationMessage ||
						  message instanceof RequestTimeoutMessage ||
						  message instanceof BusyHereMessage ||
						  message instanceof ServiceUnavailableMessage) 
				{
					System.out.println("CLIENT: CALLING -> COMPLETED");
					currentState = ClientState.COMPLETED;
					sendACK(message);
				}
				
				break;
			
			case PROCEEDING:
				
				if (message instanceof TryingMessage || 
				    message instanceof RingingMessage) {
					System.out.println("CLIENT: PROCEEDING -> PROCEEDING");
					transactionLayer.sendToUser(message);
				}else if (message instanceof OKMessage) {
					System.out.println("CLIENT: PROCEEDING -> TERMINATED");
					currentState = ClientState.TERMINATED;
					transactionLayer.sendToUser(message);
				}else if (message instanceof NotFoundMessage || 
						  message instanceof ProxyAuthenticationMessage ||
						  message instanceof RequestTimeoutMessage ||
						  message instanceof BusyHereMessage ||
						  message instanceof ServiceUnavailableMessage) 
				{
					System.out.println("CLIENT: PROCEEDING -> COMPLETED");
					currentState = ClientState.COMPLETED;
					sendACK(message);
				}
				
				break;
				
			case COMPLETED:
				
				if (message instanceof NotFoundMessage || 
					message instanceof ProxyAuthenticationMessage ||
					message instanceof RequestTimeoutMessage ||
					message instanceof BusyHereMessage ||
					message instanceof ServiceUnavailableMessage) 
					{
						System.out.println("CLIENT: COMPLETED -> COMPLETED");
						sendACK(message);
					}
				
				break;
				

			default:
				break;
				
		}
		
	}
	
	

}
