package kr.co.korbit.fisherman.pushserver.service

import kr.co.korbit.fisherman.pushserver.model.User

class AuthService() {

    @Throws(Exception::class)
    fun authenticateAccessToken(token: String?): User {
        return User("email", "password")
    }
}