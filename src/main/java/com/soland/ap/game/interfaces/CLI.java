package com.soland.ap.game.interfaces;

import com.soland.ap.game.GameLauncher;

import java.util.Scanner;

public class CLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to XO game :");
        GameLauncher launcher = new GameLauncher();
            while (true) {
                if(launcher.closeS)
                    break;
               if (!launcher.inProcessing)
                   launcher.cli(scanner.nextLine());
            }

    }
}
