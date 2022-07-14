package me.cooleg.simplevoteparty;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessVotes implements Listener {
    SimpleVoteParty main;
    public ProcessVotes(SimpleVoteParty simpleVoteParty) {
        main = simpleVoteParty;
    }

    @EventHandler
    public void onVote(VotifierEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateVotes(e.getVote().getUsername());
                main.reloadConfig();
                runCommands(e.getVote().getUsername());
                main.reloadConfig();
            }
        }.runTaskAsynchronously(main);
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
