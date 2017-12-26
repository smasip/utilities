package layers.Proxy;

import layers.UserLayer;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SIPMessage;

public abstract class UserLayerProxyAbstract extends UserLayer {

	public abstract SIPMessage registerUser(RegisterMessage register);

}
