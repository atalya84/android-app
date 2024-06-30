package com.example.newsflow.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey var email: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "password") var password: String,
//    @ColumnInfo(name = "imageUrl") var imageUrl: String
)

data class FirestoreUser(
    val name: String = "",
    val email: String = "",
    val password: String = "",
//    val imageUrl: String = "",
)

fun FirestoreUser.toRoomUser(id: String): User {
    return User(
        name = this.name,
        email = this.email,
        password = this.password,
//        imageUrl = this.imageUrl
    )
}

fun User.toFirestoreUser(): FirestoreUser {
    return FirestoreUser(
        name = this.name,
        email = this.email,
        password = this.password,
//        imageUrl = this.imageUrl
    )
}