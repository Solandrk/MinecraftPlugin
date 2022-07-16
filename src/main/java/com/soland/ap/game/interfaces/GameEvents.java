package com.soland.ap.game.interfaces;

import com.soland.ap.game.model.PlayerModel;

public interface GameEvents {

    void win(PlayerModel player);

    void draw();

}
