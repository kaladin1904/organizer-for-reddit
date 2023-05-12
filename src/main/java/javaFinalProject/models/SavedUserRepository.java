package javaFinalProject.models;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface SavedUserRepository extends CrudRepository<SavedUser, UUID> {
    SavedUser findByUsername(String username);
}
