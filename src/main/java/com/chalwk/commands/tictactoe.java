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

import static com.chalwk.Main.ticTacToeConfig;
import static com.chalwk.games.Layout.boards;
import static com.chalwk.util.util.processInvite;

public class tictactoe implements CommandInterface {

    @Override
    public String getName() {
        return "tictactoe";
    }

    @Override
    public String getDescription() {
        return "Invite someone to play Tic-Tac-Toe.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        OptionData opponent = new OptionData(OptionType.USER, "opponent", "The user you want to invite.").setRequired(true);
        OptionData board = new OptionData(OptionType.INTEGER, "board", "The board size you want to play on.").setRequired(true);
        for (int i = 0; i < boards.length; i++) {
            String size = boards[i].length + "x" + boards[i].length;
            board.addChoice(size, i);
        }
        options.add(opponent);
        options.add(board);
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        OptionMapping size = event.getOption("board");
        OptionMapping opponent = event.getOption("opponent");
        assert opponent != null;
        assert member != null;
        String challengerID = member.getId();
        String opponentID = opponent.getAsUser().getId();
        String challengerName = member.getEffectiveName();
        String opponentName = opponent.getAsUser().getEffectiveName();
        processInvite(event, opponent, challengerID, opponentID, size, challengerName, opponentName, ticTacToeConfig, "Tic-Tac-Toe");
    }
}
