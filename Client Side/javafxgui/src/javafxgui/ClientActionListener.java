package javafxgui;
public interface ClientActionListener {
public void MsgRecive(NetConnect netconn, String msg);
    public void InfoRecive(NetConnect netconn, String info);
    public void ClientDiscon(NetConnect netconn);
    public void ClientException(NetConnect netconn, Exception e); 
    public void MessageListRecive(NetConnect netconn, String message);
    void getMyInfo(NetConnect netconn, String info);
    void ccResourcesRecive(NetConnect netconn, String resources);}
