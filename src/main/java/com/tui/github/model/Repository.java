package com.tui.github.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repository {
    public String name;
    public String ownerLogin;
    public List<Branch> branches;
}
