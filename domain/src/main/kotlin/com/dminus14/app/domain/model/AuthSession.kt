package com.dminus14.app.domain.model

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
)
