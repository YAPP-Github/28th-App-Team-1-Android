package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.UserProfileUpdate
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import javax.inject.Inject

/** 회원 프로필 수정 입력을 검증하고 정규화한 뒤 저장소에 전달한다. */
class UpdateUserProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(update: UserProfileUpdate): Result<Unit> =
            runCatchingCancellable {
                val normalizedName = update.name.trim()
                validate(
                    normalizedName.length in NAME_LENGTH_RANGE,
                    "이름은 1자 이상 5자 이하로 입력해 주세요.",
                )
                val normalizedJobRole = update.jobRole.trim()
                validate(normalizedJobRole.isNotEmpty(), "직무를 입력해 주세요.")
                validate(update.careerYears in CAREER_YEARS_RANGE, "연차는 0년 이상 10년 이하로 입력해 주세요.")

                userRepository.updateUserProfile(
                    update.copy(
                        name = normalizedName,
                        jobRole = normalizedJobRole,
                    ),
                )
            }

        private fun validate(
            condition: Boolean,
            message: String,
        ) {
            if (!condition) {
                throw ValidationException(
                    errCode = VALIDATION_ERROR_CODE,
                    message = message,
                )
            }
        }

        private companion object {
            const val VALIDATION_ERROR_CODE = "VALIDATION_ERROR"
            val NAME_LENGTH_RANGE = 1..5
            val CAREER_YEARS_RANGE = 0..10
        }
    }
