package com.soland.ap.game;

import com.soland.ap.game.exeption.InvalidPlayer;
import com.soland.ap.game.exeption.LoadException;
import com.soland.ap.game.interfaces.FileInterface;
import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class GameLauncher implements FileInterface {

    public static final String CYAN = "\033[0;36m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String WHITE = "\033[0;37m";   // WHITE

    public static final String START_GAME = "/start";
    public static final String SAVE_GAME = "/save";
    public static final String PUT_NUT = "/put";
    public static final String LOAD_GAME = "/load";
    public static final String HELP = "/help";
    public static final String EXIT = "/exit";
    public static final String CONTINUE = "/continue";

    private GameModel inProcess;
    public boolean inProcessing = false;
    public boolean closeS = false;

    public GameLauncher()
    {
        help();
    }

    public void cli(String command) {
        if (command != null) {
            String[] inputs = command.split(" ");
            if (inputs.length > 0)
                switch (inputs[0]) {
                    case START_GAME : newGame(Arrays.copyOfRange(inputs, 1, inputs.length));break;
                    case SAVE_GAME : save();break;
                    case PUT_NUT : putNut(inputs);break;
                    case LOAD_GAME : loadGame(inputs);break;
                    case EXIT : exit();break;
                    case HELP : help();break;
                    default : {
                        printMessage("Invalid input !!!");
                        if (inProcessing && inProcess != null)
                            processingGame();
                    }
                }
        }
    }

    private void help() {
        printMessage("/start player1 player 2 --> start new Game \n" +
                     "/save --> if exist game which is running it will save \n" +
                     "/put position[1,9] --> in running game . it will put a nut on selected position\n" +
                     "/load name --> load game\n" +
                     "/help --> show commands\n" +
                     "/exit -->exit");
        if(inProcessing)
            processingGame();
    }

    private void exit() {
        if(inProcessing) {
            System.out.println("Save Game (/y)");
            closeS = true;
            switch (new Scanner(System.in).nextLine()) {
                case "/y" : save();break;
                default : {
                    inProcessing=false;
                    inProcess = null;
                }
            }
        }else
            closeS = true;
    }

    private void putNut(String[] inputs) {
        if (inProcess == null) {
            printMessage("There isn't any running game");
            return;
        }
        try {
            String position = inputs[1];
            if (position != null) {
                switch (inProcess.play(Integer.parseInt(position))) {
                    case END_GAME : {
                        System.out.println("Save Game ?(/save,any press)");
                        if (new Scanner(System.in).nextLine().trim().equals("/save"))
                            save();
                        help();
                        inProcess = null;
                        inProcessing = false;
                        return;
                    }
                    case OUT_OF_RANGE :{
                        printMessage(RED + "input number should be in [1,9] range");
                        processingGame();
                    }
                    case NUT_EXIST : {
                        printMessage("nut exist in this position - try again ! ! !");
                        processingGame();
                    }
                    case CONTINUE : {
                        processingGame();
                    }
                }
            }
        } catch (IndexOutOfBoundsException exception) {
            printMessage("Incorrect input . select position after /put  LIKE--> /put 1");
            processingGame();
        }catch (NumberFormatException ex)
        {
            printMessage("Invalid input -Position is a number between [1,9] --correct-->/put number[1,9]");
            processingGame();
        }
    }

    private void newGame(String[] players) {
        try {
            if (players.length < 2)
                throw new InvalidPlayer();
            if(inProcessing){
                if(!existCLG())
                    return;
            }
            inProcess = new GameModel(new PlayerModel(players[0]), new PlayerModel(players[1]));
            inProcessing = true;
            processingGame();
        } catch (InvalidPlayer e) {
            printMessage(e.toString());
            if (inProcessing)
                processingGame();
            return;
        }
    }

    public void processingGame() {
        if (!inProcess.isEnd()) {
            Scanner scanner = new Scanner(System.in);
            inProcess.getBoard().print();
            System.out.print(PURPLE + inProcess.playerTurn().getName() + " : ");
            String input = scanner.nextLine();
            cli(input);
        }else
        {
            inProcessing = false;
            inProcess = null;
        }
    }

    public static void printMessage(String message) {
        System.out.println(YELLOW +"------------------------------------------------------------------------------------");
        System.out.println(RED +message);
        System.out.println(YELLOW +"------------------------------------------------------------------------------------");
    }

    @Override
    public void save() {
        if(inProcess == null) {
            printMessage("There isn't any running game");
            return;
        }
        boolean exit = true;
        if(!inProcess.isEnd()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Save and Exit or Continue?" + GREEN + "(/continue,any press)");
            if (scanner.nextLine().trim().equals("/continue"))
                exit = false;
        }
        try {
            if(inProcess.getFileName() == null) {
                inProcess.setFileName(String.valueOf(inProcess.hashCode()));
            }
            FileOutputStream outputStream = new FileOutputStream("games/"+inProcess.getFileName());
            ObjectOutputStream ooS = new ObjectOutputStream(outputStream);
            ooS.writeObject(inProcess);
            ooS.close();
            outputStream.close();
            System.out.println("Game Saved !!! load it by Name -> "+inProcess.getFileName());
            if(exit || closeS)
            {
                inProcess = null;
                inProcessing = false;
            }else{
                processingGame();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadGame(String[] input) {
        try {
            if(input.length< 2) {
                throw new LoadException();
            }
        }catch (LoadException e)
        {
            printMessage(e.toString());
            if(inProcessing)
                processingGame();
            return;
        }
        if(inProcessing && inProcess != null) {
            if(!existCLG()) {
                processingGame();
                return;
            }
        }
        String name = input[1];
        try {
            File file = new File("games/"+name);
            if (!file.exists())
            {
                printMessage("There isn't any game with name : "+name);
                if(inProcessing)
                    processingGame();
                return;
            }
            FileInputStream inputStream = new FileInputStream("games/"+name);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            inProcess = (GameModel) objectStream.readObject();
            inProcessing=true;
            if(inProcess.isEnd())
            {
                printMessage("The game is over !!! "
                        +((inProcess.getBoard().isDraw())?"Condition : Draw":
                        "Winner is "+inProcess.getBoard().getWinner().getName() +
                                " With id : "+inProcess.getBoard().getWinner().getId()));
                inProcess.getBoard().print();
                inProcessing = false;
                inProcess = null;
            }else
                processingGame();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean existCLG() {
        System.out.println(BLUE+"\nsave and do? continue? or just load?(/save_do,/continue,any press)\n");
        String input = new Scanner(System.in).nextLine().trim();
        if(input.equals("/save_do")) {
            save();
        }else if(input.equals("/continue")) {
            return false;
        }
        return true;
    }

    @Override
    public void pause() {

    }
}
