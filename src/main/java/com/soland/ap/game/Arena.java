package com.soland.ap.game;

import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.Nut;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Arena {

    private Server server;
    private Location location;
    private GameModel[] games = new GameModel[6];
    private HashMap<Integer,Location> locations = new HashMap<>();

    public Arena(Server server , Location location) {
        this.server = server;
        this.location = location;
        setUpGamesLo();
    }

    private void setUpGamesLo() {
        locations.put(0,new Location(server.getWorld("world"),location.getX()-9,location.getY()+2,location.getZ()-15));
        locations.put(1,new Location(server.getWorld("world"),location.getX()+9,location.getY()+2,location.getZ()-15));
        locations.put(2,new Location(server.getWorld("world"),location.getX()-9,location.getY()+2,location.getZ()+1));
        locations.put(3,new Location(server.getWorld("world"),location.getX()+9,location.getY()+2,location.getZ()+1));
        locations.put(4,new Location(server.getWorld("world"),location.getX()-9,location.getY()+2,location.getZ()+15));
        locations.put(5,new Location(server.getWorld("world"),location.getX()+9,location.getY()+2,location.getZ()+15));
    }

    public Location addGame(GameModel game){
        for (int i = 0; i < games.length; i++) {
            if(games[i] == null) {
                games[i] = game;
                createBoard(game,i);
                return locations.get(i);
            }
        }
        return null;
    }
    public void endGame(GameModel game)
    {
        for (int i = 0; i < games.length; i++)
            if (games[i] == game)
                games[i] = null;
    }

    private void createBoard(GameModel game ,int index) {
        Location blockLo = locations.get(index);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                location = new Location(server.getWorld("world"),
                        blockLo.getX()+((index%2==0)?-8:+8),blockLo.getY()+1+i,blockLo.getZ()+j);
                Nut nut = game.getBoard().getNuts()[i * 3 + j];
                if(nut != null) {
                    System.out.println(Arrays.toString(game.getBoard().getPlayers()));
                    if(nut.getPlayerId().equals(game.getBoard().getPlayers()[0].getId())) {
                        location.getBlock().setType(Material.DIAMOND_BLOCK);
                    }
                    else
                        location.getBlock().setType(Material.MAGMA_BLOCK);
                }else
                    location.getBlock().setType(Material.QUARTZ_BLOCK);
                game.getBoard().setBlock(location.getBlock(),i * 3 + j);
            }
        }
    }

}
