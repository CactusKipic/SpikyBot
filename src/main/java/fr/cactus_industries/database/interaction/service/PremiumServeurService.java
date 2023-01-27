package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.schema.table.TGPremiumServer;

import java.util.Optional;

public interface PremiumServeurService {
    
    TGPremiumServer save(TGPremiumServer s);
    
    Optional<TGPremiumServer> findById(long id);
    
    boolean serverIsPremium(long id);
}
