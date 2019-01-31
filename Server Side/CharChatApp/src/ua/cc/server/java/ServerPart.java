package ua.cc.server.java;

import static ua.cc.server.java.ServerLogs.writeLogs;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ServerPart implements NetConnListener {

    private SQLConnection1 sqlcon = new SQLConnection1();
    private static final ArrayList<NetConnect> connections = new ArrayList<>();
    private static final HashMap<NetConnect, Integer> onlines = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Date date;

    private final int MAX_SIZE = 1000000;
    private String FileExtention = "", UserIPAdd = "";

    public static void main(String[] args) {
        new ServerPart();
    }

    private ServerPart() {
        System.out.println("Server running...");
        try (ServerSocket serversocket = new ServerSocket(3333);) {
            while (true) {
                try {
                    new NetConnect(this, serversocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectReady(NetConnect netconn) {
        connections.add(netconn);
        writeLogs("Client connected: " + netconn.getLogs());
        System.out.println("Client connected: " + netconn.getLogs());

    }

    @Override
    public synchronized void onReceive(NetConnect netconn, String value) {
        try {
            System.out.println("ONRECIIVER: " + value);
            int cal = connections.size();
            int k = 0, z = 13;
            while (true) {
                if (value.trim().substring(k, z).equalsIgnoreCase("UserIPAddress")) {
                    UserIPAdd = (value.trim().substring(0, k));
                    System.out.println("UserIPAdd" + UserIPAdd);
                    break;
                }
                k++;
                z++;
            }

            String filtredMsg = filterMsg(value.substring(z));
            System.out.println("FILTRED MSG: filtredMssg" + filtredMsg);
            for (int i = 0; i < cal; i++) {
                if (connections.get(i).toString().equals(UserIPAdd)) {
                    connections.get(i).sendToClient("LOGI" + filtredMsg);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL SendMsg Exception!");
        }
    }

    @Override
    public synchronized void onDisconnect(NetConnect netconn) {
        connections.remove(netconn);
        sqlcon.sqlUpdate("Update users SET UserOnline = 'offline' WHERE UserID = '" + onlines.get(netconn) + "'");
        onlines.remove(netconn);
        writeLogs("Client disconnected: " + netconn.getLogs());
        System.out.println("Client disconnected: " + netconn.getLogs());
    }

    @Override
    public synchronized void onException(NetConnect netconn, Exception e) {
        writeLogs("TCPConnection exception: +" + e);
        System.out.println("TCPConnection exception: +" + e);
    }

    private synchronized String filterMsg(String msg) throws SQLException {
        String command = msg.substring(0, 6);
        switch (command) {
            case "INSERT":

                if (sqlcon.sqlInsert(msg).equals("Inserttrue")) {
                    String userID = sqlcon.sqlSelectAll("MAX(UserID)", "users");
                    sqlcon.sqlInsert("INSERT INTO friendsids(friendsids.`UserID`) values('" + userID + "')");
                    sqlcon.sqlInsert("UPDATE users SET UserFriends ='" + sqlcon.sqlSelectAll("FriendsID", "friendsids", "UserID", ""
                            + userID) + "' WHERE UserID = '" + userID + "'");
                    return "Inserttrue";
                } else {
                    return "Insertfalse";
                }
            case "UPDATE":

                sqlcon.sqlUpdate(msg);
                return "UpdateDone";
            default:

                // if msg.startWith("Select") ->
                String str = sqlcon.sqlSelect(msg);
                return str;
        }
    }

    private synchronized String getDate(String msg) {
        int g1 = 3, h1 = 7;
        String strDate = "";
        while (true) {
            if (msg.trim().substring(g1, h1).equalsIgnoreCase("Date")) {
                strDate = msg.trim().substring(h1, h1 + 19);
                return strDate;
            }
            g1++;
            h1++;
        }
    }

    private synchronized Integer toWho(String msg) throws SQLException {
        int g = 1, h = 3;
        while (true) {
            if (msg.trim().substring(g, h).equalsIgnoreCase("id")) {
                Integer towho = Integer.parseInt(msg.trim().substring(0, g));
                return towho;
            }
            g++;
            h++;
        }
    }

    @Override
    public synchronized void UserToUser(NetConnect netconn, String value) {
        try {
            String msg = value, clearmsg = "", forSql = "", field = "MessageText";
            ;
            Integer towho = toWho(value);
            int g2 = 3, h2 = 7;
            while (true) {
                if (msg.substring(g2, h2).equalsIgnoreCase("Date")) {
                    clearmsg = msg.substring(h2 + 19);
                    forSql = msg.substring(h2 + 19);
                    break;
                }
                g2++;
                h2++;
            }
            if (clearmsg.startsWith("FILE")) {
                clearmsg = FileRecive(clearmsg.substring(4));
                int u = 40, j = 44;
                while (true) {
                    if (clearmsg.substring(u, j).equalsIgnoreCase("FILE")) {
                        forSql = clearmsg.substring(0, u);
                        clearmsg = clearmsg.substring(u);
                        break;
                    }
                    u++;
                    j++;
                }
                switch (FileExtention.toLowerCase()) {
                    case "png":
                    case "jpg":
                    case "bmp":
                        field = "ImagePath";
                        break;
                    case "txt":
                    case "pdf":
                        field = "FilePath";
                        break;
                    case "mp3":
                        field = "AudioPath";
                        break;
                }
            }
            System.out.println("1");
            sqlcon.sqlInsert("INSERT INTO messages(messages.`" + field + "`, messages.`MessageID`, messages.`MessageDate`, messages.`MessageSort`) "
                    + "values('" + forSql + "','" + sqlcon.sqlSelectAll("MessageID", "messageids", "SenderID", ""
                    + onlines.get(netconn), "ReciverID", towho + "") + "','" + getDate(value) + "','0')");
            System.out.println("2");
            sqlcon.sqlInsert("INSERT INTO messages(messages.`" + field + "`, messages.`MessageID`, messages.`MessageDate`, messages.`MessageSort`) "
                    + "values('" + forSql + "','" + sqlcon.sqlSelectAll("MessageID", "messageids", "SenderID", ""
                    + towho, "ReciverID", onlines.get(netconn) + "") + "','" + getDate(value) + "','1')");
            System.out.println("3");
            for (Map.Entry<NetConnect, Integer> m : onlines.entrySet()) {
                if (m.getValue().equals(towho)) {
                    System.out.println("4");
                    System.out.println("SELECT UserOnline FROM users WHERE UserID = " + onlines.get(netconn) + "");
                    ResultSet ui = sqlcon.getResultSet("SELECT UserOnline FROM users WHERE UserID = " + onlines.get(netconn) + "");
                    String onl = "";
                    System.out.println("41");
                    while (ui.next()) {
                        onl = ui.getString("UserOnline");
                    }
                    System.out.println("46");
                    if (onl.equalsIgnoreCase("online")) {
                        System.out.println("5");
                        m.getKey().sendToUser("MESG" + clearmsg);
                    }
                    break;
                }
            }

        } catch (SQLException e) {
            System.out.println("UserToUser SQLException");
        } catch (Exception ex) {
            System.out.println("UserToUser IOException" + ex.getLocalizedMessage());
        }
    }

    @Override
    public synchronized void ImOnline(NetConnect netconn) {
        onlines.put(netconn, sqlcon.id);
        sqlcon.sqlUpdate("Update users SET UserOnline = 'online' WHERE UserID = '" + onlines.get(netconn) + "'");
        connections.remove(netconn);
        writeLogs("ClientOnline" + netconn.getLogs());
    }

    public synchronized String FileRecive(String file) throws Exception {
        int z = 3, b = 7;
        while (true) {
            if (file.substring(z, b).equalsIgnoreCase("FILE")) {
                FileExtention = file.substring(0, z);
                break;
            }
        }

        String clearFile = file.substring(b, file.length());
        byte[] bytesDestiny = convertBytesToString(clearFile);

        int fileName = 1 + Integer.parseInt(sqlcon.sqlSelectAll("MAX(MsgID)", "Messages"));
        String filePath = "C:\\\\Users\\\\Admin\\\\CharChatResources\\\\Pictures\\\\" + fileName + "." + FileExtention + "";

        switch (FileExtention) {
            case "png":
            case "jpg":
            case "bmp":
            case "PNG":
            case "JPG":
            case "BMP":
                InputStream in = new ByteArrayInputStream(bytesDestiny);
                BufferedImage image = ImageIO.read(in);
                ImageIO.write(image, FileExtention, new File(filePath));
                return filePath + "FILEIMAGEXTE" + FileExtention + Arrays.toString(bytesDestiny);
            case "MP3":
            case "mp3":

                File fileAudio = new File(filePath);
                try (FileOutputStream fileOut = new FileOutputStream(fileAudio)) {
                    fileOut.write(bytesDestiny);
                } catch (Exception e) {
                    System.out.println("File send Exception ");
                }

                return filePath + "FILEAUDIEXTE" + FileExtention + Arrays.toString(bytesDestiny);

            case "TXT":
            case "txt":
                File fileSave = new File(filePath);
                try (FileOutputStream fileOut = new FileOutputStream(fileSave)) {
                    fileOut.write(bytesDestiny);
                } catch (Exception e) {
                    System.out.println("File send Exception ");
                }
                return filePath + "FILEFILEEXTE" + FileExtention + Arrays.toString(bytesDestiny);
            default:
                System.out.println("Unknown");
                return null;
        }
    }

    @Override
    public synchronized void getInfo(NetConnect netconn, String info) {
        try {
            String rsMsg = "", triger = info.substring(0, 6), plus;
            ResultSet resultSet, resultMiss;
            int i = 0, g = 0, d = 0, b = 0, l = 0, p = 0, f = 0, h = 0, size, w = 0, a = 0, m = 0;
            byte[] fb;
            switch (triger) {
                case "FRIEND":
                    resultSet = sqlcon.getResultSet("SELECT * FROM users WHERE `UserID` \n"
                            + "IN (SELECT UserID FROM userfriends WHERE FriendsIDs = (SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + "))");
                    resultSet.last();
                    int threadArrLength = resultSet.getRow();
                    netconn.sendToClient("INFOSIZE" + ((threadArrLength * 11) + 1));
                    resultSet.beforeFirst();
                    ResultSet resultId = sqlcon.getResultSet("SELECT UserID FROM userfriends WHERE FriendsIDs = "
                            + "(SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + ")");
                    while (resultSet.next()) {
                        resultMiss = sqlcon.getResultSet("SELECT * FROM messages WHERE MessageReaded = 0 and MessageSort = 0 and MessageID = "
                                + "(SELECT MessageID FROM messageids WHERE SenderID = " + resultSet.getInt("UserID") + " and ReciverID = " + onlines.get(netconn) + " )");
                        resultMiss.last();
                        int row = resultMiss.getRow();
                        netconn.sendToClient("INFOSERCUSMI" + ++m + "N" + row);
                    }
                    resultSet.beforeFirst();
                    while (resultId.next()) {
                        ResultSet resultMsg = sqlcon.getResultSet("SELECT * FROM messages WHERE  \n"
                                + "MsgID = (SELECT MAX(MsgID) FROM messages WHERE MessageID = (SELECT messageids.`MessageID` FROM messageids WHERE SenderID = " + onlines.get(netconn) + " "
                                + "and ReciverID = " + resultId.getInt("UserID") + "))\n" + "and \n"
                                + "MessageID = (SELECT messageids.`MessageID` FROM messageids WHERE SenderID = " + onlines.get(netconn) + " and ReciverID = " + resultId.getInt("UserID") + ")");
                        if (resultMsg.next()) {
                            rsMsg = resultMsg.getString("MessageText");
                            rsMsg = (rsMsg.equals("empty")) ? "Media File" : rsMsg;
                        }
                        netconn.sendToClient("INFOSERCLSMS" + ++f + "N" + rsMsg);
                    }
                    ResultSet friendStatus = sqlcon.getResultSet("SELECT FriendAdded FROM userfriends WHERE FriendsIDs = "
                            + "(SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + ")");
                    while (friendStatus.next()) {
                        netconn.sendToClient("INFOSERCUSFR" + ++w + "N" + friendStatus.getInt("FriendAdded"));
                    }
                    resultSet.last();
                    size = resultSet.getRow();
                    resultSet.beforeFirst();
                    netconn.sendToClient("INFOSERCSIZE" + size);
                    while (resultSet.next()) {
                        netconn.sendToClient("INFOSERCUSID" + ++i + "N" + resultSet.getInt("UserID"));
                        netconn.sendToClient("INFOSERCUSNM" + ++g + "N" + resultSet.getString("UserName"));
                        netconn.sendToClient("INFOSERCULNM" + ++d + "N" + resultSet.getString("UserLastName"));
                        netconn.sendToClient("INFOSERCUSBI" + ++b + "N" + resultSet.getString("UserBirthday"));
                        netconn.sendToClient("INFOSERCUGEN" + ++l + "N" + resultSet.getString("UserGender"));
                        netconn.sendToClient("INFOSERCUSCO" + ++h + "N" + resultSet.getString("UserCountry"));
                        netconn.sendToClient("INFOSERCUSIN" + ++a + "N" + resultSet.getString("UserInformation"));
                        try {
                            plus = "INFOSERCUSPH" + ++p + "N" + Arrays.toString(
                                    java.nio.file.Files.readAllBytes(new File(resultSet.getString("UserPhoto")).toPath()));
                            fb = plus.getBytes();
                            netconn.sendBytes(fb);
                        } catch (IOException e) {
                        }
                    }
                    break;
                case "SEARCH":
                    resultSet = sqlcon.getResultSet("SELECT * FROM users WHERE UserID != '" + onlines.get(netconn) + "' " + info.substring(6) + " and UserID NOT IN \n"
                            + "(SELECT UserID FROM userfriends WHERE FriendsIDs = \n" + "(SELECT UserFriends FROM users WHERE UserID = '" + onlines.get(netconn) + "'));");
                    resultSet.last();
                    size = resultSet.getRow();
                    netconn.sendToClient("INFOSIZE" + ((size * 8) + 1));
                    resultSet.beforeFirst();
                    netconn.sendToClient("INFOSERCSIZE" + size);
                    while (resultSet.next()) {
                        netconn.sendToClient("INFOSERCUSID" + ++i + "N" + resultSet.getInt("UserID"));
                        netconn.sendToClient("INFOSERCUSNM" + ++g + "N" + resultSet.getString("UserName"));
                        netconn.sendToClient("INFOSERCULNM" + ++d + "N" + resultSet.getString("UserLastName"));
                        netconn.sendToClient("INFOSERCUSBI" + ++b + "N" + resultSet.getString("UserBirthday"));
                        netconn.sendToClient("INFOSERCUGEN" + ++l + "N" + resultSet.getString("UserGender"));
                        netconn.sendToClient("INFOSERCUSCO" + ++h + "N" + resultSet.getString("UserCountry"));
                        netconn.sendToClient("INFOSERCUSIN" + ++a + "N" + resultSet.getString("UserInformation"));
                        try {
                            plus = "INFOSERCUSPH" + ++p + "N" + Arrays.toString(
                                    java.nio.file.Files.readAllBytes(new File(resultSet.getString("UserPhoto")).toPath()));
                            fb = plus.getBytes();
                            netconn.sendBytes(fb);
                        } catch (IOException e) {
                        }
                    }
            }
        } catch (SQLException ex) {
        }
    }

    @Override
    public synchronized void getMyInfo(NetConnect netconn) {
        try {
            String plus;
            byte[] fb;
            ResultSet ResultMy = sqlcon.getResultSet("SELECT * FROM users WHERE UserID = '" + onlines.get(netconn) + "'");
            netconn.sendToClient("MYINSIZE" + 9);
            while (ResultMy.next()) {
                netconn.sendToClient("MYINUSID" + ResultMy.getInt("UserID"));
                netconn.sendToClient("MYINUSNM" + ResultMy.getString("UserName"));
                netconn.sendToClient("MYINULNM" + ResultMy.getString("UserLastName"));
                netconn.sendToClient("MYINUSBI" + ResultMy.getString("UserBirthday"));
                netconn.sendToClient("MYINUGEN" + ResultMy.getString("UserGender"));
                netconn.sendToClient("MYINUSCO" + ResultMy.getString("UserCountry"));
                netconn.sendToClient("MYINUSIN" + ResultMy.getString("UserInformation"));
                netconn.sendToClient("MYINUSPT" + ResultMy.getString("UserPath"));
                plus = "MYINUSPH" + Arrays.toString(java.nio.file.Files.readAllBytes(new File(ResultMy.getString("UserPhoto")).toPath()));
                fb = plus.getBytes();
                netconn.sendBytes(fb);
            }
        } catch (IOException e) {
        } catch (SQLException e) {
        }
    }

    @Override
    public synchronized void onClientCommand(NetConnect netconn, String command) {
        try {
            String FriendID = command.substring(5, command.length());
            String commandStart = command.substring(0, 3);
            switch (commandStart) {
                case "ADD":
                    sqlcon.sqlInsert("INSERT INTO userfriends(userfriends.`FriendsIDs`, userfriends.`UserID`, userfriends.`FriendAdded`) values('"
                            + sqlcon.sqlSelectAll("UserFriends", "users", "UserID", "" + onlines.get(netconn)) + "','" + FriendID + "', '0')");
                    sqlcon.sqlInsert("INSERT INTO userfriends(userfriends.`FriendsIDs`, userfriends.`UserID`, userfriends.`FriendAdded`) values('"
                            + sqlcon.sqlSelectAll("UserFriends", "users", "UserID", "" + FriendID) + "','" + onlines.get(netconn) + "', '2')");
                    sqlcon.sqlInsert("INSERT INTO messageids(messageids.`SenderID`, messageids.`ReciverID`)"
                            + "values('" + onlines.get(netconn) + "','" + FriendID + "')");
                    sqlcon.sqlInsert("INSERT INTO messageids(messageids.`SenderID`, messageids.`ReciverID`)"
                            + "values('" + FriendID + "','" + onlines.get(netconn) + "')");
                    break;
                case "CNG":
                    sqlcon.sqlUpdate(command.substring(3) + "'" + onlines.get(netconn) + "'");
                    getMyInfo(netconn);
                    break;
                case "FOT":
                    int a = 0, b = 4;
                    String ext = "";
                    while (true) {
                        if (command.substring(a, b).equalsIgnoreCase("EXTE")) {
                            ext = command.substring(3, a);
                            break;
                        }
                        a++;
                        b++;
                    }
                    InputStream in = new ByteArrayInputStream(convertBytesToString(command.substring(b)));
                    BufferedImage bufimg = ImageIO.read(in);
                    ImageIO.write(bufimg, "png",
                            new File("C:\\\\Users\\\\Admin\\\\CharChatResources\\\\Pictures\\\\" + "UID" + onlines.get(netconn) + "." + ext));
                    sqlcon.sqlUpdate("UPDATE users SET " + "UserPhoto = '" + "C:\\\\Users\\\\Admin\\\\CharChatResources\\\\Pictures\\\\" + "UID" + onlines.get(netconn) + "." + ext + "'"
                            + "WHERE UserID = '" + onlines.get(netconn) + "'");
                    break;
                case "CNF"
                    sqlcon.sqlUpdate("UPDATE userfriends SET FriendAdded = 1 WHERE FriendsIDs = "
                            + "(SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + " ) and UserID = " + FriendID + " ");
                    sqlcon.sqlUpdate("UPDATE userfriends SET FriendAdded = 1 WHERE FriendsIDs = "
                            + "(SELECT UserFriends FROM users WHERE UserID = " + FriendID + " ) and UserID = " + onlines.get(netconn) + " ");
                    break;
                case "CDF":
                    sqlcon.sqlUpdate("delete from userfriends\n"
                            + "where FriendsIDs = (SELECT UserFriends FROM users WHERE UserID = " + FriendID + " ) and UserID = " + onlines.get(netconn) + ";");
                    break;
                case "RMS":
                    sqlcon.sqlUpdate("UPDATE messages SET MessageReaded = 1 WHERE MessageID = "
                            + "(SELECT MessageID FROM messageids WHERE SenderID = " + FriendID + " and ReciverID = " + onlines.get(netconn) + " "
                            + ") and MessageSort = 0");
                    sqlcon.sqlUpdate("UPDATE messages SET MessageReaded = 1 WHERE MessageID = "
                            + "(SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " and ReciverID = " + FriendID + " "
                            + ") and MessageSort = 1");
                case "CPS":
                    String oldPass = "";
                    String newPass = "";
                    int j1 = 0, j2 = 1;
                    while (true) {
                        if (command.substring(j1, j2).equals("l")) {
                            oldPass = command.substring(j2, Integer.parseInt(command.substring(j1, j2)));
                            newPass = command.substring(Integer.parseInt(command.substring(j1, j2)));
                            break;
                        }
                        j1++;
                        j2++;
                    }
                    if (sqlcon.sqlSelectBoolean("SELECT * FROM users WHERE UserID = " + onlines.get(netconn) + " "
                            + " and UserPassword = " + oldPass + " ")) {
                        sqlcon.sqlUpdate("UPDATE users SET UserPassword = " + newPass + " ");
                    } else {
                        netconn.sendToClient("MESGPASS");
                    }
            }
        } catch (SQLException ex) {
        } catch (IOException ex) {
        }
    }

    @Override
    public synchronized void onGiveMessageList(NetConnect netconn, String listName) {
        try {
            String id = listName.substring(2);
            String FilesPath = "", sendStr = "";
            String messageIDSender = sqlcon.sqlSelectAll("MessageID", "messageids", "SenderID", "" + onlines.get(netconn), "ReciverID", id);
            ResultSet messageSenderList = sqlcon.getResultSet("SELECT * FROM messages WHERE MessageID = '" + messageIDSender + "'");
            messageSenderList.last();
            int size = messageSenderList.getRow();
            netconn.sendToClient("MSLISIZE" + ((size * 8) + 1));
            messageSenderList.beforeFirst();
            int a1 = 0, a6 = 0, a2 = 0, a3 = 0, a4 = 0, a5 = 0, a7 = 0, a8 = 0, a3213 = -1;
            if (size != 0) {
                netconn.sendToClient("MSLISIZE" + size);
                while (messageSenderList.next()) {
                    netconn.sendToClient("MSLIMSID" + ++a1 + "N" + messageSenderList.getInt("MsgID"));
                    netconn.sendToClient("MSLIDATE" + ++a3 + "N" + messageSenderList.getString("MessageDate"));
                    netconn.sendToClient("MSLISORT" + ++a4 + "N" + messageSenderList.getInt("MessageSort"));
                    netconn.sendToClient("MSLIREAD" + ++a6 + "N" + messageSenderList.getInt("MessageReaded"));
                    netconn.sendToClient("MSLIMESG" + ++a2 + "N" + messageSenderList.getString("MessageText"));
                    FilesPath = messageSenderList.getString("ImagePath");
                    sendStr = (FilesPath.equals("empty")) ? "empty"
                            : Arrays.toString(java.nio.file.Files.readAllBytes(new File(FilesPath).toPath()));
                    netconn.sendToClient("MSLIIMAG" + ++a5 + "N" + FilesPath.substring(FilesPath.length() - 3, FilesPath.length()) + "EXTE" + sendStr);
                    FilesPath = messageSenderList.getString("AudioPath");
                    sendStr = FilesPath.equals("empty") ? "empty"
                            : Arrays.toString(java.nio.file.Files.readAllBytes(new File(FilesPath).toPath()));
                    netconn.sendToClient("MSLIAUDI" + ++a7 + "N" + FilesPath.substring(FilesPath.length() - 3, FilesPath.length()) + "EXTE" + sendStr);
                    FilesPath = messageSenderList.getString("FilePath");
                    sendStr = FilesPath.equals("empty") ? "empty"
                            : Arrays.toString(java.nio.file.Files.readAllBytes(new File(FilesPath).toPath()));
                    netconn.sendToClient("MSLIFILE" + ++a8 + "N" + FilesPath.substring(FilesPath.length() - 3, FilesPath.length()) + "EXTE" + sendStr);
                }
            }
        } catch (SQLException ex) {
        } catch (IOException e) {
        }
    }

    public byte[] convertBytesToString(String convert) {
        byte[] allBytes = new byte[convert.length()];
        int abc = 1, whenStart = 0, change = 0, arrayLenght = 0, j = 1;
        String[] peregonBytov;
        byte[] byteFile1;
        while (true) {
            if ((MAX_SIZE * j) > convert.length()) {
                break;
            }
            j++;
        }
        while (abc <= j) {
            if (j < 2) {
                peregonBytov = convert.substring(1, convert.length() - 1).split(",");
                byteFile1 = new byte[peregonBytov.length];
                for (int i = 0; i < byteFile1.length; i++) {
                    byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                }
                System.arraycopy(byteFile1, 0, allBytes, 0, byteFile1.length);
                whenStart += byteFile1.length;
                break;
            } else {
                if (abc == 1) {
                    while (true) {
                        if (convert.substring(change + MAX_SIZE, change + MAX_SIZE + 1).equalsIgnoreCase(",")) {
                            peregonBytov = convert.substring(1, change + MAX_SIZE).split(",");
                            break;
                        }
                        change++;
                    }
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }
                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    arrayLenght = MAX_SIZE + change + 1;
                    whenStart += byteFile1.length;
                    abc++;
                } else if (abc == j) {
                    peregonBytov = convert.substring(arrayLenght, convert.length() - 1).split(",");
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }
                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    whenStart += byteFile1.length;
                    abc++;
                } else {
                    change = 0;
                    while (true) {
                        if (convert.substring(arrayLenght + MAX_SIZE + change, change + MAX_SIZE + arrayLenght + 1).equalsIgnoreCase(",")) {
                            peregonBytov = convert.substring(arrayLenght, change + arrayLenght + MAX_SIZE).split(",");
                            break;
                        }
                        change++;
                    }
                    byteFile1 = new byte[peregonBytov.length];
                    for (int i = 0; i < byteFile1.length; i++) {
                        byteFile1[i] = Byte.parseByte(peregonBytov[i].trim());
                    }
                    System.arraycopy(byteFile1, 0, allBytes, whenStart, byteFile1.length);
                    whenStart += byteFile1.length;
                    arrayLenght = change + arrayLenght + MAX_SIZE + 1;
                    abc++;
                }
            }
        }
        byte[] bytesDestiny = new byte[whenStart];
        System.arraycopy(allBytes, 0, bytesDestiny, 0, whenStart);
        return bytesDestiny;
    }

    public int getNumber(String clear) {
        int a1 = 0, b1 = 2, a = 0;
        while (true) {
            if (clear.substring(a1, b1).equalsIgnoreCase("id")) {
                a = Integer.parseInt(clear.substring(0, a1));
                break;
            }
            a1++;
            b1++;
        }
        return a;
    }

    @Override
    public void infoDelete(NetConnect netconn, String delete) {
        String triger = delete.substring(0, 4);
        String clear = delete.substring(4);
        int UserID1 = 0;
        switch (triger) {
            case "MYMS":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("DELETE FROM messages WHERE MsgID = " + UserID1);
                break;
            case "BOMS":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MsgID in (" + UserID1 + ", " + (1 + UserID1) + ");");
                break;
            case "MYLS":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " AND ReciverID = " + UserID1 + ")"
                        + " AND MessageSort = 0;'");
                break;
            case "BOLS":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " AND ReciverID = " + UserID1 + " )"
                        + "AND MessageSort = 0;");
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + UserID1 + " AND ReciverID = " + onlines.get(netconn) + ")"
                        + "AND MessageSort = 1;");
                break;
            case "MYFR":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("delete from userfriends\n"
                        + "where FriendsIDs = (SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + " ) and UserID = " + UserID1 + ";");
                sqlcon.sqlUpdate("UPDATE userfriends SET FriendAdded = 3 WHERE FriendsIDs = "
                        + "(SELECT UserFriends FROM users WHERE UserID = " + UserID1 + " ) and UserID = " + onlines.get(netconn) + " ");
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " AND ReciverID = " + UserID1 + " )"
                        + "AND MessageSort = 1;");
                sqlcon.sqlUpdate("delete from messageids WHERE SenderID = " + onlines.get(netconn) + " and ReciverID = " + UserID1 + " ");
                sqlcon.sqlUpdate("delete from messageids WHERE SenderID = " + UserID1 + " and ReciverID = " + onlines.get(netconn) + " ");
                break;
            case "MFBO":
                UserID1 = getNumber(clear);
                sqlcon.sqlUpdate("delete from userfriends\n"
                        + "where FriendsIDs = (SELECT UserFriends FROM users WHERE UserID = " + onlines.get(netconn) + " ) and UserID = " + UserID1 + ";");
                sqlcon.sqlUpdate("delete from userfriends\n"
                        + "where FriendsIDs = (SELECT UserFriends FROM users WHERE UserID = " + UserID1 + " ) and UserID = " + onlines.get(netconn) + ";");
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " AND ReciverID = " + UserID1 + " )"
                        + "AND MessageSort = 0;");
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + UserID1 + " AND ReciverID = " + onlines.get(netconn) + ")"
                        + "AND MessageSort = 1;");
                sqlcon.sqlUpdate("delete from messages\n"
                        + "where MessageID in (SELECT MessageID FROM messageids WHERE SenderID = " + onlines.get(netconn) + " AND ReciverID = " + UserID1 + " )"
                        + "AND MessageSort = 1;");
                sqlcon.sqlUpdate("delete from messageids WHERE SenderID = " + onlines.get(netconn) + " and ReciverID = " + UserID1 + " ");
                sqlcon.sqlUpdate("delete from messageids WHERE SenderID = " + UserID1 + " and ReciverID = " + onlines.get(netconn) + " ");
                break;
            default:
        }
    }

    @Override
    public void giveTools(NetConnect netcon, String tools) {
        int Toolscounter = 0, tableSize;
        try {
            ResultSet rs = sqlcon.getResultSet("SELECT * FROM tools");
            rs.last();
            tableSize = rs.getRow();
            netcon.sendToUser("TOOLSIZE" + (tableSize));
            rs.beforeFirst();
            while (rs.next()) {
                URL url = getClass().getResource(rs.getString("ToolsPath"));
                File f = new File(url.toString().substring(6));
                String plus = "TOOLTOOL" + ++Toolscounter + "N" + Arrays.toString(
                        java.nio.file.Files.readAllBytes(f.toPath()));
                netcon.sendToUser(plus);
            }
        } catch (IOException e) {
        } catch (SQLException sqex) {
        }
    }
}
