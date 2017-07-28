package com.architecture.component.util.common;

public class LoadMoreState {

    private final boolean running;
    private final String errorMessage;
    private boolean handledError = false;

    LoadMoreState(boolean running, String errorMessage) {
        this.running = running;
        this.errorMessage = errorMessage;
    }

    boolean isRunning() {
        return running;
    }

    String getErrorMessage() {
        return errorMessage;
    }

    String getErrorMessageIfNotHandled() {
        if (handledError) {
            return null;
        }
        handledError = true;
        return errorMessage;
    }
}