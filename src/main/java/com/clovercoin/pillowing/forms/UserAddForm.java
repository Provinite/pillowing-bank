package com.clovercoin.pillowing.forms;

import lombok.Data;

@Data
public class UserAddForm {
    private String email;
    private String username;
    private String password;

    private Boolean isActive = true;

    private Boolean isAdmin;
    private Boolean isMod;
    private Boolean isGuestArtist;
}
