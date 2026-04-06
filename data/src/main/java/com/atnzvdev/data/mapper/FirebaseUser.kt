package com.atnzvdev.data.mapper

import com.atnzvdev.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): User {
    return User(
        id = this.uid,
        displayName = this.displayName,
        email = this.email,
        isAnonymous = this.isAnonymous
    )

}