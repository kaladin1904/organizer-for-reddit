package javaFinalProject.models;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedUser {

    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userId;

    @Id
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
}

// TODO - store encrypted passwords, not plaintext