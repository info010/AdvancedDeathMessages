package org.info_0.advanceddeathmessages.util;

import org.bukkit.entity.Player;

import java.sql.*;

public class Database {

    private final Connection connection;

    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:"+path);
        try (Statement statement = connection.createStatement()){
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS players (" +
                            "uuid TEXT PRIMARY KEY, " +
                            "username TEXT NOT NULL, " +
                            "message TEXT NOT NULL)"
            );
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")){
            statement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void addPlayer(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO players (uuid, username, message) VALUES (?, ?, ?)")){
            statement.setString(1,player.getUniqueId().toString());
            statement.setString(2,player.getDisplayName());
            statement.setString(3,"Default");
            statement.executeUpdate();
        }
    }

    public void updatePlayerDeathMessage(Player player,String message) throws SQLException {
        if(!playerExists(player)) addPlayer(player);
        try (PreparedStatement statement = connection.prepareStatement("UPDATE players SET message = ? WHERE uuid = ?")){
            statement.setString(1,message);
            statement.setString(2,player.getUniqueId().toString());
            statement.executeUpdate();
        }
    }

    public String getDeathMessage(Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT message FROM players WHERE uuid = ?")){
            statement.setString(1,player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("message");
            }else {
                return "Default";
            }
        }
    }
}
