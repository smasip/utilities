package fsm;

import java.io.IOException;

import layers.*;
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

public enum ServerStateProxy {
	
	
	PROCEEDING{
		@Override
		public ServerStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			System.out.println("PROCEEDING");
			System.out.println(message.toStringMessage());
			if (message instanceof InviteMessage) {
				try {
					TryingMessage tryingMessage = (TryingMessage) SIPMessage.createResponse(SIPMessage._100_TRYING, message);
					((TransactionLayerProxy) tl).sendToTransportServer(tryingMessage);
					tl.sendToUser(message);
					return this;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if (message instanceof RingingMessage) {
				try {
					((TransactionLayerProxy) tl).sendToTransportServer(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return this;
			}else if (message instanceof NotFoundMessage || 
					  message instanceof ProxyAuthenticationMessage ||
					  message instanceof RequestTimeoutMessage ||
					  message instanceof BusyHereMessage ||
					  message instanceof ServiceUnavailableMessage) {
				// Falta send response
				return COMPLETED;
			}else if (message instanceof OKMessage) {
				//Falta send response
				return TERMINATED;
			}
			return this;
		}

	},
	COMPLETED{
		@Override
		public ServerStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			return TERMINATED;
		}
		
	},
	TERMINATED{
		@Override
		public ServerStateProxy processMessage(SIPMessage message, TransactionLayer tl) {
			System.out.println("TERMINATED");
			System.out.println(message.toStringMessage());
			return this;
		}
		
	};
	
	public abstract ServerStateProxy processMessage(SIPMessage message, TransactionLayer tl);


}
