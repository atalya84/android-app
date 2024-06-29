package com.example.newsflow.data.database.posts

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsflow.data.models.Post

@Database(entities = [Post::class], version = 2, exportSchema = false)
abstract class PostDatabase: RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {

        @Volatile
        private var INSTANCE: PostDatabase? = null

        fun getDatabase(context: Context): PostDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    "posts_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}