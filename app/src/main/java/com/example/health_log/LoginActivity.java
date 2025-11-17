package com.example.health_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup; // ✅ 이 줄이 추가되었습니다.
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextId;
    private EditText editTextPassword;
    private RadioGroup radioGroupUserType;
    private Button buttonLogin;
    private Button buttonSignUp;
    private TextView textViewFindCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.btnSignup);
        textViewFindCredentials = findViewById(R.id.txtFind);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (id.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedId = radioGroupUserType.getCheckedRadioButtonId();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (selectedId == R.id.radioUser) {
                    editor.putString("user_type", "user");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "유저 로그인 성공!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else if (selectedId == R.id.radioTrainer) {
                    editor.putString("user_type", "trainer");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "트레이너 로그인 성공!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "사용자 유형을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
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
                Intent intent = new Intent(LoginActivity.this, FindIdPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}