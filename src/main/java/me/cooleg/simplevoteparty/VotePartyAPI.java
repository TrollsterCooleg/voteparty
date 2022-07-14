package me.cooleg.simplevoteparty;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class VotePartyAPI {

    private SimpleVoteParty main;
    public VotePartyAPI(SimpleVoteParty simpleVoteParty) {
        main = simpleVoteParty;
    }

    public void setRequirement(int num) {
        main.getConfig().set("NeededVotes", num);
        warning();
    }

    public int getRequirement() {
        return main.getConfig().getInt("NeededVotes");
    }

    public void setCurrent(int num) {
        main.getConfig().set("CurrentVotes", num);
        warning();
    }

    public int getCurrent() {
        return main.getConfig().getInt("CurrentVotes");
    }

    private void warning() {
        if (main.getConfig().getBoolean("API-Alerts")) {
            Bukkit.getLogger().info(ChatColor.YELLOW + "A plugin is adjusting the VoteParty through its API.");
            Bukkit.getLogger().info(ChatColor.YELLOW + "If you wish to disable these warnings, turn them off in the config!");
        }
    }
}
