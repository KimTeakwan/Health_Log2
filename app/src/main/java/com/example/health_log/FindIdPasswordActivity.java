package com.example.health_log;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FindIdPasswordActivity extends AppCompatActivity {

    private RadioGroup radioGroupFind;
    private RadioButton radioFindId;
    private RadioButton radioFindPassword;
    private EditText editTextName;
    private EditText editTextEmail;
    private Button buttonFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_password);

        radioGroupFind = findViewById(R.id.radioGroupFind);
        radioFindId = findViewById(R.id.radioFindId);
        radioFindPassword = findViewById(R.id.radioFindPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonFind = findViewById(R.id.buttonFind);

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(FindIdPasswordActivity.this, "이름과 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedId = radioGroupFind.getCheckedRadioButtonId();
                if (selectedId == R.id.radioFindId) {
                    // Dummy ID find logic
                    Toast.makeText(FindIdPasswordActivity.this, "당신의 아이디는 'dummy_id' 입니다.", Toast.LENGTH_SHORT).show();
                } else if (selectedId == R.id.radioFindPassword) {
                    // Dummy password find logic
                    Toast.makeText(FindIdPasswordActivity.this, "비밀번호 재설정 링크를 이메일로 보냈습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
