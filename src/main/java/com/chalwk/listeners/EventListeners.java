/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */

package com.chalwk.listeners;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.chalwk.games.ButtonClick.onClick;

public class EventListeners extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        System.out.println("""
                _______________________________________________
                  ___   _   __  __ ___ ___   ___  ___ _____
                 / __| /_\\ |  \\/  | __/ __| | _ )/ _ \\_   _|
                | (_ |/ _ \\| |\\/| | _|\\__ \\ | _ \\ (_) || |
                 \\___/_/ \\_\\_|  |_|___|___/ |___/\\___/ |_|
                Copyright (c) 2023, GamesBot. Jericho Crosby
                _______________________________________________""");
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        onClick(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

    }
}