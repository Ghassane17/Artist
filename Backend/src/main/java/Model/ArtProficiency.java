package Model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ArtProficiency {
    private String artName;        // e.g., "Painting"
    private String proficiencyLevel; // e.g., "EXPERT"
    
    
    // getters and setters...
    
    public String getArtistRole() {
        return artName + " (" + proficiencyLevel + ")" ;
    }
}