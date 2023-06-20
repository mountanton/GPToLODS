
/*  This code belongs to the Semantic Access and Retrieval (SAR) group of the
 *  Information Systems Laboratory (ISL) of the
 *  Institute of Computer Science (ICS) of the
 *  Foundation for Research and Technology - Hellas (FORTH)
 *  Nobody is allowed to use, copy, distribute, or modify this work.
 *  It is published for reasons of research results reproducibility.
 *  (c) 2020 Semantic Access and Retrieval group, All rights reserved
 */
package gr.forth.ics.isl.demoExternal.core;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 *
 * @author Mike Mountantonakis
 */
public class ConnectToVirtuoso {

	private String virtuosoHost;
	private String virtuosoPort;
	private String virtuosoUserName;
	private String virtuosoPassword;
	/**
	 *
	 */
	protected static Connection connec;
	private String cleargraphFile;
	protected static VirtuosoRepository repository;
	protected static RepositoryConnection conn;

	/**
	 * Constructor Initializes the variables which are necessary for connecting
	 * (host,port,username,password)
	 */
	public ConnectToVirtuoso() {
               

                this.virtuosoHost = "83.212.97.78";
		this.virtuosoPort = "1111";
		this.virtuosoUserName = "dba";
		this.virtuosoPassword = "nC9s"; 

	}

        private void initializeVirtuosoProperties(String virtuosoProperties){
             try {
                BufferedReader br=new BufferedReader(new FileReader(virtuosoProperties));
                String s="";
                while((s=br.readLine())!=null){
                    String[] split=s.split(":");
                    if(split[0].equals("virtuosoHost")){
                        this.virtuosoHost=split[1];
                    }
                    else if(split[0].equals("virtuosoPort")){
                        this.virtuosoPort=split[1];
                    }
                    else if(split[0].equals("virtuosoUserName")){
                        this.virtuosoUserName=split[1];
                    }
                    else if(split[0].equals("virtuosoPassword")){
                        this.virtuosoPassword=split[1];
                    }
                    
                }
                br.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ConnectToVirtuoso.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ConnectToVirtuoso.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        
	/**
	 * This method starts the connection to the virtuoso repository (openrdf)
	 *
	 * @throws RepositoryException
	 */
	public void startConnection() throws RepositoryException {

		ConnectToVirtuoso.repository = new VirtuosoRepository("jdbc:virtuoso://"
				+ this.virtuosoHost + ":" + this.virtuosoPort
				+ "/charset=UTF-8/log_enable=2",
				this.virtuosoUserName, this.virtuosoPassword);
		ConnectToVirtuoso.conn = ConnectToVirtuoso.repository.getConnection();
	}

	/**
	 * This method starts the connection to virtuoso repository (jdbc3.Driver)
	 *
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void startJDBC_Connection() throws ClassNotFoundException, SQLException {
		Class.forName("virtuoso.jdbc4.Driver");

		ConnectToVirtuoso.connec = DriverManager.getConnection("jdbc:virtuoso://" + this.virtuosoHost + ":"
				+ this.virtuosoPort + "", this.virtuosoUserName, this.virtuosoPassword);

	}

	/**
	 * Returns JDBC Connection
	 *
	 * @return
	 */
	public static Connection getJDBC_Connection() {
		return connec;
	}

	/**
	 * Terminates JDBC Connection
	 *
	 * @throws SQLException
	 */
	public void terminateJDBC_Connection() throws SQLException {
		if (ConnectToVirtuoso.connec != null) {
			ConnectToVirtuoso.connec.close();
		}
	}

	/**
	 * Returns OpenRDF Connection
	 *
	 * @return openRDF Connection
	 */
	public static RepositoryConnection getConnection() {
		return ConnectToVirtuoso.conn;
	}

	/**
	 * Returns the virtuoso Repository (openrdf)
	 *
	 * @return the virtuoso Repository
	 */
	public static VirtuosoRepository getRepository() {
		return ConnectToVirtuoso.repository;
	}

	/**
	 * Terminates openRDF Connection
	 *
	 * @throws WarehouseControllerException
	 */
	public void terminateConnection() throws Exception {
		if (ConnectToVirtuoso.conn != null) {
			try {
				ConnectToVirtuoso.conn.close();
			} catch (RepositoryException ex) {
				throw new Exception("An error occured while closing the connection with Virtuoso Knowledge Base+n" + ex.getMessage());
			}
		}

	}
}
