package fsm;

import mensajesSIP.*;
import layers.*;

public enum ClientState {
	CALLING {
		@Override
		public ClientState processMessage(SIPMessage message, TransactionLayer tl) {
			
			if(message instanceof InviteMessage) {
				System.out.println("CLIENT: CALLING -> CALLING");
				tl.sendRequest(message);
				return this;
			}else if (message instanceof TryingMessage || 
					  message instanceof RingingMessage) {
				System.out.println("CLIENT: CALLING -> PROCEEDING");
				tl.sendToUser(message);
				return PROCEEDING;
			}else if (message instanceof OKMessage) {
				System.out.println("CLIENT: CALLING -> TERMINATED");
				tl.sendToUser(message);
				return TERMINATED;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) 
			{
				System.out.println("CLIENT: CALLING -> COMPLETED");
				tl.sendACK(message);
				return COMPLETED;
			}
			return this;
		}

	},
	PROCEEDING{
		@Override
		public ClientState processMessage(SIPMessage message, TransactionLayer tl) {
			
			if (message instanceof TryingMessage || 
			    message instanceof RingingMessage) {
				System.out.println("CLIENT: PROCEEDING -> PROCEEDING");
				tl.sendToUser(message);
				return this;
			}else if (message instanceof OKMessage) {
				System.out.println("CLIENT: PROCEEDING -> TERMINATED");
				tl.sendToUser(message);
				return TERMINATED;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) 
			{
				System.out.println("CLIENT: PROCEEDING -> COMPLETED");
				tl.sendACK(message);
				return COMPLETED;
			}
			
			return this;
		}

	},
	COMPLETED{
		@Override
		public ClientState processMessage(SIPMessage message, TransactionLayer tl) {
			if (message instanceof NotFoundMessage || 
				message instanceof ProxyAuthenticationMessage ||
				message instanceof RequestTimeoutMessage ||
				message instanceof BusyHereMessage ||
				message instanceof ServiceUnavailableMessage) 
			{
				System.out.println("CLIENT: COMPLETED -> COMPLETED");
				tl.sendACK(message);
			}
			
			return this;
			
		}
		
	},
	TERMINATED{
		@Override
		public ClientState processMessage(SIPMessage message, TransactionLayer tl) {
			return this;
		}
		
	};
	
	public abstract ClientState processMessage(SIPMessage message, TransactionLayer tl);

}
