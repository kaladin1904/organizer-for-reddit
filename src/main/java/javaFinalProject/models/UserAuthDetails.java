package javaFinalProject.models;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@EntityScan
@Table(name="user_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDetails {

    @Id
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Lob
    private String accessToken;

    @Column(nullable = false)
    @Lob
    private String refreshToken;
}
