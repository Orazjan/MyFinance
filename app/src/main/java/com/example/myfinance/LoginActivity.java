//package com.example.myfinance;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentManager;
//
//import com.example.myfinance.fragments.LoginFragment;
//import com.example.myfinance.fragments.RegistrationFragment;
//
///**
// * Активность для входа и регистрации пользователя.
// */
//public class LoginActivity extends AppCompatActivity implements RegistrationFragment.OnRegSuccessListener, LoginFragment.LoginFragmentListener {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new RegistrationFragment())
//                    .commit();
//        }
//    }
//
//    /**
//     * Этот метод будет вызван, когда пользователь успешно зарегистрировался.
//     */
//    @Override
//    public void onRegSuccess() {
//        Toast.makeText(this, "Регистрация прошла успешна! Добро пожаловать", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//
//    /**
//     * Этот метод будет вызван, когда пользователь успешно вошел в систему.
//     */
//    @Override
//    public void onLoginSuccess() {
//        Toast.makeText(this, "Вход выполнен! Добро пожаловать", Toast.LENGTH_SHORT).show();
//        finish();
//    }
//
//    @Override
//    public void onSwitchToRegister() {
//        getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(
//                        android.R.anim.slide_in_left, android.R.anim.slide_out_right,
//                        android.R.anim.slide_in_left, android.R.anim.slide_out_right
//                )
//                .replace(R.id.fragment_container, new RegistrationFragment())
//                .addToBackStack(null) // Добавляем в стек, чтобы кнопка Назад вернула на вход
//                .commit();
//    }
//
//}