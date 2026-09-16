package com.example.eternotev2.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "username")
    val username: String,
    
    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
