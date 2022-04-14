package de.bonndan.nivio.appuser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findByExternalIdAndIdProvider(String externalId, String idProvider);

}
