package fr.cactus_industries.listeners;

import fr.cactus_industries.commands.Commands;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.server.ServerChangeOwnerEvent;
import org.javacord.api.listener.server.ServerChangeOwnerListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SpikyOwnerChangeListener implements ServerChangeOwnerListener {
    
    /*private static final SpikyOwnerChangeListener listener = new SpikyOwnerChangeListener();
    
    public static SpikyOwnerChangeListener getInstance() {
        return listener;
    }*/
    
    public SpikyOwnerChangeListener() {
    }
    
    @Override
    public void onServerChangeOwner(ServerChangeOwnerEvent event) {
        Server server = event.getServer();
        User newOwner = event.getNewOwner().get();
        User oldOwner = event.getOldOwner().get();
        log.info("Le propriétaire du serveur "+server.getName()+" ("+server.getId()+") est passé de "
                +oldOwner.getName()+" ("+oldOwner.getId()+") à "+newOwner.getName()+" ("+newOwner.getId()+").");
        /*Commands.updatePermissionOfUser(server, newOwner, true);
        Commands.updatePermissionOfUser(server, oldOwner, false);*/
    }
}
