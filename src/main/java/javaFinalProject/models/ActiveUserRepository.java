package javaFinalProject.models;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveUserRepository extends CrudRepository<ActiveUser, UUID> {

    ActiveUser findByUsername(String username);
    
}
