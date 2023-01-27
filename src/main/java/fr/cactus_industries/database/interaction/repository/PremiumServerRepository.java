package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TGPremiumServer;
import fr.cactus_industries.database.schema.table.TGServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

public interface PremiumServerRepository extends JpaRepository<TGPremiumServer, Long> {
    
    @Query("SELECT CASE WHEN COUNT(tbl.server)>0 THEN true ELSE false END FROM TGPremiumServer AS tbl " +
            "WHERE tbl.server=:#{#server} AND tbl.endSub>:#{#today}")
    boolean serverIsPremium(@Param("server") long server,@Param("today") Date today);
}
