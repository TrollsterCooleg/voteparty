package me.cooleg.simplevoteparty;

import me.cooleg.simplevoteparty.command.VoteCommand;
import me.cooleg.simplevoteparty.command.VoteTab;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleVoteParty extends JavaPlugin {


    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("voteparty").setExecutor(new VoteCommand(this));
        getCommand("voteparty").setTabCompleter(new VoteTab());

        Bukkit.getServer().getPluginManager().registerEvents(new ProcessVotes(this), this);

        new VotePlaceholder(this).register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
