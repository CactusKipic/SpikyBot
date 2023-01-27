package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.interaction.repository.PremiumServerRepository;
import fr.cactus_industries.database.schema.table.TGPremiumServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.Optional;

@Service
public class PremiumServeurServiceImpl implements PremiumServeurService{
    
    @Autowired
    private PremiumServerRepository repository;
    
    @Override
    public TGPremiumServer save(TGPremiumServer s) {
        return repository.save(s);
    }
    
    @Override
    public Optional<TGPremiumServer> findById(long id) {
        return repository.findById(id);
    }
    
    @Override
    public boolean serverIsPremium(long id) {
        return repository.serverIsPremium(id, new Date(Calendar.getInstance().getTimeInMillis()));
    }
}
