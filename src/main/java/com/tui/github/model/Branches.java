package com.tui.github.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Branches {
    private List<Branch> branches;
    private boolean isBranchHasNextPage;
}
