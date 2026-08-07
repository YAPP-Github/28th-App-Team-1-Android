package com.dminus14.app.domain.model

/**
 * 답변 제출 결과.
 */
data class SubmitAnswerResult(
    val answerId: Long?,
    val nextQuestion: NextQuestion?,
    val sessionEnded: Boolean,
    val wrapUpMessage: WrapUpMessage?,
    val endType: String?,
)

/**
 * 다음 면접 질문 정보.
 */
data class NextQuestion(
    val questionId: Long,
    val isLast: Boolean,
    val turn: QuestionTurn,
)

/**
 * 질문 턴 단계 정보.
 */
data class QuestionTurn(
    val turnLevel: Int,
    val depthLevel: Int,
)

/**
 * 면접 마무리 멘트.
 */
data class WrapUpMessage(
    val ttsAudio: String?,
)
