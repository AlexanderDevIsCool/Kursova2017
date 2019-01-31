package ua.cc.server.java;

import java.sql.*;
import java.util.Properties;

public class SQLConnection1 {

    private static final String DBLogin = "root";
    private static final String DBUrl = "jdbc:mysql://localhost:3306/charchatdb";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DBPass = "aligment%1945";
    private final String dbname = "charchatdb";
    private boolean exist = false;
    private Connection conn;
    private PreparedStatement ps;
    private Statement st;
    private ServerPart server = null;
    private ResultSet result = null;
    private Properties props = new Properties();
    public int id;

    private final String tableUsers
            = "CREATE TABLE `users` (\n"
            + "  `UserID` int(11) NOT NULL AUTO_INCREMENT,\n"
            + "  `UserName` varchar(30) NOT NULL,\n"
            + "  `UserMail` varchar(50) NOT NULL,\n"
            + "  `UserPassword` varchar(30) NOT NULL,\n"
            + "  `UserCountry` varchar(30) DEFAULT NULL,\n"
            + "  `UserGender` varchar(10) DEFAULT NULL,\n"
            + "  `UserIPAddress` varchar(30) NOT NULL,\n"
            + "  `UserPhoto` varchar(100) DEFAULT 'C:\\\\Users\\\\Admin\\\\CharChatResources\\\\Pictures\\\\defaulticon.png',\n"
            + "  `UserFriends` int(11) DEFAULT NULL,\n"
            + "  `UserBirthday` varchar(30) DEFAULT NULL,\n"
            + "  `UserOnline` varchar(20) DEFAULT 'offline',\n"
            + "  `UserLastName` varchar(40) DEFAULT 'empty',\n"
            + "  `UserInformation` text,\n"
            + "  `UserPath` varchar(100) NOT NULL DEFAULT 'C:\\\\Users\\\\Admin\\\\CharChatResources\\\\Pictures',\n"
            + "  PRIMARY KEY (`UserID`),\n"
            + "  UNIQUE KEY `UserMail` (`UserMail`),\n"
            + "  UNIQUE KEY `UserFriends` (`UserFriends`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8";

    private final String tableUserfriends
            = "CREATE TABLE `userfriends` (\n"
            + "  `UserFriend` int(11) NOT NULL AUTO_INCREMENT,\n"
            + "  `FriendsIDs` int(11) NOT NULL,\n"
            + "  `UserID` int(11) DEFAULT NULL,\n"
            + "  `FriendAdded` int(11) NOT NULL DEFAULT '0',\n"
            + "  PRIMARY KEY (`UserFriend`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8";

    private final String tableTools
            = "CREATE TABLE `tools` (\n"
            + "  `ToolsPath` varchar(100) DEFAULT NULL\n"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

    private final String talbemessages
            = "CREATE TABLE `messages` (\n"
            + "  `MsgID` int(11) NOT NULL AUTO_INCREMENT,\n"
            + "  `MessageID` int(255) NOT NULL,\n"
            + "  `MessageDate` varchar(30) DEFAULT NULL,\n"
            + "  `MessageText` varchar(255) DEFAULT 'empty',\n"
            + "  `ImagePath` varchar(250) DEFAULT 'empty',\n"
            + "  `MessageSort` varchar(1) DEFAULT NULL,\n"
            + "  `MessageReaded` varchar(1) DEFAULT '0',\n"
            + "  `AudioPath` varchar(100) DEFAULT 'empty',\n"
            + "  `FilePath` varchar(100) DEFAULT 'empty',\n"
            + "  PRIMARY KEY (`MsgID`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8";

    private final String tablemessageids
            = "CREATE TABLE `messageids` (\n"
            + "  `MessageID` int(11) NOT NULL AUTO_INCREMENT,\n"
            + "  `SenderID` int(100) NOT NULL,\n"
            + "  `ReciverID` int(100) NOT NULL,\n"
            + "  PRIMARY KEY (`MessageID`),\n"
            + "  KEY `SenderID` (`SenderID`),\n"
            + "  CONSTRAINT `messageids_ibfk_1` FOREIGN KEY (`SenderID`) REFERENCES `users` (`UserID`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8";

    private final String friendsids
            = "CREATE TABLE `friendsids` (\n"
            + "  `FriendsID` int(11) NOT NULL AUTO_INCREMENT,\n"
            + "  `UserID` int(11) NOT NULL,\n"
            + "  PRIMARY KEY (`FriendsID`),\n"
            + "  UNIQUE KEY `UserID` (`UserID`)\n"
            + ") ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8";

    SQLConnection1() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(URL, DBLogin, DBPass);
            ResultSet rs = conn.getMetaData().getCatalogs();
            while (rs.next()) {
                if (rs.getString(1).equals(dbname)) {
                    exist = true;
                }
            }

            if (!exist) {
                st = conn.createStatement();
                st.execute("CREATE DATABASE " + dbname);
                conn.close();
                st.close();
                props.put("user", "root");
                props.put("password", "aligment%1945");
                props.put("useUnicode", "true");
                props.put("useServerPrepStmts", "true");
                props.put("characterEncoding", "UTF-8");
                conn = conn = DriverManager.getConnection(DBUrl, DBLogin, DBPass);
                st = conn.createStatement();
                st.execute(tableUsers);
                st.execute(tableUserfriends);
                st.execute(tableTools);
                st.execute(talbemessages);
                st.execute(tablemessageids);
                st.execute(friendsids);
                st.execute("INSERT INTO tools values('/res/defaultaudio.png')");
                st.execute("INSERT INTO tools values('/res/add.png')");
                st.execute("INSERT INTO tools values('/res/defaultfile.png')");
                st.execute("INSERT INTO tools values('/res/defaulticon.png')");
                st.execute("INSERT INTO tools values('/res/more.png')");
                st.execute("INSERT INTO tools values('/res/fon1.png')");
                st.execute("INSERT INTO tools values('/res/question.png')");
                st.execute("INSERT INTO tools values('/res/defaultnotification.mp3')");
                st.execute("INSERT INTO tools values('/res/defaultlogo.jpg')");

            } else {
                props.put("user", "root");
                props.put("password", "aligment%1945");
                props.put("useUnicode", "true");
                props.put("useServerPrepStmts", "true");
                props.put("characterEncoding", "UTF-8");
                conn = DriverManager.getConnection(DBUrl, DBLogin, DBPass);
                System.out.println("Database connected");
            }

        } catch (ClassNotFoundException | IllegalAccessException | SQLException | InstantiationException ex) {
            System.out.println("Driver creation error");
        }
    }

    public void sqlUpdate(String SQLUpdate) {
        try {
            st = conn.createStatement();
            st.executeLargeUpdate(SQLUpdate);
        } catch (SQLException ex) {
            System.out.println("Update falil: " + SQLUpdate);
        }
    }

    public String sqlInsert(String SQLInsert) {
        try {
            st = conn.createStatement();
            st.execute(SQLInsert);
            return "Inserttrue";
        } catch (SQLException e) {
            return "Insertfalse";
        }

    }

    public String sqlSelectAll(String what, String from) throws SQLException {
        st = conn.createStatement();
        String select = "SELECT " + what + " FROM " + from;
        System.out.println("sqlSelectAll select: " + select);
        int resultstr;
        result = st.executeQuery(select);
        if (result.next()) {
            resultstr = result.getInt(what);
            System.out.println("resutstr: " + resultstr);
            return "" + resultstr;
        }
        return "fail";
    }

    public String sqlSelectAll(String what, String from, String where, String value) throws SQLException {
        st = conn.createStatement();
        String select = "SELECT " + what + " FROM " + from + " WHERE " + where + "='" + value + "'";
        System.out.println("sqlSelectAll select: " + select);
        int resultstr;
        result = st.executeQuery(select);
        if (result.next()) {
            resultstr = result.getInt(what);
            System.out.println("resutstr: " + resultstr);
            return "" + resultstr;
        }
        return "fail";
    }

    public String sqlSelectAll(String what, String from, String where, String value, String where2, String value2) throws SQLException {
        st = conn.createStatement();
        String select = "SELECT " + what + " FROM " + from + " WHERE " + where + "='" + value + "' and " + where2 + "='" + value2 + "'";
        System.out.println("sqlSelectAll select: " + select);
        int resultstr;
        result = st.executeQuery(select);
        if (result.next()) {
            resultstr = result.getInt(what);
            System.out.println("resutstr: " + resultstr);
            return "" + resultstr;
        }
        return "fail";
    }

    public String sqlSelect(String SQLSelect) throws SQLException {
        st = conn.createStatement();
        result = st.executeQuery(SQLSelect);
        if (result.next()) {
            id = result.getInt("UserID");
            sqlUpdate("UPDATE users SET UserIPAddress = '" + getIPAddress(id) + "' WHERE UserID = " + id);
            return "true";
        } else {
            return "false";
        }
    }

    public boolean sqlSelectBoolean(String sql) throws SQLException {
        st = conn.createStatement();
        result = st.executeQuery(sql);
        if (result.next()) {
            return true;
        } else {
            return false;
        }
    }

    public String getIPAddress(int UserID) throws SQLException {
        String select = "SELECT UserIPAddress FROM users WHERE UserID = " + UserID;
        st = conn.createStatement();
        result = st.executeQuery(select);
        String ipadd = "";
        while (result.next()) {
            ipadd = result.getString("UserIPAddress");
        }
        System.out.println(ipadd);
        return ipadd;
    }

    public ResultSet getResultSet(String sql) throws SQLException {
        st = conn.createStatement();
        result = st.executeQuery(sql);
        return result;
    }
//    
//    public static void main(String args[]) {
//        new SQLConnection1();
//    }
}
