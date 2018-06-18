package de.bonndan.nivio.landscape;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandscapeRepository extends CrudRepository<Landscape, Long> {

    Landscape findDistinctByIdentifier(String identifier);
}