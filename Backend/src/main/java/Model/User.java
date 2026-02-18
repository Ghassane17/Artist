package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max=50)
    @Email
    @Column(unique = true)  
    private String email;
    
    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 10)
    private String firstName;

    @NotBlank
    @Size(max = 10)
    private String lastName;

    @NotBlank
    @Size(max = 20)
    @Column(unique = true)
    private String username;
   
    private String profilePictureUrl ; 
    @NotBlank
    @Size(max = 300)
    private String bio ; 

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

        @ElementCollection
    @CollectionTable(name = "user_art_proficiencies", 
                     joinColumns = @JoinColumn(name = "user_id"))
    private ArrayList<ArtProficiency> artistRoles = new ArrayList<>();

  

    public User() {
    }

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles(){
        return Collections.unmodifiableSet(roles); //set map hash issue typing
    }

    public void addRole(Role role){
        if (role != null){
            this.roles.add(role) ; 
        }
    }

    public void removeRole(Role role) throws IllegalArgumentException { 
        if (role != null) {
            // Prevent removing the last role - user must always have at least one role
            if (this.roles.size() <= 1) {
                throw new IllegalArgumentException("User must have at least one role. Cannot remove the last role.");
            }
            this.roles.remove(role);
        }
    }

    public ArrayList<ArtProficiency> getArtistRoles() {
        return artistRoles;
    }

 public void addArt(ArtProficiency art){
        if (art != null){
            this.artistRoles.add(art) ; 
        }
    }

    public void removeArt(ArtProficiency art) throws IllegalArgumentException { 
        if (art != null) {
            // Prevent removing the last role - user must always have at least one role
            if (this.artistRoles.size() <= 1) {
                throw new IllegalArgumentException("Artist must have at least one role. Cannot remove the last role.");
            }
            this.artistRoles.remove(art);
        }
    }


private boolean isAdmin() {
    return roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_ADMIN);
}

private boolean isModerator() {
    return roles.stream().anyMatch(r -> r.getName() == ERole.ROLE_MODERATOR);
}

    public String getProfilePictureUrl(){
        return profilePictureUrl ; 
    }

    public void setProfilePictureUrl(String profilePictureUrl){
        this.profilePictureUrl = profilePictureUrl ; 
    }
    public String getBio(){
        return bio ; 
    }

    public void setBio(String bio){
        this.bio = bio ; 
    }


    
}



