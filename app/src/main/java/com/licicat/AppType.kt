package com.licicat

object AppType {

    private var userType: UserType = UserType.UNKNOWN

    fun setUserType(type: UserType) {
        userType = type
    }

    fun getUserType(): UserType {
        return userType
    }
}

enum class UserType {
    UNKNOWN,
    EMPRESA,
    ENTITAT
}