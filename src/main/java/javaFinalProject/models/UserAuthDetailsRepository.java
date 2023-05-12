package javaFinalProject.models;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface UserAuthDetailsRepository extends CrudRepository<UserAuthDetails, UUID> {
    UserAuthDetails findByUsername(String username);
}
