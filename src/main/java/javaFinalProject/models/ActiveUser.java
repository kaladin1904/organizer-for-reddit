package javaFinalProject.models;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="active_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveUser {

    @Id
    @Column(nullable = false)
    private String username;
    
}
