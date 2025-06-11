package com.omnia.core.resilience.exception;

import org.springframework.boot.ExitCodeGenerator;

public class ExitException extends RuntimeException implements ExitCodeGenerator {

    public ExitException(String msg) {
        super(msg);
    }

    public ExitException(String msg, Throwable cause) {
        super(msg, cause);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}
