package com.soland.ap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class EventModel {

    private final String eventTag;

    private EventListener listener;

    public EventModel(String eventTag,EventListener listener) {
        this.eventTag = eventTag;
        this.listener = listener;
    }
    public void callEvent(Player player, String[] data) {
        listener.event(player,data);
    }

    public String getEventTag() {
        return eventTag;
    }
}
