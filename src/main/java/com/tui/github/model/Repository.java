package com.tui.github.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repository {
    private String name;
    private String ownerLogin;
    private Branches branches;
}
