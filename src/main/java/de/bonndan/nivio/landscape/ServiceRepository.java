package de.bonndan.nivio.landscape;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {


    List<Service> findAllByLandscape(Landscape landscape);
    List<Service> findAllByLandscapeAndGroup(Landscape landscape, String group);


    Optional<Service> findByLandscapeAndGroupAndIdentifier(Landscape landscape, String group, String identifier);
}
