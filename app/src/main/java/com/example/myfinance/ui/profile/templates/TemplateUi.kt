package com.example.myfinance.ui.profile.templates

import com.example.myfinance.domain.model.Templates

data class TemplateUi(
    val id: Int, val name: String, val amount: Double, val isIncome: Boolean
)

fun Templates.toUi(): TemplateUi = TemplateUi(
    id = id, name = name, amount = amount, isIncome = isIncome
)