package com.soland.ap.game.model;

import com.soland.ap.game.GameLauncher;
import com.soland.ap.game.interfaces.GameEvents;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameModel implements GameEvents, Serializable {

    private Board board;
    private boolean isEnd;
    private PlayerModel playerTurn;
    private String fileName;
    private Server server;

    public GameModel(PlayerModel player1, PlayerModel player2) {
        board = new Board(player1, player2, this);
    }
    public GameModel(Server server,PlayerModel player1, PlayerModel player2) {
        this.server = server;
        board = new Board(player1, player2, this);
    }
    public GameModel(PlayerModel player1, PlayerModel player2, GameEvents ev) {
        board = new Board(player1, player2, ev);
    }

    public GameModel(Board board) {
        this.board = board;
    }

    public Conditions play(int position, Block block, Player player) {
        System.out.println(position);
        if (isEnd)
            return Conditions.END_GAME;
        if (position > 9 || position < 0) {
            return Conditions.OUT_OF_RANGE;
        }
        if (board.checkNutExist(position))
            return Conditions.NUT_EXIST;
        if (player.getName().equals(board.getPlayers()[0].getName()))
            block.setType(Material.DIAMOND_BLOCK);
        else
            block.setType(Material.MAGMA_BLOCK);

        board.play(board.playerTurn().addNut(position, block));
        if (board.getTurn() > 9 || isEnd) {
            return Conditions.END_GAME;
        } else
            return Conditions.CONTINUE;
    }

    public Conditions play(int position) {
        System.out.println(position);
        if (isEnd)
            return Conditions.END_GAME;
        if (position > 9 || position < 1) {
            return Conditions.OUT_OF_RANGE;
        }
        if (board.checkNutExist(position))
            return Conditions.NUT_EXIST;
        board.play(board.playerTurn().addNut(position - 1));
        if (board.playerTurn().getName().equals(board.getPlayers()[0].getName()))
            getBoard().getBlocks()[position].setType(Material.DIAMOND_BLOCK);
        else
            getBoard().getBlocks()[position].setType(Material.MAGMA_BLOCK);
        if (board.getTurn() > 9 || isEnd) {
            return Conditions.END_GAME;
        } else
            return Conditions.CONTINUE;
    }

    @Override
    public void win(PlayerModel player) {
        isEnd = true;
        if(server != null) {
            Player p1 = server.getPlayer(board.getPlayers()[0].getName());
            Player p2 = server.getPlayer(board.getPlayers()[0].getName());
            p1.sendMessage("Winner is : " + player.getName() + " with Id : " + player.getId());
            p2.sendMessage("Winner is : " + player.getName() + " with Id : " + player.getId());
            Location loc ;
            if(p1.getName().equals(player.getName())) {
                loc = p1.getLocation();
            }else
                loc = p2.getLocation();

            Firework fw = p1.getWorld().spawn(loc, Firework.class);
            Firework fw2 = p1.getWorld().spawn(loc, Firework.class);
            Firework fw3 = (Firework) p1.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            org.bukkit.Color color = org.bukkit.Color.RED;
            org.bukkit.Color color2 = org.bukkit.Color.BLUE;
            fwm.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(color,color2).build());
            fwm.setPower(2);
            fw.setFireworkMeta(fwm);
            fw2.setFireworkMeta(fwm);
        }

    }

    @Override
    public void draw() {
        isEnd = true;
        if(server != null) {
            server.getPlayer(board.getPlayers()[0].getName()).sendMessage("Draw !!!");
            server.getPlayer(board.getPlayers()[1].getName()).sendMessage("Draw !!!");
        }
    }

    public PlayerModel playerTurn() {
        return board.playerTurn();
    }

    public Board getBoard() {
        return board;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isInGame(PlayerModel player) {
        if (Arrays.stream(board.getPlayers()).filter(p -> p.getName().trim().equals(player.getName().trim())).collect(Collectors.toList()).size() > 0)
            return true;
        return false;
    }


    public void teleportPL(Server server) {
        Arrays.stream(board.getPlayers()).forEach(p -> {
            if(p.getLocation() != null)
               server.getPlayer(p.getName()).teleport(p.getLocation());
        });
    }

    public void setBoard(List<String> fPositions, List<String> sPositions) {
        for (int i = 0; i < fPositions.size(); i++) {
            String position= String.valueOf(fPositions.get(i).charAt(1));
            board.play(board.getPlayers()[0].addNut(Integer.parseInt(position)));
        }
        for (int i = 0; i < sPositions.size(); i++) {
            String position= String.valueOf(sPositions.get(i).charAt(1));
            board.play(board.getPlayers()[1].addNut(Integer.parseInt(position)));
        }
    }

    public String getBoardStructure() {
        Nut[] nuts = board.getNuts();
        String positions = "";
        for (int i = 0; i < nuts.length; i++) {
            Nut nut = nuts[i];
            StringBuilder builder = new StringBuilder();
            if(nut != null) {
                if (nut.getPlayerId().equals(board.getPlayers()[0].getId())) {
                    builder.append(1);
                } else if (nut.getPlayerId().equals(board.getPlayers()[1].getId())) {
                    builder.append(2);
                }
                builder.append(nut.getPosition());
                positions += builder.toString() + ",";
            }
        }
        System.out.println(positions);
        return positions;
    }
}
