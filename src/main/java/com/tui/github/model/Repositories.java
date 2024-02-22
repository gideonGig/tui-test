package com.tui.github.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repositories {
    private List<Repository> repositories;
    private boolean isRepositoryHasNextPage;
}
