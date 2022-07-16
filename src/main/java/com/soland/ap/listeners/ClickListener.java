package com.soland.ap.listeners;

import com.soland.ap.Game;
import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ClickListener implements Listener {

    private Game game;

    public ClickListener(Game game) {
        this.game = game;
    }

    @EventHandler
    public void ballFiring(PlayerInteractEvent e) {
        if (e.getHand() == EquipmentSlot.HAND) {
            Player p = e.getPlayer();
            Action a = e.getAction();
            GameModel gm = game.getPlayerGame(PlayerModel.factory(p));
            if ((a == Action.RIGHT_CLICK_BLOCK) && gm != null) {
                Block[] blocks = gm.getBoard().getBlocks();
                Block block = e.getClickedBlock();
                for (int i = 0; i < 9; i++) {
                    if (blocks[i].getX() == block.getX() &&
                            blocks[i].getY() == block.getY() &&
                            blocks[i].getZ() == block.getZ()) {
                        if (!gm.isEnd()) {
                            if (!gm.playerTurn().getName().trim().equals(p.getName().trim())) {
                                p.sendMessage("it's not your turn ! ! !");
                                return;
                            }
                            switch (gm.play(i, block, p)) {
                                case END_GAME: {
                                    game.getServer().getPlayer(gm.getBoard().getPlayers()[0]
                                            .getName()).sendMessage("/save --> save game \n" +
                                            "/end back to first location");
                                    return;
                                }
                                case OUT_OF_RANGE: {
                                    p.sendMessage("input number should be in [1,9] range");
                                }
                                case NUT_EXIST: {
                                    p.sendMessage("nut exist in this position - try again ! ! !");
                                }
                                case CONTINUE: {

                                }
                            }
                        }else
                            p.sendMessage("Game is over ! ! !");
                    }
                }
            }
        }
    }
}
