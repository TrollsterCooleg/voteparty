package me.cooleg.simplevoteparty.command;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.libs.kyori.adventure.platform.facet.Facet;
import me.cooleg.simplevoteparty.SimpleVoteParty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import sun.management.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoteCommand implements CommandExecutor {

    SimpleVoteParty main;

    public VoteCommand(SimpleVoteParty simpleVoteParty) {
        main = simpleVoteParty;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.BLUE + "Plugin by Cooleg.");
        }
        switch (args[0]) {
            case ("reload"):
                main.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Reloaded Successfully!");
                break;
            case ("simulatevote"):
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateVotes("Trollster");
                        main.reloadConfig();
                        runCommands("Trollster");
                        main.reloadConfig();
                    }
                }.runTaskAsynchronously(main);
                break;
            default:
                sender.sendMessage(ChatColor.BLUE + "Plugin by Cooleg.");
                break;
        }
        return false;
    }

    private void updateVotes(String vote) {
        FileConfiguration config = main.getConfig();
        config.set("CurrentVotes", config.getInt("CurrentVotes") + 1);
        if (config.getBoolean("VoteForRewards")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(vote);
            List<String> players = config.getStringList("Players");
            players.add(String.valueOf(player.getUniqueId()));
            config.set("Players", players);
        }
        main.saveConfig();
    }

    private void runCommands(String p) {
        FileConfiguration config = main.getConfig();
        if (config.getInt("CurrentVotes") < config.getInt("NeededVotes")) {return;}
        config.set("CurrentVotes", 0);
        main.saveConfig();
        List<String> PlayerCommands = config.getStringList("PlayerCommands");
        List<String> GlobalCommands = config.getStringList("GlobalCommands");

        ConsoleCommandSender console = Bukkit.getConsoleSender();

        for (String s : PlayerCommands) {
            if (!s.contains("%player%")) {continue;}
            if (config.getBoolean("VoteForRewards")) {
                boolean offline = config.getBoolean("CommandsWorkOffline");
                List<String> players = config.getStringList("Players");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (String usr : players) {
                            UUID user = UUID.fromString(usr);
                            if (!Bukkit.getOfflinePlayer(user).isOnline() && !offline) {
                                continue;
                            }
                            Bukkit.dispatchCommand(console, s.replace("%player%", Bukkit.getOfflinePlayer(user).getName()));
                        }
                    }
                }.runTask(main);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player play : Bukkit.getOnlinePlayers()) {
                            Bukkit.dispatchCommand(console, s.replace("%player%", play.getName()));
                        }
                    }
                }.runTask(main);
            }
        }

        for (String s : GlobalCommands) {
            if (s.contains("%player%")) {continue;}
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(console, s);
                }
            }.runTask(main);
        }
        List<String> mt = new ArrayList<>();
        config.set("Players", mt);
        main.saveConfig();
    }
}
