/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atm.gob.ec.conexion;

/**
 *
 * @author erik.flores
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;

import atm.gob.ec.utils.Utils;
import java.sql.Statement;
import java.util.Properties;

class Conexion {
	
    private String user = new String();//
    private String passw = new String();//
    private String URL = new String();//
    private String DRIVER = new String();//
    private String DATABASE = new String();//
    private String DB_SERVER = new String();
    private String DB_PORT = new String();
    protected String strSentencia = new String();
    protected String strSentencia2 = new String();
    protected String strSentencia3 = new String();
    protected int results = 0;
    protected Connection m_conn = null;
    protected Connection m_conn2 = null;
    protected Connection m_conn3 = null;
    protected PreparedStatement pstmt = null;
    protected PreparedStatement pstmt2 = null;
    protected PreparedStatement pstmt3 = null;
    protected CallableStatement cstmt = null;
    protected CallableStatement cstmt2 = null;
    protected CallableStatement cstmt3 = null;
    protected Statement stmt = null;
    protected Statement stmt2 = null;
    protected Statement stmt3 = null;
    protected ResultSet rst = null;
    protected ResultSet rst2 = null;
    protected ResultSet rst3 = null;
    protected long SID = 0;

    private Properties props = new Properties();
		
    public Conexion() throws SQLException{

        props = Utils.getProperties();
        
    }

    public Conexion(String param1, String param2) throws SQLException{

        props = Utils.getProperties();

        setUser(param1);
        setPasswd(param2);

        //Armar la URL
        if (getDriver().toLowerCase().matches("(.*)mysql(.*)"))
            setURL(props.getProperty("DB.MYSQLURL")+"://"+getServidor()+":"+getPuerto()+"/"+getDB());

        if (getDriver().toLowerCase().matches("(.*)oracle(.*)"))
            setURL(props.getProperty("DB.ORACLEURL")+":@"+getServidor()+":"+getPuerto()+":"+getDB());

        if (getDriver().toLowerCase().matches("(.*)sqlserver(.*)"))
            setURL(props.getProperty("DB.MSSQLURL")+"://"+getServidor()+":"+getPuerto());
    }
	
    public Conexion(String... params) throws SQLException{ 
        
        props = Utils.getProperties();        
        setDriver(params[0]);
        setDB(params[1]);
        setURL(params[2] + this.getDB());
        setUser(params[3]);
        setPasswd(params[4]);
    }
    
    private void setConexion(String url, String usuario, String clave )throws SQLException{
    	if (m_conn == null){
            if (getDriver().toLowerCase().matches("(.*)mysql(.*)")) 
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            m_conn = DriverManager.getConnection(url, usuario, clave);
    	}
        m_conn.setAutoCommit(false);
    }
    
    private Connection setConexion(Connection c, String driver, String url, String usuario, String clave )throws SQLException{
    	if (c == null){
            if (driver.toLowerCase().matches("(.*)mysql(.*)")) 
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            c = DriverManager.getConnection(url, usuario, clave);
    	}
        c.setAutoCommit(false);
        return c;
    }
    
    public Connection getConexion()throws SQLException{
    	setConexion(getURL(), getUser(), getPasswd());
        results = 0;
        return m_conn;
    }	
		
    public Connection getConexion(String u, String p)throws SQLException{
	setUser(u);
	setPasswd(p);
    	setConexion(getURL(), getUser(), getPasswd());
        results = 0;
        return m_conn;
    }	
		
    public Connection getConexion(String URL, String u, String p)throws SQLException{
	setUser(u);
	setPasswd(p);
        setURL(URL);
    	setConexion(getURL(), getUser(), getPasswd());
        results = 0;
        return m_conn;
    }	
		
    public Connection getConexion(String driver, String url, String u, String p)throws SQLException{
	setUser(u);
	setPasswd(p);
        setDriver(driver);
        setURL(url);
    	setConexion(getURL(), getUser(), getPasswd());
        results = 0;
        return m_conn;
    }	
		
    public Connection getConexion(Connection c, String driver, String url, String u, String p)throws SQLException{
	//setUser(u);
	//setPasswd(p);
        //setDriver(driver);
        //setURL(url);
    	c = setConexion(c, driver, url, u, p);
        results = 0;
        return c;
    }	
		
    public void closeConexion(){
        try{
            if (m_conn != null && !m_conn.isClosed())
                m_conn.close();
            m_conn = null;
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    protected PreparedStatement preparedStatement() throws SQLException{
        pstmt = m_conn.prepareStatement(strSentencia);
        return pstmt;
    }
	
    protected PreparedStatement preparedStatement(String param) throws SQLException{
        pstmt = m_conn.prepareStatement(param);
        return pstmt;
    }
	
    protected PreparedStatement preparedStatement(Connection c, PreparedStatement p, String param) throws SQLException{
        p = c.prepareStatement(param);
        return p;
    }
	
    protected Statement creaStatement() throws SQLException{
        stmt = m_conn.createStatement();
        return stmt;
    }
	
    protected Statement creaStatement(Connection c, Statement s) throws SQLException{
        s = c.createStatement();
        return s;
    }
    
    protected Statement executeUpdate(String param) throws SQLException{
        stmt.executeUpdate(param);
        return stmt;
    }
    
    protected int executeUpdate(Statement s, String param) throws SQLException{
        results = s.executeUpdate(param);
        return results;
    }
    
    protected CallableStatement prepareCall() throws SQLException{
        cstmt = m_conn.prepareCall(strSentencia);
        return cstmt;
    }
	
    protected CallableStatement prepareCall(String param) throws SQLException{
        cstmt = m_conn.prepareCall(param);
        return cstmt;
    }
	
    protected ResultSet executeQuery() throws SQLException{
        rst = pstmt.executeQuery();
        return rst;
    }
	
    protected ResultSet executeQuery(PreparedStatement p, ResultSet r) throws SQLException{
        r = p.executeQuery();
        return r;
    }
	
    protected int executeUpdate() throws SQLException{
        results = pstmt.executeUpdate();
        return results;
    }

    protected int executeUpdate(PreparedStatement p) throws SQLException{
        int results = p.executeUpdate();
        return results;
    }

    protected int executeUpdate(Statement s) throws SQLException{
        int results = s.executeUpdate(strSentencia);
        return results;
    }

    protected int executeCallUpdate() throws SQLException{
        results = cstmt.executeUpdate();
        return results;
    }

    protected void closeResultSet() {
        try{
            if (rst != null)
                rst.close();
            if (rst2 != null)
                rst2.close();
            if (rst3 != null)
                rst3.close();
            rst = null;
            rst2 = null;
            rst3 = null;
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected void closePreparedStatement() {		
        try{
            if (pstmt != null )
                    pstmt.close();
            pstmt = null;
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected void closeCallableStatement() {		
        try{
            if (cstmt != null )
                    cstmt.close();
            cstmt = null;
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected void closeStatement() {		
        try{
            if (stmt != null )
                    stmt.close();
            stmt = null;
        }catch(SQLException e){
             e.printStackTrace();
        }
    }

    public void commit() {
        try{
            if (m_conn != null && !m_conn.isClosed())
                m_conn.commit();			
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void rollback() {
        try{
            if (m_conn != null && !m_conn.isClosed())
                m_conn.rollback();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void close(){
        closeConexion();
    }

    private void setUser(String param){
        this.user = param;
    }

    public String getUser(){
        return this.user;
    }

    private void setPasswd(String param){
            this.passw = param;
    }

    public String getPasswd(){
            return this.passw;
    }

    private void setURL(String param){
            this.URL = param;
    }

    public String getURL(){
        return this.URL;
    }

    private void setDB(String param){
            this.DATABASE = param;
    }

    private String getDB(){
            return this.DATABASE;
    }

    private void setDriver(String param){
            this.DRIVER = param;
    }

    private String getDriver(){
            return this.DRIVER;
    }

    public long getSID(){
            try{
                    getConexion();
                    strSentencia = props.getProperty("DB.98");
                    preparedStatement();
                    executeQuery();
                    while(rst.next()){
                            SID = rst.getLong("S_ID");
                    }
            }catch(Exception e){
                    e.printStackTrace();
            }finally{
                    closeResultSet();
            }
            return SID;
    }

    private String getServidor() {
        //To change body of generated methods, choose Tools | Templates.
        return this.DB_SERVER;
    }

    private String getPuerto() {            
        //To change body of generated methods, choose Tools | Templates.
        return this.DB_PORT;
    }
    
    

}	



