package de.messdiener.cms.app.services.sql;

import de.messdiener.cms.app.settings.DatabaseConfiguration;
import de.messdiener.cms.cache.Cache;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseService {

    private final static Logger LOGGER = Logger.getLogger("Manager.DatabaseService");
    private final static String CONNECTION_URL = "jdbc:mysql://" + DatabaseConfiguration.HOST + ":" + DatabaseConfiguration.PORT
            + "/" + DatabaseConfiguration.DATABASE;
    private Connection connection;

    public DatabaseService() {

        LOGGER.setLevel(Level.ALL);



        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Verbindung wird hergestellt!");
            this.connection = DriverManager.getConnection(CONNECTION_URL, DatabaseConfiguration.USER, DatabaseConfiguration.PASSWORD);

            LOGGER.finest("SQL-Verbindung aufgebaut!");

            Cache.updateRefresh();

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.severe("Fehler beim Aufbauen der SQL Verbindung");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void reconnect() {

        Cache.updateRefresh();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Verbindung wird hergestellt!");
            this.connection = DriverManager.getConnection(CONNECTION_URL, DatabaseConfiguration.USER, DatabaseConfiguration.PASSWORD);
            LOGGER.finest("SQL-Verbindung aufgebaut!");
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.severe("Fehler beim Aufbauen der SQL Verbindung");
            e.printStackTrace();
        }
    }

    public boolean exists(String module, String identifierAttribute, String identifier) {
        try {
            return getConnection().prepareStatement("SELECT " + identifierAttribute + " FROM  " + module + " WHERE " + identifierAttribute + "='" + identifier + "'").executeQuery().next();
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
            return false;
        }
    }

    public void delete(String module, String identifierAttribute, String identifier)  {

        if(!exists(module, identifierAttribute, identifier)){
            return;
        }

        try {
            getConnection().prepareStatement("DELETE FROM "+ module + " WHERE " + identifierAttribute + "='" + identifier + "'").executeUpdate();
            LOGGER.finest("Entry [" + identifier + "] erfolgreich gelöscht.");
        } catch (SQLException e) {
            LOGGER.severe("Fehler! ");
            e.printStackTrace();
        }
    }

}
