package com.example.eternotev2.data.repository

import com.example.eternotev2.data.local.dao.UserDao
import com.example.eternotev2.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    fun getUserByEmailFlow(email: String): Flow<UserEntity?> {
        return userDao.getUserByEmailFlow(email)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }
}
