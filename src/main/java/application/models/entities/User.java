package application.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.base.BaseEntity;
import core.utils.Searchable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Table(name = "users")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false, unique = true)
    @Searchable
    private String      email;

    @Column(nullable = false)
    @JsonIgnore
    private String      password;

    private String      avatarUrl;

    @Column(name = "enabled")
    @Builder.Default
    private boolean     enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Topic.class)
    @Builder.Default
    private List<Topic> topics = new ArrayList<>();
}
