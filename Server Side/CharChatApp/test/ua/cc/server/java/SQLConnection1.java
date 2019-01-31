package ua.cc.server.java;

import java.sql.*;
import java.util.Properties;

public class SQLConnection1 {
    
    private static final String DBLogin = "root";
    private static final String DBUrl = "jdbc:mysql://localhost:3306/charchatdb";
    private static final String DBPass = "aligment%1945";
   
    private Connection conn;
    private PreparedStatement ps;
    private Statement st;
    private ServerPart server;
    private ResultSet result;
    private Properties props = new Properties(); 
    
    SQLConnection1(){
        try {
            
            props.put("user", "root");         
            props.put("password", "aligment%1945");
            props.put("useUnicode", "true");
            props.put("useServerPrepStmts", "false");
            props.put("characterEncoding", "UTF-8"); 
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
        } catch(
                ClassNotFoundException | IllegalAccessException | InstantiationException ex){
            System.out.println("Driver creation error"); 
        } 
        try {
            
            conn = DriverManager.getConnection(DBUrl,DBLogin,DBPass);
            System.out.println("Database connected");  
  
      }catch(SQLException e){
          System.out.println("Database error");
      }
    }
    
    public synchronized void sqlUpdate(String SQLUpdate) throws SQLException {
            st = conn.createStatement();
            st.executeLargeUpdate(SQLUpdate);
            st.close();
    }
    
    public synchronized void sqlInsert(String SQLInsert)throws SQLException {
            st = conn.createStatement();
            st.execute(SQLInsert);
            st.close();
    }
    
    public synchronized void sqlSelect(String SQLSelect) throws SQLException {
            st = conn.createStatement();
            result = st.executeQuery(SQLSelect);
            String UserIP = "";
            while(result.next())
            UserIP += result.getString("UserIPAddress");
            if(result.first()){
              server.sendToUser("true", UserIP); 
            } 
            else {
              server.sendToUser("false", UserIP);
            }
             st.close();
    }
    
    public synchronized String getIPAddress(String UserID) throws SQLException{
        String select = "SELECT UserIPAddress FROM users WHERE UserID = ?";
        ps = conn.prepareStatement(select);
        ps.setInt(1, Integer.parseInt(UserID));
        result = ps.executeQuery();
        return result.toString();
    }
    
//    public static void main(String args[]) {
//        new SQLConnection1();
//    }
}
