package com.soland.ap.game.exeption;

public class InvalidPlayer extends GameExceptions
{
    public InvalidPlayer() {
        super("Invalid inputs --Correct input--> /start player1 player2");
    }
}