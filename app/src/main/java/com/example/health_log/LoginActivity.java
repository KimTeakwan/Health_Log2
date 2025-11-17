package com.example.health_log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button btnUserLogin;
    private Button btnTrainerLogin;
    private Button buttonSignUp;
    private TextView textViewFindCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnUserLogin = findViewById(R.id.btnUserLogin);
        btnTrainerLogin = findViewById(R.id.btnTrainerLogin);
        buttonSignUp = findViewById(R.id.btnSignup);
        textViewFindCredentials = findViewById(R.id.txtFind);

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });

        btnTrainerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, TrainerLoginActivity.class);
                startActivity(intent);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        textViewFindCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "아이디/비밀번호 찾기 화면으로 이동", Toast.LENGTH_SHORT).show();
                // In a real app, you would start a FindCredentialsActivity here
            }
        });
    }
}
