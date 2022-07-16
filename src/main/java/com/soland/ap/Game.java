package com.soland.ap;

import com.soland.ap.game.Arena;
import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements GameInterface {

    private DatabaseIt databaseIt;
    private ArrayList<GameModel> requested;
    private ArrayList<GameModel> onGoing;
    private Server server;
    private Arena arena;

    public Game(Server server) {
        this.server = server;
        try {
            databaseIt = new DatabaseIt();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        requested = new ArrayList<>();
        onGoing = new ArrayList<>();
        buildArena();
    }

    @Override
    public boolean isPlaying(PlayerModel player) {
        if (onGoing == null)
            return false;
        for (int i = 0; i < onGoing.size(); i++) {
            if (onGoing.get(i).isInGame(player))
                return true;
        }
        return false;
    }

    @Override
    public int playersCount() {
        return (onGoing != null) ? onGoing.size() : 0;
    }

    @Override
    public boolean isWantToPlay(PlayerModel player) {
        if (requested == null)
            return false;
        if (getRequestedG(player) != null)
            return true;
        return false;
    }

    @Override
    public GameModel getPlayerGame(PlayerModel player) {
        List<GameModel> games = onGoing.stream().
                filter(r -> r.isInGame(player)).
                collect(Collectors.toList());
        if (games.size() > 0)
            return games.get(0);

        return null;
    }

    @Override
    public GameModel acceptRequest(PlayerModel player) {
        GameModel game = getRequestedG(player);
        onGoing.add(game);
        requested.remove(game);
        teleport(game);
        System.out.println(Arrays.toString(onGoing.toArray()));
        return game;
    }

    private void teleport(GameModel game) {
        Player ch = server.getPlayer(game.getBoard().getPlayers()[0].getName());
        Player pl = server.getPlayer(game.getBoard().getPlayers()[1].getName());
        Location location = arena.addGame(game);
        pl.teleport(location);
        ch.teleport(new Location(location.getWorld(), location.getX() + 1, location.getY(), location.getZ() + 1));
    }

    private void clearArea(Location chLocation) {
        Location location;
        for (int i = -18; i <= +18; i++) {
            for (int j = -21; j <= +21; j++) {
                for (int k = -18; k <= +18; k++) {
                    location = new Location(server.getWorld("world"),
                            chLocation.getX() + i, chLocation.getY() + j, chLocation.getZ() + k);
                    location.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    public void buildArena() {
        //Create Area <-->  According to challenger location.
        Location blockLo = new Location(server.getWorld("world"), 100, 240, 100);
        Location location;
        clearArea(blockLo);
        this.arena = new Arena(server, blockLo);

        for (int t = 0; t < 2; t++) {
            for (int i = -18; i <= +18; i++) {
                for (int j = -21; j <= 21; j++) {
                    location = new Location(server.getWorld("world"),
                            blockLo.getX() + i, blockLo.getY() + ((t == 0) ? 0 : 18), blockLo.getZ() + j);
                    location.getBlock().setType(Material.ICE);
                }
            }
            for (int i = -18; i <= 18; i++) {
                for (int j = 0; j <= 6; j++) {
                    location = new Location(server.getWorld("world"),
                            blockLo.getX() + i, blockLo.getY() + j, blockLo.getZ() + ((t == 0) ? +21 : -21));
                    location.getBlock().setType(Material.PRISMARINE);
                }
            }
            for (int i = -18; i <= 18; i++) {
                if (i > 2 || i < -2)
                    for (int j = 0; j <= 6; j++) {
                        location = new Location(server.getWorld("world"),
                                blockLo.getX() + i, blockLo.getY() + j, blockLo.getZ() + ((t == 0) ? -7 : 7));
                        location.getBlock().setType(Material.PRISMARINE);
                    }
            }
            for (int i = -21; i <= 21; i++) {
                for (int j = 0; j <= +18; j++) {
                    location = new Location(server.getWorld("world"),
                            blockLo.getX() + ((t == 0) ? +18 : -18), blockLo.getY() + j, blockLo.getZ() + i);
                    location.getBlock().setType(Material.PRISMARINE);
                }
            }
            int[] doors = new int[]{-14, -13, 0, 1, 14, 15};
            for (int i = -21; i <= 21; i++) {
                for (int j = 0; j <= +18; j++) {
                    int finalI = i;
                    if ((!Arrays.stream(doors).anyMatch(x -> x == finalI)) || j >= 4) {
                        location = new Location(server.getWorld("world"),
                                blockLo.getX() + ((t == 0) ? +2 : -2), blockLo.getY() + j, blockLo.getZ() + i);
                        location.getBlock().setType(Material.PRISMARINE);
                    }
                }
            }
        }
    }

    @Override
    public void sendRequest(Player player1, Player player2) {
        GameModel game = new GameModel(server, PlayerModel.factory(player1),
                PlayerModel.factory(player2));
        requested.add(game);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (requested.stream().anyMatch(g -> g == game)) {
                            player1.sendMessage("Request Failure ! ! !");
                            player2.sendMessage("Request time ended ! ! !");
                            requested.remove(game);
                        }
                    }
                }, 10000);
            }
        }).start();
    }

    @Override
    public void denyRequest(PlayerModel player) {
        GameModel game = getRequestedG(player);
        Player ch = server.getPlayer(game.getBoard().getPlayers()[0].getName());
        ch.sendMessage(game.getBoard().getPlayers()[1].getName() + " deny your request !!!");
        requested.remove(game);
    }

    private GameModel getRequestedG(PlayerModel player) {
        List<GameModel> games = requested.stream().
                filter(r -> r.isInGame(player)).
                collect(Collectors.toList());
        if (games.size() > 0)
            return games.get(0);
        return null;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void save(Player event) {
        onGoing.stream().filter(g -> g.isInGame(PlayerModel.factory(event))).collect(Collectors.toList()).forEach(
                g -> {
                    sendSaveMessage(g, databaseIt.saveGame(g));
                    arena.endGame(g);
                    onGoing.remove(g);
                    requested.remove(g);
                    g.teleportPL(server);
                });
    }

    private void sendSaveMessage(GameModel g, int savedId) {
        PlayerModel players[] = g.getBoard().getPlayers();
        System.out.println(players[0].getName() + "-" +
                players[1].getName());
        server.getPlayer(players[0].getName()).sendMessage("Game Saved With id : " + savedId);
        server.getPlayer(players[1].getName()).sendMessage("Game Saved With id : " + savedId);
    }

    @Override
    public void endGame(Player event) {
        onGoing.stream().filter(g -> g.isInGame(PlayerModel.factory(event))).collect(Collectors.toList()).forEach(
                g -> {
                    g.teleportPL(server);
                    arena.endGame(g);
                    onGoing.remove(g);
                });
        requested.stream().filter(g -> g.isInGame(PlayerModel.factory(event))).collect(Collectors.toList()).forEach(
                g -> {
                    requested.remove(g);
                });
    }

    @Override
    public void loadGame(Player event, int gameId) {
        endGame(event);
        GameModel game = databaseIt.loadGame(server, gameId);
        if (game != null) {
            List<PlayerModel> players = Arrays.stream(game.getBoard().getPlayers()).
                    filter(p -> !p.getName().equals(event.getName())).
                    collect(Collectors.toList());
            Player player = null;
            if (player != null) {
                if (players.size() == 0) {
                    String nPlayer = players.get(0).getName();
                    player = server.getPlayer(nPlayer);
                }
                player.sendMessage(event.getName() + " want to load game with id : " + gameId + " (\\accept ,\\deny))");
                event.sendMessage("We send request for your opponent . if he(she) accept the game continue !!!\n" +
                        "else just you will teleport in game arena to watch previous result");
                requested.add(game);
            } else {
                event.sendMessage("Your opponent is offline! ! !");
                requested.add(game);
                acceptRequest(PlayerModel.factory(event));
            }
        } else {
            event.sendMessage("There isn't any game with input id ! ! !");
        }
    }

}
