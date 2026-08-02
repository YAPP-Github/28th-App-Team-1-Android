package com.dminus14.app.feature.login.onboarding

internal const val ONBOARDING_NICKNAME_MAX_LENGTH = 5

internal const val ONBOARDING_NICKNAME_CONSTRAINT_TEXT = "한글·영문 혼용 가능, 최대 5글자"

private val nicknameAllowedCharRegex = Regex("[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]")

internal fun sanitizeOnboardingNickname(input: String): String =
    input
        .filter { char -> nicknameAllowedCharRegex.matches(char.toString()) }
        .take(ONBOARDING_NICKNAME_MAX_LENGTH)

internal fun isValidOnboardingNickname(name: String): Boolean = sanitizeOnboardingNickname(name) == name && name.isNotBlank()
