package de.bonndan.nivio.landscape;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {


    List<Service> findAllByIdentifier(String identifier);
    List<Service> findAllByLandscape(Landscape landscape);
    List<Service> findAllByLandscapeAndGroup(Landscape landscape, String group);

}
