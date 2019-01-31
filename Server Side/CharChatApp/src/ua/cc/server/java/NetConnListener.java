package ua.cc.server.java;

public interface NetConnListener {
    void onConnectReady(NetConnect netconn);

    void onReceive(NetConnect netconn, String value);

    void onDisconnect(NetConnect netconn);

    void onException(NetConnect netconn, Exception e);

    void UserToUser(NetConnect netconn, String value);

    void ImOnline(NetConnect netconn);

    void getInfo(NetConnect netconn, String info);

    void onClientCommand(NetConnect netconn, String command);

    void onGiveMessageList(NetConnect netconn, String listName);

    void getMyInfo(NetConnect netconn);

    void infoDelete(NetConnect netcon, String delete);

    void giveTools(NetConnect netcon, String tools);
}
