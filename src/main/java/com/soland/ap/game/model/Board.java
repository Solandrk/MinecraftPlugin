package com.soland.ap.game.model;
import com.soland.ap.game.GameLauncher;
import com.soland.ap.game.interfaces.GameEvents;
import org.bukkit.block.Block;

public class Board {

    private Nut[] nuts = new Nut[9];
    private PlayerModel player1;
    private PlayerModel player2;
    private GameEvents events;
    private int turn = 1;
    private boolean draw = false;
    private PlayerModel winner ;
    private Block[] blocks = new Block[9];

    /**
     * Board contain nuts and players
     * @param player1  first player who select position
     * @param player2 second player
     * @param events An interface with handle game conditions
     * */
    public Board(PlayerModel player1, PlayerModel player2, GameEvents events) {
        this.player1 = player1;
        this.player2 = player2;
        this.events = events;
    }

    /**
     * play a turn :
     * @param nut --> nut contain : position ,player who is playing
     * */
    public void play(Nut nut) {
            if (nut != null) {
                if (!checkNutExist(nut.getPosition()))
                    nuts[nut.getPosition()] = nut;
                turn++;
            }
        checkWin();
    }
    /**
     * check player wins :
     * use Player.win()-->(this function is not static)
     * */
    private void checkWin() {
        if(player1.isWin()) {
            winner = player1;
            events.win(player1);
        }
        else if(player2.isWin()) {
            winner = player2;
            events.win(player2);
        }
        else if(turn > 9) {
            draw= true;
            events.draw();
        }
    }

    /**
     * print Board --> nuts ,walls
     * */
    public void print() {
        String board = GameLauncher.CYAN+"\n";
        for (int i = 1; i <= 9; i++) {
            if(nuts[i-1] != null)
                board+= "|"+ GameLauncher.RED+(nuts[i-1].getPlayerId() == player1.getId() ? GameLauncher.RED+"X": GameLauncher.GREEN+"O")+ GameLauncher.CYAN +"|";
            else
                board+= GameLauncher.CYAN +"| |";
            if(i%3== 0)
                board+="\n";
        }
        System.out.println(board);
    }

    /**
     * check selected position --> if exist nut in selected position return false;
     * @param position --> selected position
     * */
    public boolean checkNutExist(int position) {
       return (nuts[position] == null)?false:true;
    }
    /**
     * Select Play who should select position
     * */
    public PlayerModel playerTurn() {
        return (turn%2 !=0)?player1:player2;
    }
    /**
     * return counts of game turn
     * */
    public int getTurn() {
        return turn;
    }

    public boolean isDraw() {
        return draw;
    }

    public PlayerModel getWinner() {
        return winner;
    }

    public PlayerModel[] getPlayers()
    {
        return new PlayerModel[]{player1,player2};
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[] blocks) {
        this.blocks = blocks;
    }

    public void setBlock(Block block ,int i){
        blocks[i] = block;
    }

    public Nut[] getNuts() {
        return nuts;
    }
}
