package layers;

import mensajesSIP.SIPMessage;

public abstract class UserLayer {
	
	protected TransactionLayer transactionLayer;
	
	
	public TransactionLayer getTransactionLayer() {
		return transactionLayer;
	}

	public void setTransactionLayer(TransactionLayer transactionLayer) {
		this.transactionLayer = transactionLayer;
	}


	public abstract void recvFromTransaction(SIPMessage message);

}
