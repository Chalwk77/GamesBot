/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.commands;

import com.chalwk.listeners.CommandInterface;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

import static com.chalwk.Main.hangmanConfig;
import static com.chalwk.games.Layout.gallows;
import static com.chalwk.util.util.processInvite;

public class hangman implements CommandInterface {

    @Override
    public String getName() {
        return "hangman";
    }

    @Override
    public String getDescription() {
        return "Invite someone to play Hangman.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        OptionData opponent = new OptionData(OptionType.USER, "opponent", "The user you want to invite.").setRequired(true);
        OptionData art = new OptionData(OptionType.INTEGER, "gallows", "Set the Gallows art you want to use.").setRequired(true);
        for (int i = 0; i < gallows.length; i++) {
            art.addChoice("Gallows: " + (i + 1), i);
        }
        options.add(opponent);
        options.add(art);
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        OptionMapping art = event.getOption("gallows");
        OptionMapping opponent = event.getOption("opponent");
        assert opponent != null;
        assert member != null;
        String challengerID = member.getId();
        String opponentID = opponent.getAsUser().getId();
        String challengerName = member.getEffectiveName();
        String opponentName = opponent.getAsUser().getEffectiveName();
        processInvite(event, opponent, challengerID, opponentID, art, challengerName, opponentName, hangmanConfig, "Hangman");
    }
}
