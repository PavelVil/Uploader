package com.github.pavelvil.springboottest.model;

import com.github.pavelvil.springboottest.model.security.Authority;
import com.github.pavelvil.springboottest.model.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "usr")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"ownFiles", "userRoles", "sharedFiles"})
@ToString
public class User extends BaseModel implements UserDetails {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "owned", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> ownFiles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "shared_files",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private Set<File> sharedFiles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        userRoles.forEach(userRole -> authorities.add(new Authority(userRole.getRole().getName())));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addOwnFile(File file) {
        ownFiles.add(file);
    }

    public boolean ownFileExist(File file) {
        return ownFiles.contains(file);
    }

    public boolean fileIsPresent(File file) {
        return ownFiles.contains(file) || sharedFiles.contains(file);
    }

    public void addSharedFile(File file) {
        sharedFiles.add(file);
    }

    public Optional<File> getFile(File file) {
        Optional<File> optionalFile = ownFiles.stream().filter(f -> f.equals(file)).findFirst();

        if (!optionalFile.isPresent()) {
            optionalFile = sharedFiles.stream().filter(f -> f.equals(file)).findFirst();
        }

        return optionalFile;
    }

}
