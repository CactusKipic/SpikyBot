package fr.cactus_industries.database.interaction.repository;

import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntity;
import fr.cactus_industries.database.schema.table.TPDFReadingChannelEntityPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;

import java.util.List;

public interface PDFReadingChannelRepository extends JpaRepository<TPDFReadingChannelEntity, TPDFReadingChannelEntityPK> {
    
    @Query("SELECT tbl FROM TPDFReadingChannelEntity AS tbl WHERE tbl.server=:#{#server}")
    List<TPDFReadingChannelEntity> findAllServer(@Param("server") long server);
    
    @Override
    @Query("SELECT tbl FROM TPDFReadingChannelEntity AS tbl ORDER BY tbl.server")
    List<TPDFReadingChannelEntity> findAll();
}
