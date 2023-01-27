package fr.cactus_industries.database.interaction.service;

import fr.cactus_industries.database.schema.table.TGServerEntity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ServerService {
    
    TGServerEntity save(TGServerEntity s);
    
    List<TGServerEntity> findAll();
    
    Optional<TGServerEntity> findById(long id);
    
    Long deleteAllLeaveBefore(Date leaveDate);
}
