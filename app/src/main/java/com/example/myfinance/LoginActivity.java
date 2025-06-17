package com.example.myfinance;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.myfinance.Fragments.LoginFragment;
import com.example.myfinance.Fragments.RegistrationFragment;

/**
 * Активность для входа и регистрации пользователя.
 */
public class LoginActivity extends AppCompatActivity implements RegistrationFragment.OnRegSuccessListener, LoginFragment.OnLoginSuccessListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegistrationFragment())
                    .commit();
        }
    }

    /**
     * Этот метод будет вызван, когда пользователь успешно зарегистрировался.
     */
    @Override
    public void onRegSuccess() {
        Toast.makeText(this, "Регистрация прошла успешна! Добро пожаловать", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Этот метод будет вызван, когда пользователь успешно вошел в систему.
     */
    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Вход выполнен! Добро пожаловать", Toast.LENGTH_SHORT).show();
        finish();
    }


    /**
     * Обработка нажатия кнопки "Назад".
     * Если это LoginActivity и она содержит только LoginFragment (без Back Stack),
     * то нажатие "Назад" приведет к закрытию активности.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}