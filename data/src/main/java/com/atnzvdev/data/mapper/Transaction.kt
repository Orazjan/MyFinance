package com.atnzvdev.data.mapper

import com.atnzvdev.data.local.entity.TransactionEntity
import com.atnzvdev.domain.model.Transaction
import com.atnzvdev.domain.model.TypeOfOperation

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