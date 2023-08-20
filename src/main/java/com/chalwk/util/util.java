/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.util;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.json.JSONObject;

import java.io.IOException;

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
