package com.ercanbeyen.movieapplication.entity;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "audiences")
public non-sealed class Audience extends Base implements UserDetails {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Setter
    @Column(unique = true, length = 100)
    private String username;
    @Setter
    private String password;
    @Setter
    @Getter
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}
    )
    @JoinTable(
            name = "audiences_roles",
            joinColumns = {@JoinColumn(name = "audience_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles;
    @Setter
    @Getter
    @OneToMany(mappedBy = "audience", cascade = CascadeType.MERGE)
    private List<Rating> ratings = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String toString() {
        List<Integer> ratingIdList = ratings.stream()
                .map(Rating::getId)
                .toList();

        List<RoleName> roleNameList = roles.stream()
                .map(Role::getRoleName)
                .toList();

        return "Audience{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", roles=" + roleNameList +
                ", ratings=" + ratingIdList +
                '}';
    }
}
