/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.commands;

import com.chalwk.listeners.CommandInterface;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.chalwk.Main.*;
import static com.chalwk.util.util.saveConfig;

public class Setup implements CommandInterface {

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Setup Hangman or Tic-Tac-Toe for your server.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        optionData result = getOptionData();
        options.add(result.games());
        options.add(result.channelID());
        options.add(result.roleID());
        return options;
    }

    @NotNull
    private optionData getOptionData() {

        OptionData games = new OptionData(OptionType.STRING, "game", "The game you want to setup.");
        OptionData channelID = new OptionData(OptionType.CHANNEL, "channel_id", "The channel id where hangman will be played.");
        OptionData roleID = new OptionData(OptionType.ROLE, "role_id", "The role id of the players who can play hangman.");

        games.addChoice("Tic-Tac-Toe", "tictactoe");
        games.addChoice("Hangman", "hangman");

        games.setRequired(true);
        channelID.setRequired(true);
        roleID.setRequired(false);

        return new optionData(games, channelID, roleID);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {

        Member member = event.getMember();

        Guild guild = getGuild(event);
        OptionMapping game = event.getOption("game");
        OptionMapping channelID = event.getOption("channel_id");
        OptionMapping roleID = event.getOption("role_id");

        assert game != null;
        assert member != null;
        assert channelID != null;

        if (guild.getTextChannelById(channelID.getAsLong()) == null) {
            event.reply("Invalid channel ID.").setEphemeral(true).queue();
        } else if (roleID != null && guild.getRoleById(roleID.getAsString()) == null) {
            event.reply("Invalid role ID.").setEphemeral(true).queue();
        } else if (game.getAsString().equals("tictactoe")) {
            saveConfig(guild, channelID, roleID, ticTacToeConfig, "tictactoe.json");
            event.reply("Tic-Tac-Toe has been setup in the " + guild.getTextChannelById(channelID.getAsLong()).getAsMention() + " channel.").setEphemeral(true).queue();
        } else if (game.getAsString().equals("hangman")) {
            saveConfig(guild, channelID, roleID, hangmanConfig, "hangman.json");
            event.reply("Hangman has been setup in the " + guild.getTextChannelById(channelID.getAsLong()).getAsMention() + " channel.").setEphemeral(true).queue();
        } else {
            event.reply("Invalid game or something went wrong!").setEphemeral(true).queue();
        }
    }

    private record optionData(OptionData games, OptionData channelID, OptionData roleID) {
    }
}
