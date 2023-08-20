/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.util;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import static com.chalwk.games.hangman.Hangman.hangmanSubmission;
import static com.chalwk.games.tictactoe.TicTacToe.tictactoeSubmission;
import static com.chalwk.util.FileIO.writeJSON;

public class util {

    public static Game[] games = new Game[0];

    public static void saveConfig(Guild guild, OptionMapping channel_id, OptionMapping role_id, JSONObject config, String fileName) throws IOException {
        config.put(guild.getId(),
                new String[]{channel_id.getAsString(),
                        role_id == null ? null : role_id.getAsString()
                });
        writeJSON(config.toString(4), fileName);
    }

    public static <T> void privateMessage(T event, Member member, String Message) {

        Guild guild = (event instanceof ButtonInteractionEvent)
                ? ((ButtonInteractionEvent) event).getGuild()
                : ((SlashCommandInteraction) event).getGuild();

        assert guild != null;
        guild.retrieveMemberById(member.getId()).queue(user -> user.getUser()
                .openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(Message).queue()));
    }

    @Nullable
    public static JSONArray getConfig(SlashCommandInteractionEvent event, String gameName, JSONObject settings, String guildID) {
        JSONArray config;
        try {
            config = settings.getJSONArray(guildID);
        } catch (Exception e) {
            event.reply(gameName + " is not setup for this Server. Use `/setup` to do this.").setEphemeral(true).queue();
            return null;
        }
        return config;
    }

    public static boolean allowInvite(SlashCommandInteractionEvent event, JSONObject config, String gameName) {

        String guildID = Objects.requireNonNull(event.getGuild()).getId();
        String channelID = event.getChannel().getId();

        JSONArray settings = getConfig(event, gameName, config, guildID);
        if (settings == null) return false;

        String configChannelID = settings.get(0).toString();
        if (!configChannelID.equals(channelID)) {
            event.reply("You cannot play (" + gameName + ") in this channel.").setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    public static void processInvite(SlashCommandInteractionEvent event, OptionMapping opponent, String challengerID, String opponentID, OptionMapping option, String challengerName, String opponentName, JSONObject config, String gameName) {
        if (!allowInvite(event, config, gameName)) return;
        if (opponent.getAsUser().isBot()) {
            event.reply("You cannot invite a bot to play " + gameName + ".").setEphemeral(true).queue();
        } else if (challengerID.equals(opponentID)) {
            event.reply("You cannot invite yourself to play " + gameName + ".").setEphemeral(true).queue();
        } else {

            Game game = null;
            if (gameName.equals("Tic-Tac-Toe")) {
                game = new Game(event, option, null, challengerID, opponentID, challengerName, opponentName, gameName);
                tictactoeSubmission(event, game);
            } else if (gameName.equals("Hangman")) {
                game = new Game(event, null, option, challengerID, opponentID, challengerName, opponentName, gameName);
                hangmanSubmission(event, game);
            }
            games = addGame(games, game);
        }
    }

    public static Game[] addGame(Game[] games, Game game) {
        Game[] newGames = new Game[games.length + 1];
        System.arraycopy(games, 0, newGames, 0, games.length);
        newGames[games.length] = game;
        return newGames;
    }

    public static Game[] removeGame(Game[] games, Game game) {
        Game[] newGames = new Game[games.length - 1];
        int index = 0;
        for (Game g : games) {
            if (g != game) {
                newGames[index++] = g;
            }
        }
        return newGames;
    }
}
