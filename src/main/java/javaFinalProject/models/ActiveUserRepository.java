package javaFinalProject.models;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface ActiveUserRepository extends CrudRepository<ActiveUser, UUID> {

    ActiveUser findByUsername(String username);
    
}
