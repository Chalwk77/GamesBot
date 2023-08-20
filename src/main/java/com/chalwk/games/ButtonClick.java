/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.chalwk.Main.hangmanConfig;
import static com.chalwk.Main.ticTacToeConfig;
import static com.chalwk.games.tictactoe.TicTacToe.*;
import static com.chalwk.util.util.games;

public class ButtonClick {

    public static void onClick(ButtonInteractionEvent event) {

        Channel channel = event.getChannel();
        String channelID = channel.getId();

        Button button = event.getButton();
        Member member = event.getMember();
        assert member != null;

        List<Role> roles = member.getRoles();
        String memberID = member.getId();
        String buttonID = button.getId();
        assert buttonID != null;

        String buttonLabel = button.getLabel();

        for (Game game : games) {

            if (!allowAction(channelID, roles, game)) continue;

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
                    }
                } else if (gameName.equals("Tic-Tac-Toe")) {
                    if (!moveAllowed(buttonLabel, game) || !yourTurn(game, member)) {
                        return;
                    } else {
                        makeMove(event, buttonLabel, game);
                    }
                }
            }
        }
    }

    private static boolean allowAction(String channelID, List<Role> roles, Game game) {

        JSONObject settings = (game.gameName.equals("Tic-Tac-Toe")) ? ticTacToeConfig : hangmanConfig;

        String guildID = game.guild.getId();
        JSONArray config = settings.getJSONArray(guildID);
        if (config == null) {
            return false;
        }

        // Check if this is the correct channel:
        String configChannelID = config.get(0).toString();
        String configRoleID = config.get(1).toString();
        if (!configChannelID.equals(channelID)) {
            return false;
        }

        // If the role is defined, check if the user has the role:
        if (!configRoleID.equals("null")) {
            for (Role role : roles) {
                if (role.getId().equals(configRoleID)) {
                    return true;
                }
            }
            // user does not have the role (send message)?
            return false;
        }

        return true;
    }

    private static boolean canClick(String memberID, String playerID, ButtonInteractionEvent event, String message) {
        if (!memberID.equals(playerID)) {
            event.reply(message).setEphemeral(true).queue();
            return true;
        }
        return false;
    }
}
