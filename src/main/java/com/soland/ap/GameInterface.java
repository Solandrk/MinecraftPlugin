package com.soland.ap;

import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.entity.Player;

public interface GameInterface {

    boolean isPlaying(PlayerModel player);
    int playersCount();
    boolean isWantToPlay(PlayerModel player);
    GameModel acceptRequest(PlayerModel player);
    GameModel getPlayerGame(PlayerModel player);
    void sendRequest(Player player1, Player player2);
    void denyRequest(PlayerModel player);
    void save(Player event);
    void loadGame(Player event,int gameName);

    void endGame(Player event);
}
