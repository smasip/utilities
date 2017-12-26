package fsm;

import java.io.IOException;

import mensajesSIP.*;
import layers.*;
import layers.Proxy.TransactionLayerProxy;

public enum ServerState {
	
	
	PROCEEDING{
		@Override
		public ServerState processMessage(SIPMessage message, TransactionLayer tl) {
			
			if (message instanceof InviteMessage) {
				System.out.println("SERVER: PROCEEDING -> PROCEEDING");
				if(tl instanceof TransactionLayerProxy) {
					TryingMessage tryingMessage = (TryingMessage) SIPMessage.createResponse(SIPMessage._100_TRYING, message);
					tl.sendResponse(tryingMessage);
				}
				tl.sendToUser(message);
				return this;
			}else if (message instanceof RingingMessage) {
				System.out.println("SERVER: PROCEEDING -> PROCEEDING");
				tl.sendResponse(message);
				return this;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) {
				System.out.println("SERVER: PROCEEDING -> COMPLETED");
				tl.sendError(message);
				return COMPLETED;
			}else if (message instanceof OKMessage) {
				System.out.println("SERVER: PROCEEDING -> TERMINATED");
				tl.sendResponse(message);
				return TERMINATED;
			}
			System.out.println("entro3");
			return this;
		}

	},
	COMPLETED{
		@Override
		public ServerState processMessage(SIPMessage message, TransactionLayer tl) {
			System.out.println("entro2");
			if(message instanceof ACKMessage) {
				System.out.println("SERVER: COMPLETED -> TERMINATED");
				tl.cancelTimer();
				return TERMINATED;
			}
			
			return this;
		}
		
	},
	TERMINATED{
		@Override
		public ServerState processMessage(SIPMessage message, TransactionLayer tl) {
			return this;
		}
		
	};
	
	public abstract ServerState processMessage(SIPMessage message, TransactionLayer tl);


}
