package de.bonndan.nivio.landscape;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {


    List<Service> findAllByLandscape(Landscape landscape);
    List<Service> findAllByLandscapeAndGroup(Landscape landscape, String group);


    Optional<Service> findByLandscapeAndGroupAndIdentifier(Landscape landscape, String group, String identifier);

    //TODO this is ugly, removing services in indexer does not work otherwise
    @Modifying
    @Query("delete from Service s where s.id = ?1")
    @Transactional
    void delete(Long id);
}
