package de.creelone.dismine;

import java.sql.*;

public class MySQL {

	public String host; // localhost likely
	public String port; // 3306 likely
	public String database;
	public String username; // root
	public String password; // ""
	public Connection con;

	public MySQL(String host, int port, String database, String username, String password) {
		this.host = host;
		this.port = port + "";
		this.database = database;
		this.username = username;
		this.password = password; // Stored very securely xD
	}

	public void connect() {
		if(!isConnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				System.out.println("[MySQL] Connected!");
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}

	public void disconnect() {
		if(isConnected()) {
			try {
				con.close();
				System.out.println("[MySQL] Disconnected!");
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		}
	}

	public boolean isConnected() {
		return (con == null ? false : true);
	}

	public void update(String qry) {
		try {
			PreparedStatement ps = con.prepareStatement(qry);
			ps.executeUpdate();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public PreparedStatement prepare(String qry) {
		try {
			return con.prepareStatement(qry);
		} catch (SQLException e) {
			return null;
		}
	}

	public ResultSet getResult(String qry) {
		try {
			PreparedStatement ps = con.prepareStatement(qry);
			return ps.executeQuery();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		return null;
	}

}
