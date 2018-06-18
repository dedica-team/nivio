package de.bonndan.nivio.landscape;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {

    List<Service> findAllByLandscape(Landscape landscape);

    List<Service> findAllByLandscapeAndType(Landscape landscape, String type);
}
