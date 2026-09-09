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
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;

public class RepositorioConexion extends Conexion {

    @SuppressWarnings("unchecked")
    private final Hashtable conexiones;

    @SuppressWarnings("unchecked")
    public RepositorioConexion() throws Exception{ 
        super();
        conexiones = new Hashtable();
    }

    public boolean existeConexion(String p_idConexion){ 
        return conexiones.containsKey(p_idConexion);
    }	

    public Connection getConexion(String p_idConexion){ 
        return (Connection)conexiones.get(p_idConexion);
    }

    @SuppressWarnings("unchecked")
    public void insertarConexion(String p_idConexion, Connection p_conn){
        conexiones.put(p_idConexion, p_conn);
    }

    @SuppressWarnings("unchecked")
    public void insertarConexion(){
        long p_idConexion = getSID();
        conexiones.put(String.valueOf(p_idConexion), m_conn);
    }

    public void eliminarConexion(String p_idConexion) throws SQLException{ 
        Connection conn=(Connection)conexiones.remove(p_idConexion); 
        conn.close();
    }

    @SuppressWarnings("unchecked")
    public void eliminarConexiones() throws SQLException{ 
        Enumeration enumConexiones=conexiones.elements();
        while(enumConexiones.hasMoreElements()){ 
            Connection conn=(Connection)enumConexiones.nextElement();
            conn.close();
        }
        conexiones.clear();
    }

    public Connection generarConexion(String p_user, String p_password) throws SQLException{
        String user = p_user;
        String password = p_password;
        getConexion(user, password);
        return m_conn;
    }

    /*
    public Connection generarConexion(String p_user, String p_password) throws SQLException{
            String user = new String(p_user);
            String password = new String(p_password);
            //Conexion conexion=new Conexion_v1(user, password);
            getConexion(user, password);
            return m_conn;
    }
    */

    //solo generara una nueva conexion si el id indicado no exista ya
    public Connection generarConexion(String p_idConexion, String p_user, String p_password) throws SQLException{
        if(!existeConexion(p_idConexion)){
            getConexion(p_user, p_password);
            insertarConexion(p_idConexion, m_conn);
        }
        return m_conn;
    }

    //metodos para administracion del sistema
    @SuppressWarnings("unchecked")
    public Hashtable getConexiones(){
        return conexiones;
    }

    @Override
    public void close(){
        try {
            eliminarConexiones();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.close();
    }

}

