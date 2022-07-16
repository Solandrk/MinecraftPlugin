package com.soland.ap.game.model;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerModel implements Serializable {

    private String name;
    private String id;
    private int nutCount;
    private ArrayList<Nut> nuts;
    private boolean isTurn = false;
    private Location location;
    public PlayerModel() {
    }

    public PlayerModel(String name) {
        this.name = name;
        this.id = String.valueOf(this.hashCode());
    }

    public void addNut(Nut nut) {
        if (nuts == null)
            nuts = new ArrayList<>();
        nuts.add(nut);
    }

    public Nut addNut(int position) {
        if (nuts == null)
            nuts = new ArrayList<>();
        nuts.add(new Nut(getId(), position));
        return nuts.get(nuts.size() - 1);
    }
    public Nut addNut(int position,Block block) {
        if (nuts == null)
            nuts = new ArrayList<>();
        nuts.add(new Nut(getId(), position,block));
        return nuts.get(nuts.size() - 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNutCount() {
        return nutCount;
    }

    public void setNutCount(int nutCount) {
        this.nutCount = nutCount;
    }

    public boolean isWin() {
        if(nuts == null )
            return false;
        if(nuts.size()<3)
            return false;
        int stateC = 0;
        int k = 0;
        boolean win ;
        for (int i = 1; i <= 4; i++) {
            if (i == 2)
                k = 2;
            else k = 0;
            stateC = 0;
            win = true;
            for (; k < 9; k+=i) {
                int finalK = k;
                List<Nut> sNuts = new ArrayList<>();
                for (Nut nut :
                        nuts) {
                    if(nut.getPosition() == finalK){
                        sNuts.add(nut);
                    }
                }
                if(sNuts.size() == 0)
                    win = false;
                stateC++;
                if(stateC==3){
                    if(win)
                        return true;
                    stateC = 0;
                    win = true;
                }
                if(i == 3 &&k<8&& k+3>8) {
                    k+=3;
                    k %= 8;
                    k-=3;
                }
            }
        }
        return false;
    }

    public boolean isTurn() {
        return isTurn;
    }

    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    public Location getLocation() {

        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public static PlayerModel factory(org.bukkit.entity.Player player) {
        PlayerModel playerModel = new PlayerModel(player.getName());
        playerModel.
                setLocation(new Location(player.getWorld(),
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ()));
        return playerModel;
    }
}
