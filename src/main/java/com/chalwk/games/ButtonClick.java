/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import static com.chalwk.Main.games;
import static com.chalwk.games.tictactoe.TicTacToe.makeMove;
import static com.chalwk.games.tictactoe.TicTacToe.moveAllowed;

public class ButtonClick {

    public static void onClick(ButtonInteractionEvent event) {

        Button button = event.getButton();
        Member member = event.getMember();
        assert member != null;

        String memberID = member.getId();
        String buttonID = button.getId();
        assert buttonID != null;

        String buttonLabel = button.getLabel();

        for (Game game : games) {

            String gameName = game.gameName;
            String challengerID = game.challengerID;
            String opponentID = game.opponentID;

            if (memberID.equals(challengerID) || memberID.equals(opponentID)) {
                if (!game.started) {
                    if (buttonID.equalsIgnoreCase("accept")) {
                        if (canClick(memberID, opponentID, event, "You are not the opponent. Unable to accept."))
                            continue;
                        game.acceptInvitation(event);
                    } else if (buttonID.equalsIgnoreCase("decline")) {
                        if (canClick(memberID, opponentID, event, "You are not the opponent. Unable to decline."))
                            continue;
                        game.declineInvitation(event, member);
                    } else if (buttonID.equalsIgnoreCase("cancel")) {
                        if (canClick(memberID, challengerID, event, "You are not the challenger. Unable to cancel."))
                            continue;
                        game.cancelInvitation(event, member);
                    } else if (gameName.equals("Tic-Tac-Toe")) {
                        if (!moveAllowed(buttonLabel, game)) {
                            return;
                        } else {
                            makeMove(event, buttonLabel, game);
                        }
                    }
                }
            }
        }
    }

    private static boolean canClick(String memberID, String playerID, ButtonInteractionEvent event, String message) {
        if (!memberID.equals(playerID)) {
            event.reply(message).setEphemeral(true).queue();
            return true;
        }
        return false;
    }
}
