package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.interaction.repository.ServerRepository;
import fr.cactus_industries.database.schema.table.TGServerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServerServiceImpl implements ServerService{
    @Autowired
    private ServerRepository repository;
    
    @Override
    public TGServerEntity save(TGServerEntity s) {
        return repository.save(s);
    }
    
    @Override
    public List<TGServerEntity> findAll() {
        return repository.findAll();
    }
    
    @Override
    public Optional<TGServerEntity> findById(long id) {
        return repository.findById(id);
    }
    
    @Override
    public Long deleteAllLeaveBefore(Date leaveDate) {
        return repository.deleteAllLeaveBefore(leaveDate);
    }
}
