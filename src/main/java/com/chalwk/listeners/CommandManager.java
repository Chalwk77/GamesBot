/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */

package com.chalwk.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {

    private final List<CommandInterface> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            for (CommandInterface command : commands) {
                guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (CommandInterface command : commands) {
            String this_command = event.getName();
            if (this_command.equals(command.getName())) {
                try {
                    command.execute(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
    }

    public void add(CommandInterface command) {
        commands.add(command);
    }
}
