package com.example.health_log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.health_log.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.loginResult.observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.isSuccess()) {
                Toast.makeText(getApplicationContext(), loginResult.getMessageResId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), loginResult.getMessageResId(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonLogin.setOnClickListener(v -> {
            String id = binding.editTextId.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            int selectedId = binding.radioGroupUserType.getCheckedRadioButtonId();
            loginViewModel.login(id, password, selectedId, R.id.radioUser, R.id.radioTrainer);
        });

        binding.btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        binding.txtFind.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, FindIdPasswordActivity.class);
            startActivity(intent);
        });
    }
}
