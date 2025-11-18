package com.example.health_log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 100;

    private ImageView profileImageView;
    private EditText nicknameEditText;
    private Button saveButton;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.edit_profile_image);
        nicknameEditText = findViewById(R.id.edit_nickname);
        saveButton = findViewById(R.id.btn_save_profile);

        // Get data from launching activity
        Intent intent = getIntent();
        String currentNickname = intent.getStringExtra("nickname");
        String currentImageUriString = intent.getStringExtra("imageUri");

        nicknameEditText.setText(currentNickname);
        if (currentImageUriString != null) {
            selectedImageUri = Uri.parse(currentImageUriString);
            profileImageView.setImageURI(selectedImageUri);
        }

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newNickname", nicknameEditText.getText().toString());
                if (selectedImageUri != null) {
                    resultIntent.putExtra("newImageUri", selectedImageUri.toString());
                }
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
        }
    }
}
