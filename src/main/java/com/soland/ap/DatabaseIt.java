package com.soland.ap;

import com.soland.ap.game.model.Board;
import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.Server;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DatabaseIt {

    private static final String DB_URL = "jdbc:sqlite:sqlite.db";
    private static final String GAME_TABLE = "game_table";
    private static final String CREATE_GAME_TABLE = "CREATE TABLE IF NOT EXISTS " + GAME_TABLE
            + "(id INTEGER, players TEXT,positions TEXT)";

    private Connection conn;
    public DatabaseIt() throws SQLException {
        conn = connect();
        setUp();
    }

    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void setUp() throws SQLException {
        if (conn != null) {
            conn.createStatement().execute(CREATE_GAME_TABLE);
        }
    }

    public GameModel loadGame(Server server, int id) {
        String query = "SELECT * FROM " + GAME_TABLE + " WHERE id = " + id;
        try {
            Statement st = conn.createStatement();
            ResultSet results = st.executeQuery(query);
            GameModel game = null;
            results.next();
            while (!results.isAfterLast()) {
                String[] playersName = results.getString(2).split(",");
                PlayerModel player = PlayerModel.factory(server.getPlayer(playersName[0]));
                PlayerModel player2 = new PlayerModel(playersName[1]);
                String[] positions = results.getString(3).split(",");
                System.out.println(Arrays.toString(positions));
                List<String> p1Positions = Arrays.stream(positions).filter(n -> n.charAt(0) == '1').collect(Collectors.toList());
                List<String> p2Positions = Arrays.stream(positions).filter(n -> n.charAt(0) == '2').collect(Collectors.toList());
                game = new GameModel(server, player, player2);
                game.setBoard(p1Positions, p2Positions);
                results.next();
            }
            if (game != null) {
                game.setFileName(String.valueOf(id));
                game.getBoard().play(null);
                return game;
            }
        } catch (SQLException e) {
            try {
                conn.close();
                conn = connect();
                System.out.println(e.toString());
            } catch (SQLException ex) {}
        }
        return null;
    }

    public int saveGame(GameModel game) {
        int randomName = new Random().nextInt(10000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(game.getFileName() == null) {
                        PlayerModel[] players = game.getBoard().getPlayers();
                        String playerNames = players[0].getName() + "," + players[1].getName();
                        String query = "INSERT INTO " + GAME_TABLE + "(id,players,positions) VALUES("
                                + randomName + ",'" +
                                playerNames + "','" + game.getBoardStructure() + "')";
                        Statement st = conn.createStatement();
                        st.execute(query);
                    }else {
                        String query = "UPDATE TABLE " + GAME_TABLE + " SET positions = "+"'" + game.getBoardStructure() + "' WHERE id = "+game.getFileName();
                        Statement st = conn.createStatement();
                        st.execute(query);
                    }
                } catch (SQLException e) {
                    try {
                        conn.close();
                        conn = connect();
                    } catch (SQLException ex) {}
                    System.out.println(e.toString());
                }

            }
        }).start();
        return (game.getFileName() != null)? Integer.parseInt(game.getFileName()) :randomName;
    }

}
