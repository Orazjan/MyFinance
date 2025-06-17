package com.example.myfinance;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myfinance.Fragments.LoginFragment;
import com.example.myfinance.Fragments.RegistrationFragment;

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

    @Override
    public void onRegSuccess() {
        // Логика, что делать после успешной регистрации
        Toast.makeText(this, "Регистрация успешна в LoginActivity!", Toast.LENGTH_SHORT).show();
        finish(); // Например, закрыть LoginActivity и вернуться на предыдущий экран
    }

    /**
     * Этот метод будет вызван, когда пользователь успешно вошел в систему.
     */
    @Override
    public void onLoginSuccess() {
        finish();
    }

    /**
     * Загружает фрагмент LoginFragment в контейнер активности.
     *
     * @param fragment Экземпляр LoginFragment для загрузки.
     */
    private void loadLoginFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
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