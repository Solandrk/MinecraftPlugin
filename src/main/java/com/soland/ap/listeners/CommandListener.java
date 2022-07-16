package com.soland.ap.listeners;

import com.soland.ap.EventListener;
import com.soland.ap.EventModel;
import com.soland.ap.Game;
import com.soland.ap.game.model.GameModel;
import com.soland.ap.game.model.PlayerModel;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandListener implements CommandExecutor {

    private Server server;
    private Game game;

    public CommandListener(Server server, Game game) {
        this.server = server;
        this.game = game;
    }

    private EventModel[] events = new EventModel[]{
            new EventModel("challenge", new EventListener() {
                @Override
                public void event(Player playerS, String[] data) {
                    //send Message
                    try {
                        if (data.length < 1)
                            throw new Exception("Player name couldn't be empty ! ! ");
                        Player player = server.getPlayer(data[0]);
                        playerS.sendMessage(playerS.getName() + " Challenge player " + data[0]);

                        if (player == null)
                            throw new Exception("Couldn't find player with username : " + data[0]);
                        if (!player.isOnline())
                            throw new Exception("Player is offline ! ! !");
                        Player sender = (playerS).getPlayer();
                        player.sendMessage(sender.getName() + " requested for XO game (/accept,/deny) : ");
                        game.sendRequest(sender, player);
                    } catch (Exception e) {
                        playerS.sendMessage(e.toString());
                    }
                }
            }),
            new EventModel("put", new EventListener() {
                @Override
                public void event(Player p, String[] data) {
                    GameModel gm = game.getPlayerGame(PlayerModel.factory(p));
                    try {
                        if (!gm.isEnd()) {
                            if (!gm.playerTurn().getName().trim().equals(p.getName().trim())) {
                                p.sendMessage("it's not your turn ! ! !");
                                return;
                            }
                            switch (gm.play(Integer.parseInt(data[0])-1)) {
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
                                case CONTINUE: {}
                            }
                        } else
                            p.sendMessage("Game is over ! ! !");
                    }catch (IndexOutOfBoundsException exception) {
                        p.sendMessage("position couldn't be empty");
                    }catch (NumberFormatException exception) {
                        p.sendMessage("incorrect position ! ! !");
                    }
                }
            }),
            new EventModel("deny", new EventListener() {
                @Override
                public void event(Player playerS, String[] data) {
                    PlayerModel player = PlayerModel.factory(playerS);
                    if (game.isWantToPlay(player)) {
                        game.denyRequest(player);
                    }
                }
            }),
            new EventModel("accept", new EventListener() {
                @Override
                public void event(Player playerS, String[] data) {
                    PlayerModel player = PlayerModel.factory(playerS);
                    if (game.isWantToPlay(player)) {
                        game.acceptRequest(player);
                    }
                }
            }),
            new EventModel("build", new EventListener() {
        @Override
        public void event(Player event, String[] data) {
            game.buildArena();
        }
    }),
            new EventModel("save", new EventListener() {
        @Override
        public void event(Player event, String[] data) {
            if(game.isPlaying(PlayerModel.factory(event)))
                game.save(event);
        }
    }),
            new EventModel("end", new EventListener() {
        @Override
        public void event(Player event, String[] data) {
            if(game.isPlaying(PlayerModel.factory(event)))
                game.endGame(event);
        }
    }),
            new EventModel("load", new EventListener() {
        @Override
        public void event(Player event, String[] data) {
            game.loadGame(event, Integer.parseInt(data[0]));
        }
    })};

    private boolean checkPosition() {
        return false;
    }

    private void putBlockInPosition() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Arrays.stream(events).
                    filter(e -> e.getEventTag().equals(command.getName().toLowerCase())).
                    collect(Collectors.toList()).
                    forEach(e -> e.callEvent((Player) sender, args));
        }
        return false;
    }
}
