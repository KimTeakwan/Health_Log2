package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextName, editTextSignUpId, editTextSignUpEmail, editTextSignUpPassword, editTextConfirmPassword, editTextDob, editTextPhone;
    private RadioGroup radioGroupUserType;
    private Button buttonAttachFile;
    private Button buttonSubmitSignUp;
    private Uri fileUri;

    private static final int FILE_PICKER_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editTextName);
        editTextSignUpId = findViewById(R.id.editTextSignUpId);
        editTextSignUpEmail = findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        buttonAttachFile = findViewById(R.id.buttonAttachFile);
        editTextDob = findViewById(R.id.editTextDob);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSubmitSignUp = findViewById(R.id.buttonSubmitSignUp);

        radioGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonTrainer) {
                    buttonAttachFile.setVisibility(View.VISIBLE);
                } else {
                    buttonAttachFile.setVisibility(View.GONE);
                }
            }
        });

        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        buttonSubmitSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "파일이 선택되었습니다: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String signUpId = editTextSignUpId.getText().toString().trim();
        String email = editTextSignUpEmail.getText().toString().trim();
        String password = editTextSignUpPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String dob = editTextDob.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        int selectedUserTypeId = radioGroupUserType.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedUserTypeId);
        String userType = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(signUpId) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(dob) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserTypeId == R.id.radioButtonTrainer && fileUri == null) {
            Toast.makeText(this, "트레이너는 자격증 파일을 첨부해야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // In a real app, you would register the user here with all the details
        String summary = "회원가입 성공!\n" +
                "이름: " + name + "\n" +
                "아이디: " + signUpId + "\n" +
                "이메일: " + email + "\n" +
                "회원 유형: " + userType + "\n" +
                "생년월일: " + dob + "\n" +
                "핸드폰: " + phone;

        if (fileUri != null) {
            summary += "\n첨부파일: " + fileUri.getPath();
        }

        Toast.makeText(this, summary, Toast.LENGTH_LONG).show();
        finish(); // Go back to LoginActivity
    }
}