package com.soland.ap.listeners;

import com.soland.ap.Game;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private Game game;

    public BlockBreakListener(Game game){
        this.game = game;
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getX()>40 && event.getBlock().getX()<130 &&
                event.getBlock().getY()>210 && event.getBlock().getY()<270 &&
                event.getBlock().getZ()>60 && event.getBlock().getZ()<140 )
             event.setCancelled(true);
    }
}
