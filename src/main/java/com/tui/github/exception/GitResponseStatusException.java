package com.tui.github.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class GitResponseStatusException extends ResponseStatusException {
    private final String _message;
    private final HttpStatus _status;

    public GitResponseStatusException(HttpStatus status, String message) {
        super(status, message);
        this._message = message;
        this._status = status;

    }

    @Override
	public String getMessage() {
		return _message;
	}

    public HttpStatus getStatus() {
        return _status;
    }
}
