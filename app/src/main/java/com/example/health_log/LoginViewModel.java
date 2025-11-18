package com.example.health_log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginResult> _loginResult = new MutableLiveData<>();
    public LiveData<LoginResult> loginResult = _loginResult;

    public void login(String id, String password, int userTypeId, int userRadioId, int trainerRadioId) {
        if (id == null || id.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            _loginResult.setValue(new LoginResult(false, R.string.enter_id_and_password));
            return;
        }

        if (userTypeId == userRadioId) {
            // Placeholder for actual user login logic
            _loginResult.setValue(new LoginResult(true, R.string.user_login_success));
        } else if (userTypeId == trainerRadioId) {
            // Placeholder for actual trainer login logic
            _loginResult.setValue(new LoginResult(true, R.string.trainer_login_success));
        } else {
            _loginResult.setValue(new LoginResult(false, R.string.select_user_type));
        }
    }
}
