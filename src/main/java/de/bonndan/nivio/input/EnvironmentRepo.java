package de.bonndan.nivio.input;

import org.springframework.data.repository.CrudRepository;

public interface EnvironmentRepo extends CrudRepository<Environment, Long> {

    Environment findDistinctByIdentifier(String identifier);
}