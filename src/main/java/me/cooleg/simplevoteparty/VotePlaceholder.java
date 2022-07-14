package me.cooleg.simplevoteparty;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VotePlaceholder extends PlaceholderExpansion {
    SimpleVoteParty main;
    public VotePlaceholder(SimpleVoteParty simpleVoteParty) {
        main = simpleVoteParty;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "voteparty";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Cooleg";
    }

    @Override
    public @NotNull String getVersion() {
        return "4.2.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equals("votes")) {return String.valueOf(main.getConfig().getInt("CurrentVotes"));}
        if (params.equals("needed")) {return String.valueOf(main.getConfig().getInt("NeededVotes"));}

        return "";
    }


}
