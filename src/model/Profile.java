package io.sproof.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model-Klasse fuer ein Sproof Profile
 */
@Data
@NoArgsConstructor
public class Profile extends AbstractSproofData {

    private String name;
    private String profileText;
    private String image;
    private String website;

}
