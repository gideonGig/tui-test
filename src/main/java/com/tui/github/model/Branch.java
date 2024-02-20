package com.tui.github.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Branch {
    public String name;
    public String lastCommitSha;
}
