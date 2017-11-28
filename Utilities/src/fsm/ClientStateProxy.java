package fsm;

import java.io.IOException;

import layers.*;
import mensajesSIP.*;

public enum ClientStateProxy {
	CALLING {
		@Override
		public ClientStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			System.out.println("CALLING");
			System.out.println(message.toStringMessage());
			if(message instanceof InviteMessage) {
				try {
					((TransactionLayerProxy)tl).sendToTransportClient(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return this;
			}else if (message instanceof RingingMessage) {
				tl.sendToUser(message);
				return PROCEEDING;
			}else if (message instanceof OKMessage) {
				return TERMINATED;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) {
				return COMPLETED;
			}
			return this;
		}

	},
	PROCEEDING{
		@Override
		public ClientStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			System.out.println("PROCEEDING");
			if (message instanceof TryingMessage || 
			    message instanceof RingingMessage) {
				return this;
			}else if (message instanceof OKMessage) {
				return TERMINATED;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) {
				// Falta send ACK y resp to TU
				return COMPLETED;
			}
			return this;
		}

	},
	COMPLETED{
		@Override
		public ClientStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			return TERMINATED;
		}
		
	},
	TERMINATED{
		@Override
		public ClientStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			return this;
		}
		
	};
	
	public abstract ClientStateProxy processMessage(SIPMessage message, TransactionLayer tl);

}
