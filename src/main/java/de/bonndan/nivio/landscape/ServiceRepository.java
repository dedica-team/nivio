package de.bonndan.nivio.landscape;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends CrudRepository<Service, Long> {

    List<Service> findAllByEnvironment(String identifier);

    List<Service> findAllByEnvironmentAndType(String identifier, String infrastructure);
}
