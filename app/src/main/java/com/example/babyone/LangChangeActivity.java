package com.example.babyone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class LangChangeActivity extends AppCompatActivity {

    Button btnEnglish, btnFrench, btnArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang_change);

        btnEnglish = findViewById(R.id.btnSetLangEN);
        btnFrench = findViewById(R.id.btnSetLangFR);
        btnArabic = findViewById(R.id.btnSetLangAR);

        GlobalFunctions locale = new GlobalFunctions(getApplicationContext(),this);

        btnEnglish.setOnClickListener(v -> {
            locale.setLocale("en");
            locale.getLocale();
            finish();
        });

        btnFrench.setOnClickListener(v -> {
            locale.setLocale("fr");
            locale.getLocale();
            finish();
        });

        btnArabic.setOnClickListener(v -> {
            locale.setLocale("ar");
            locale.getLocale();
            finish();
        });
    }
}