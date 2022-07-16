package com.soland.ap;
import com.soland.ap.listeners.BlockBreakListener;
import com.soland.ap.listeners.ClickListener;
import com.soland.ap.listeners.CommandListener;
import org.bukkit.plugin.java.JavaPlugin;


public final class XOPlugin extends JavaPlugin {

    private static final String WELCOME_MESSAGE =
            " Welcome ! Welcome !" +
                    "\nto Soland world !!! " +
                    "\nit's my honour to see you ";
    private Game game;
    private CommandListener commandListener;
    @Override
    public void onEnable() {
        System.out.println(WELCOME_MESSAGE);
        game = new Game(getServer());
        commandListener = new CommandListener(getServer(),game);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(game),this);
        getServer().getPluginManager().registerEvents(new ClickListener(game),this);
        this.getServer().getPluginCommand("challenge").setExecutor(commandListener);
        this.getServer().getPluginCommand("put").setExecutor(commandListener);
        this.getServer().getPluginCommand("deny").setExecutor(commandListener);
        this.getServer().getPluginCommand("accept").setExecutor(commandListener);
        this.getServer().getPluginCommand("build").setExecutor(commandListener);
        this.getServer().getPluginCommand("end").setExecutor(commandListener);
        this.getServer().getPluginCommand("save").setExecutor(commandListener);
        this.getServer().getPluginCommand("load").setExecutor(commandListener);
    }
}
