package fr.cactus_industries.tools.tickets;

import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class TicketsMessageListener implements MessageCreateListener {
    
    private static final HashMap<ServerTextChannel, Timer> timers = new HashMap<>();
    
    private final TicketsLogicMessage ticketsLogicMessage;
    
    public TicketsMessageListener(TicketsLogicMessage ticketsLogicMessage) {
        this.ticketsLogicMessage = ticketsLogicMessage;
    }
    
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        ServerTextChannel channel = messageCreateEvent.getServerTextChannel().get();
        if(messageCreateEvent.getMessage().getAuthor().isYourself() && timers.get(channel) != null)
            return;
        if(timers.get(channel) != null){
            timers.get(channel).cancel();
            timers.get(channel).purge();
        }
        Timer timer = new Timer();
        timer.schedule(new UpdateTicket(channel, ticketsLogicMessage), (15*1000));
        timers.put(channel, timer);
    }
    
    private static class UpdateTicket extends TimerTask {
        private final ServerTextChannel channel;
        private final TicketsLogicMessage ticketsLogicMessage;
    
        public UpdateTicket(ServerTextChannel channel, TicketsLogicMessage ticketsLogicMessage) {
            this.channel = channel;
            this.ticketsLogicMessage = ticketsLogicMessage;
        }
    
        @Override
        public void run() {
            ticketsLogicMessage.updateTicket(this.channel);
            Timer timer = timers.get(this.channel);
            timer.cancel();
            timer.purge();
            timers.remove(this.channel);
        }
    }
}
