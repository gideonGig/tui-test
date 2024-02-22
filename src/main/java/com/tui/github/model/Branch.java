package com.tui.github.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Branch {
    private String name;
    private String lastCommitSha;
}
