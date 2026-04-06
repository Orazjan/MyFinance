package com.atnzvdev.data.local.entity

import com.atnzvdev.domain.model.Template
import com.atnzvdev.domain.model.TypeOfOperation

fun Template.toEntity(): TemplateEntity {
    return TemplateEntity(
        id = this.id,
        name = this.name,
        isIncome = this.isIncome,
        amount = this.amount,
        typeOfOperation = this.typeOfOperation.name,
        reminderTime = this.reminderTime,
        iconRes = this.iconRes,
        order = this.order
    )
}

fun TemplateEntity.toDomain(): Template {
    return Template(
        id = this.id,
        name = this.name,
        isIncome = this.isIncome,
        amount = this.amount,
        reminderTime = this.reminderTime,
        iconRes = this.iconRes,
        order = this.order,
        typeOfOperation = TypeOfOperation.valueOf(this.typeOfOperation)
    )
}