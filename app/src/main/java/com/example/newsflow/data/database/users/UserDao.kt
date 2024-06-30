package com.example.newsflow.data.database.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.example.newsflow.data.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email")
    fun get(email: String): User

    @Update
    suspend fun update(post: User)

//    @Delete
//    fun delete(id: String)
}