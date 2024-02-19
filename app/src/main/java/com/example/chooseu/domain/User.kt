package com.example.chooseu.domain

import com.example.chooseu.data.database.models.UserEntity

data class User(
    val userName: String,
    val name: String,
)


fun User.toUserEntity(): UserEntity {
    return UserEntity(
        userName = this.userName,
        password = "",
        name = this.name,
        lastName = ""
    )
}