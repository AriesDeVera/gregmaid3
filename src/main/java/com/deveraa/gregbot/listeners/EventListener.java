package com.deveraa.gregbot.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw(); // Stores the message in a string variable
        if (message.contains("jonmar")) {
            event.getChannel().sendMessage("likes boys uwu").queue();
        } else if (message.contains("fritz is")) {
            event.getChannel().sendMessage("zesty papi ;)").queue();
        }
    }
}
