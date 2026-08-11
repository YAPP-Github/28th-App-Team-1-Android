package com.dminus14.app.domain.model

/** 답변 제출 또는 면접 종료 요청 유형이다. */
enum class InterviewAnswerEndRequest {
    Skip,
    ManualEnd,
    HardCap,
    BackExit,
}

/** 서버가 확정한 면접 종료 유형이다. */
sealed interface InterviewEndType {
    data object NormalEnd : InterviewEndType

    data object ManualEnd : InterviewEndType

    data object HardCap : InterviewEndType

    data object BackExit : InterviewEndType

    data object SttReset : InterviewEndType

    data class Unknown(
        val rawValue: String,
    ) : InterviewEndType
}

/** 하나의 논리 답변 제출을 재시도할 때 그대로 보존하는 명령이다. */
data class SubmitInterviewAnswerCommand(
    val sessionId: Long,
    val questionId: Long,
    val isWrapUp: Boolean,
    val questionAudioStartAt: Float? = null,
    val questionAudioEndAt: Float? = null,
    val answerStartAt: Float? = null,
    val answerEndAt: Float? = null,
    val answerDuration: Float? = null,
    val endType: InterviewAnswerEndRequest? = null,
    val audioFile: InterviewMediaFileRef? = null,
)

/** 답변 제출 결과. */
data class SubmitAnswerResult(
    val answerId: Long?,
    val nextQuestion: NextQuestion?,
    val sessionEnded: Boolean,
    val wrapUpMessage: WrapUpMessage?,
    val endType: InterviewEndType?,
    val reportGenerating: Boolean = false,
)

/** 다음 면접 질문 정보. */
data class NextQuestion(
    val questionId: Long,
    val isLast: Boolean,
    val turn: QuestionTurn,
)

/** 질문 턴 단계 정보. */
data class QuestionTurn(
    val turnLevel: Int,
    val depthLevel: Int,
)

/** 면접 마무리 멘트. Base64 payload는 저장하거나 기록하지 않는다. */
data class WrapUpMessage(
    val ttsAudio: String?,
)
