package com.example.newsflow.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "posts")
data class Post (
    @PrimaryKey var id: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var desc: String,
    @ColumnInfo(name = "imageUrl") var imageUrl: String,
    @ColumnInfo(name = "articleUrl") var articleUrl: String,
    @ColumnInfo(name = "createdString") var createdString: String,
    @ColumnInfo(name = "userId") var userId: String,
)

data class FirestorePost(
    val title: String = "",
    val desc: String = "",
    val imageUrl: String = "",
    val articleUrl: String = "",
    val createdString: String = "",
    val userId: String = ""
)

fun FirestorePost.toRoomPost(id: String): Post {
    return Post(
        id = id,
        title = this.title,
        desc = this.desc,
        imageUrl = this.imageUrl,
        articleUrl = this.articleUrl,
        createdString = this.createdString,
        userId = this.userId
    )
}

fun Post.toFirestorePost(): FirestorePost {
    return FirestorePost(
        title = this.title,
        desc = this.desc,
        imageUrl = this.imageUrl,
        articleUrl = this.articleUrl,
        createdString = this.createdString,
        userId = this.userId
    )
}