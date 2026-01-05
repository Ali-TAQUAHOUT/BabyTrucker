package com.example.babyone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.babyone.utils.AuthHelper;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextName;
    private MaterialButton btnRegister;
    private TextView textViewLogin;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get role from intent
        role = getIntent().getStringExtra("role");
        if (role == null) {
            role = "G"; // Default to Guardian
        }

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        btnRegister = findViewById(R.id.btnRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Register button click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Login text click
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String name = editTextName.getText().toString().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            email.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user
        boolean success = AuthHelper.registerUser(this, username, password, email, role, name, null);
        if (success) {
            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
            
            // Auto login after registration
            com.example.babyone.utils.AuthHelper.login(this, username, password);
            
            // Redirect based on role
            Intent intent;
            if (role.equals("D")) {
                intent = new Intent(RegisterActivity.this, DoctorRegistration.class);
            } else if (role.equals("M")) {
                intent = new Intent(RegisterActivity.this, MidWifeRegistration.class);
            } else {
                intent = new Intent(RegisterActivity.this, FirstTimeGuardian.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Erreur: Le nom d'utilisateur existe déjà", Toast.LENGTH_SHORT).show();
        }
    }
}



