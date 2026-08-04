package com.dminus14.app.data.remote.dto.interview

import com.dminus14.app.domain.model.NextQuestion
import com.dminus14.app.domain.model.QuestionTurn
import com.dminus14.app.domain.model.SubmitAnswerResult
import com.dminus14.app.domain.model.WrapUpMessage
import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/interview/sessions/{sessionId}/answers
 */
data class SubmitAnswerRequestDto(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("isWrapUp")
    val isWrapUp: Boolean,
    @SerializedName("questionAudioStartAt")
    val questionAudioStartAt: Float? = null,
    @SerializedName("questionAudioEndAt")
    val questionAudioEndAt: Float? = null,
    @SerializedName("answerStartAt")
    val answerStartAt: Float? = null,
    @SerializedName("answerEndAt")
    val answerEndAt: Float? = null,
    @SerializedName("answerDuration")
    val answerDuration: Float? = null,
    @SerializedName("endType")
    val endType: String? = null,
)

data class SubmitAnswerResponseDto(
    @SerializedName("answerId")
    val answerId: Long? = null,
    @SerializedName("nextQuestion")
    val nextQuestion: NextQuestionDto? = null,
    @SerializedName("sessionEnded")
    val sessionEnded: Boolean,
    @SerializedName("wrapUpMessage")
    val wrapUpMessage: WrapUpMessageDto? = null,
    @SerializedName("endType")
    val endType: String? = null,
) {
    fun toDomain(): SubmitAnswerResult =
        SubmitAnswerResult(
            answerId = answerId,
            nextQuestion = nextQuestion?.toDomain(),
            sessionEnded = sessionEnded,
            wrapUpMessage = wrapUpMessage?.toDomain(),
            endType = endType,
        )
}

data class NextQuestionDto(
    @SerializedName("questionId")
    val questionId: Long,
    @SerializedName("isLast")
    val isLast: Boolean,
    @SerializedName("turn")
    val turn: QuestionTurnDto,
) {
    fun toDomain(): NextQuestion =
        NextQuestion(
            questionId = questionId,
            isLast = isLast,
            turn = turn.toDomain(),
        )
}

data class QuestionTurnDto(
    @SerializedName("turnLevel")
    val turnLevel: Int,
    @SerializedName("depthLevel")
    val depthLevel: Int,
) {
    fun toDomain(): QuestionTurn =
        QuestionTurn(
            turnLevel = turnLevel,
            depthLevel = depthLevel,
        )
}

data class WrapUpMessageDto(
    @SerializedName("ttsAudio")
    val ttsAudio: String? = null,
) {
    fun toDomain(): WrapUpMessage =
        WrapUpMessage(
            ttsAudio = ttsAudio,
        )
}
