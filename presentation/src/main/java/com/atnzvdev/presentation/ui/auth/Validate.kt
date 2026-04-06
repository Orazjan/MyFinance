package com.atnzvdev.presentation.ui.auth

import android.util.Patterns
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Validate @Inject constructor() {
    fun validateEmail(email: String): ValidationResult {
        return if (email.isEmpty()) ValidationResult.Error("Email не может быть пустым")
        else if (!Patterns.EMAIL_ADDRESS.matcher(email)
                .matches()
        ) ValidationResult.Error("Некорректный формат почты")
        else ValidationResult.Success

    }

    fun validatePassword(password: String): ValidationResult {
        return if (password.isEmpty()) ValidationResult.Error("Пароль не может быть пустым")
        else if (password.length < 8) ValidationResult.Error("Пароль не должен быть меньше 8 знаков")
        else if (password.equals("Qwerty")) ValidationResult.Error("Пароль не должен быть $password")
        else ValidationResult.Success
    }

    fun validateName(name: String): ValidationResult {
        return if (name.isEmpty()) ValidationResult.Error("Имя не может быть пустым")
        else if (name.length < 2) ValidationResult.Error("Имя не может быть меньше 2х символов")
        else ValidationResult.Success
    }

    fun validateAuth(email: String, password: String): ValidationResult {
        val emailResult = validateEmail(email)
        if (emailResult is ValidationResult.Error) return emailResult

        val passwordResult = validatePassword(password)
        if (passwordResult is ValidationResult.Error) return passwordResult

        return ValidationResult.Success
    }

    fun validateRegistration(email: String, password: String, userName: String): ValidationResult {
        val emailResult = validateEmail(email)
        if (emailResult is ValidationResult.Error) return emailResult

        val username = validateName(userName)
        if (username is ValidationResult.Error) return username

        val passwordResult = validatePassword(password)
        if (passwordResult is ValidationResult.Error) return passwordResult

        return ValidationResult.Success
    }

}