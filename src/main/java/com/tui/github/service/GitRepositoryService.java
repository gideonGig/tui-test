package com.tui.github.service;

import com.tui.github.model.RepositoryModel;

public interface GitRepositoryService {
    RepositoryModel getUserRepository(String userName);
}
