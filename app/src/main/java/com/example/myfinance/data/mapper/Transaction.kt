package com.example.myfinance.data.mapper

import com.example.myfinance.data.local.entity.TransactionEntity
import com.example.myfinance.domain.model.Transaction
import com.example.myfinance.domain.model.TypeOfOperation

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        title = title,
        description = description,
        amount = amount,
        type = type.name,
        date = date
    )
}

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        title = title,
        description = description,
        amount = amount,
        type = TypeOfOperation.valueOf(type),
        date = date
    )

}