package fsm;

import java.util.Timer;
import java.util.TimerTask;

import layers.TransactionLayer;
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

public abstract class ServerFSM {
	
	public static enum ServerState {PROCEEDING, COMPLETED, TERMINATED}
	public ServerState currentState;
	protected TransactionLayer transactionLayer;
	private Timer timer;
	private TimerTask task;
	
	public ServerFSM(TransactionLayer transactionLayer) {
		super();
		this.transactionLayer = transactionLayer;
		this.currentState = ServerState.TERMINATED;
		this.timer = new Timer();
		this.task = null;
	}
	
	public boolean isTerminated() {
		return (currentState == ServerState.TERMINATED);
	}
	
	private void sendError(SIPMessage error) {
		
		if(task == null) {
			
   	 		task = new TimerTask() {
   	 			
   	 			int numTimes = 0;
			
				@Override
				public void run() {
					if(numTimes < 4) {
						transactionLayer.sendResponse(error);
						numTimes++;
					}else {
						System.out.println("SERVER: COMPLETED -> TERMINATED");
						currentState = ServerState.TERMINATED;
						transactionLayer.resetLayer();
						task.cancel();
						task = null;
					}
				}
   	 		};
		
			timer.schedule(task, 0, 200);
   	 	}
			
	}
	
	public void cancelTimer() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}
	
	public abstract void onInvite(InviteMessage invite);
	
	public void processMessage(SIPMessage message) {
		
		switch (currentState) {
		
			case TERMINATED:
				
				if (message instanceof InviteMessage) {
					System.out.println("SERVER: PROCEEDING -> PROCEEDING");
					currentState = ServerState.PROCEEDING;
					onInvite((InviteMessage)message);
				}
				
				break;
				
			case PROCEEDING:
				
				if (message instanceof RingingMessage) {
					System.out.println("SERVER: PROCEEDING -> PROCEEDING");
					transactionLayer.sendResponse(message);
				}else if (message instanceof NotFoundMessage || 
						  message instanceof ProxyAuthenticationMessage ||
						  message instanceof RequestTimeoutMessage ||
						  message instanceof BusyHereMessage ||
						  message instanceof ServiceUnavailableMessage) {
					System.out.println("SERVER: PROCEEDING -> COMPLETED");
					currentState = ServerState.COMPLETED;
					sendError(message);
				}else if (message instanceof OKMessage) {
					System.out.println("SERVER: PROCEEDING -> TERMINATED");
					currentState = ServerState.TERMINATED;
					transactionLayer.sendResponse(message);
					transactionLayer.resetLayer();
				}
				
				break;
				
			case COMPLETED:
				
				if(message instanceof ACKMessage) {
					System.out.println("SERVER: COMPLETED -> TERMINATED");
					currentState = ServerState.TERMINATED;
					cancelTimer();
					transactionLayer.resetLayer();
				}
				
				break;
	
			default:
				break;
				
		}
		
	}
	

}
