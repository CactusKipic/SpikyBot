package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TGServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

@Repository
public interface ServerRepository extends JpaRepository<TGServerEntity, Long> {
    
    @Transactional
    @Modifying
    @Query("DELETE FROM TGServerEntity AS tbl WHERE tbl.leaveDate < :leaveDate")
    Long deleteAllLeaveBefore(@Param("leaveDate") Date leaveDate);
}
