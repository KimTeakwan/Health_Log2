package com.example.health_log;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TrainerLoginActivity extends AppCompatActivity {

    private EditText editTextId;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_login);

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(TrainerLoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // For now, any input is considered a successful login
                    Toast.makeText(TrainerLoginActivity.this, "트레이너 로그인 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TrainerLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        findViewById(R.id.txtFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainerLoginActivity.this, FindIdPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
