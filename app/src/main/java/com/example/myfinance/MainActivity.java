package com.example.myfinance;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myfinance.Fragments.AnalizFragment;
import com.example.myfinance.Fragments.MainFragment;
import com.example.myfinance.Fragments.ProfileFragment;
import com.example.myfinance.Prevalent.StatusBarColorHelper;

public class MainActivity extends AppCompatActivity {

    private Button btnAnaliz, btnMain, btnProfile;
    private Button currentSelectedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarColorHelper.setStatusBarColorFromPrimaryVariant(this);

        btnAnaliz = findViewById(R.id.btnAnaliz);
        btnMain = findViewById(R.id.btnMain);
        btnProfile = findViewById(R.id.btnProfile);

        btnAnaliz.setOnClickListener(v -> selectFragment(new AnalizFragment(), btnAnaliz));
        btnMain.setOnClickListener(v -> selectFragment(new MainFragment(), btnMain));
        btnProfile.setOnClickListener(v -> selectFragment(new ProfileFragment(), btnProfile));

        if (savedInstanceState == null) {
            selectFragment(new MainFragment(), btnMain);
        }
    }

    private void selectFragment(Fragment fragment, Button selectedButton) {
        if (currentSelectedButton != null) {
            currentSelectedButton.setSelected(false);
        }

        selectedButton.setSelected(true);
        currentSelectedButton = selectedButton;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}