package com.example.health_log;

import androidx.annotation.StringRes;

/**
 * A wrapper class for the result of a login attempt.
 */
public class LoginResult {
    private final boolean isSuccess;
    @StringRes
    private final int messageResId;

    public LoginResult(boolean isSuccess, @StringRes int messageResId) {
        this.isSuccess = isSuccess;
        this.messageResId = messageResId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    @StringRes
    public int getMessageResId() {
        return messageResId;
    }
}
