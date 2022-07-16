package com.soland.ap.game.model;
import org.bukkit.block.Block;

public class Nut {

    private int position;
    private String playerId;
    private Block block;

    public Nut(int position) {
        position = position;
    }

    public Nut(String playerId , int position)
    {
        this.position = position;
        this.playerId = playerId;
    }
    public Nut(String playerId , int position, Block block)
    {
        this.position = position;
        this.playerId = playerId;
        this.block = block;
    }
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Nut{" +
                "position=" + position +
                ", playerId='" + playerId + '\'' +
                ", block=" + block +
                '}';
    }
}
