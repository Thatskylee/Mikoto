package org.Clover.Moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.Clover.Clover;
import org.Clover.Utilities.Data;
import org.Clover.Utilities.RoleCheck;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Clear extends ListenerAdapter {

    private final Clover clover;
    RoleCheck rc = new RoleCheck();
    Data data = new Data();
    EmbedBuilder eb = new EmbedBuilder();
    EmbedBuilder success = new EmbedBuilder();
    EmbedBuilder log = new EmbedBuilder();

    public Clear(Clover clover) {
        this.clover = clover;
    }

    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("clear")) return;
        if (rc.isOwner(event) || rc.isAdmin(event)) {
            if (event.getOptions().size() == 0) {
                eb.setDescription("You didn't specify enough arguments");
                eb.setColor(new Color(Data.failedRed));
                eb.setTimestamp(Instant.now());
                eb.setFooter("Insufficient Arguments", data.getSelfAvatar(event));

                event.replyEmbeds(eb.build()).queue((msg) -> {
                    eb.clear();
                    msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                });
            } else if (event.getOptions().size() == 1) {
                try {
                    Integer messageCount = Integer.parseInt(event.getOption("amount").getAsString());
                    if (messageCount < 2) {
                        eb.setDescription("Too few messages to delete. Minimum amount of messages I can delete is 2");
                        eb.setColor(new Color(Data.failedRed));
                        eb.setTimestamp(Instant.now());
                        eb.setFooter("Too Few Messages to Delete", data.getSelfAvatar(event));

                        event.replyEmbeds(eb.build()).queue((msg) -> {
                            eb.clear();
                            msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                        });
                    } else if (messageCount > 100) {
                        eb.setDescription("Maximum amount of messages I can delete at a time is 100");
                        eb.setColor(new Color(Data.failedRed));
                        eb.setTimestamp(Instant.now());
                        eb.setFooter("Too Many Messages to Delete", data.getSelfAvatar(event));

                        event.replyEmbeds(eb.build()).queue((msg) -> {
                            eb.clear();
                            msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                        });
                    } else {
                        List<Message> messages = event.getChannel().getHistory().retrievePast(messageCount).complete();
                        event.getChannel().purgeMessages(messages);

                        success.setDescription("Deleted " + messageCount.toString() + " messages from " + event.getChannel().getAsMention());
                        success.setColor(new Color(data.successGreen));
                        success.setTimestamp(Instant.now());
                        success.setFooter("Cleared Messages", event.getJDA().getSelfUser().getEffectiveAvatarUrl());

                        log.setDescription(event.getMember().getAsMention() + " deleted " + messageCount.toString() + " messages from: " + event.getChannel().getAsMention());
                        log.setColor(new Color(data.successGreen));
                        log.setTimestamp(Instant.now());
                        log.setFooter("Cleared Messages Log", data.getSelfAvatar(event));

                        event.replyEmbeds(success.build()).queue((msg) -> {
                            success.clear();
                            msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                            event.getGuild().getTextChannelCache().getElementById(clover.getGuildConfig().get("logChannel")).sendMessageEmbeds(log.build()).queue((msg2) -> {
                                log.clear();
                            });
                        });
                    }
                } catch (NumberFormatException nfe) {
                    eb.setDescription("`" + event.getOption("amount").getAsString() + "` is not a valid number. Please input a number between 2 and 100");
                    eb.setColor(new Color(Data.failedRed));
                    eb.setTimestamp(Instant.now());
                    eb.setFooter("Not a valid number", data.getSelfAvatar(event));

                    event.replyEmbeds(eb.build()).queue((msg) -> {
                        eb.clear();
                        msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                    });
                }
            }
        } else {
            eb.setDescription("You don't have permission to use that command.");
            eb.setColor(new Color(Data.failedRed));
            eb.setTimestamp(Instant.now());
            eb.setFooter("Insufficient Permissions", data.getSelfAvatar(event));

            event.replyEmbeds(eb.build()).queue((msg) -> {
                eb.clear();
                msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
            });
        }
    }
}