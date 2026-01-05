package com.example.babyone;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.babyone.utils.AuthHelper;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    // Initialize variables
    MaterialCardView btSignIn;
    ImageView imageViewLAB;
    TextInputEditText editTextUsername;
    TextInputEditText editTextPassword;
    TextView textViewRegister;
    int currentImageIndex = 0, oldImageIndex = 0;
    String role;

    int[] imgArr = new int[]{
            R.drawable.login_bg1,
            R.drawable.login_bg2,
            R.drawable.login_bg3,
            R.drawable.login_bg4
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // transparent status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        // Assign variables
        btSignIn = findViewById(R.id.bt_sign_in);
        imageViewLAB = findViewById(R.id.imageViewLAB);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textViewRegister = findViewById(R.id.textViewRegister);

        // Start the image loop
        startImageLoop();

        // Check if user is already logged in
        Map<String, Object> currentUser = AuthHelper.getCurrentUser(this);
        if (currentUser != null) {
            String userRole = (String) currentUser.get("role");
            redirectByRole(userRole);
            return;
        }

        // Login button click
        btSignIn.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                displayToast("Veuillez remplir tous les champs");
                return;
            }

            // Attempt login
            Map<String, Object> user = AuthHelper.login(this, username, password);
            if (user != null) {
                role = (String) user.get("role");
                displayToast("Connexion réussie");
                redirectByRole(role);
            } else {
                displayToast("Nom d'utilisateur ou mot de passe incorrect");
            }
        });

        // Register text click
        textViewRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SelectRole.class));
        });
    }

    private void redirectByRole(String role) {
        if (role == null) {
            startActivity(new Intent(LoginActivity.this, MainLanding.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        Intent intent;
        if (role.equals("D")) {
            intent = new Intent(LoginActivity.this, Doctor.class);
        } else if (role.equals("M")) {
            intent = new Intent(LoginActivity.this, MidWife.class);
        } else {
            intent = new Intent(LoginActivity.this, MainLanding.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startImageLoop() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create a TransitionDrawable to fade between the current and next image
                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {
                        ContextCompat.getDrawable(LoginActivity.this, imgArr[oldImageIndex]),
                        ContextCompat.getDrawable(LoginActivity.this, imgArr[currentImageIndex])
                });
                imageViewLAB.setImageDrawable(transitionDrawable);
                transitionDrawable.startTransition(500);

                // Increment the image index and wrap around if necessary
                oldImageIndex = currentImageIndex;
                currentImageIndex = (currentImageIndex + 1) % imgArr.length;

                // Schedule the next image loop iteration
                handler.postDelayed(this, 5000);
            }
        }, 0);
    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
