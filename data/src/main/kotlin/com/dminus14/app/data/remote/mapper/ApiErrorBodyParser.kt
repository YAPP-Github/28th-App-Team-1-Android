package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.ApiErrorResponseDto
import com.google.gson.Gson
import okhttp3.Response
import retrofit2.HttpException

/**
 * 서버 에러 응답 바디를 [ApiErrorResponseDto]로 파싱하는 공용 유틸리티.
 *
 * Retrofit 호출부([HttpException])와 OkHttp [okhttp3.Authenticator]([Response])는
 * 에러 바디에 접근하는 방식이 달라 각각의 오버로드를 제공하되, 파싱 로직 자체는 한 곳에서만 관리한다.
 */
internal object ApiErrorBodyParser {
    private val gson = Gson()

    fun parse(error: HttpException): ApiErrorResponseDto? =
        runCatching {
            error.response()?.errorBody()?.string()?.let { body ->
                gson.fromJson(body, ApiErrorResponseDto::class.java)
            }
        }.getOrNull()

    /**
     * [Response.peekBody]로 읽어 원본 스트림을 소비하지 않는다.
     * 이 응답은 [okhttp3.Authenticator.authenticate]가 `null`을 반환하면 그대로 상위 호출부(Retrofit)에
     * 전파되어 다시 파싱되므로, 여기서 [Response.body]를 직접 읽으면 상위 호출부의 에러 파싱이 깨진다.
     */
    fun parse(response: Response): ApiErrorResponseDto? =
        runCatching {
            response.peekBody(MAX_PEEK_BYTES).string().let { body ->
                gson.fromJson(body, ApiErrorResponseDto::class.java)
            }
        }.getOrNull()

    /** Refresh API 401 — RefreshToken 만료/무효로 재로그인 필요. */
    fun isLoginExpired(error: HttpException): Boolean =
        parse(error)?.code == ApiErrorCode.LOGIN_EXPIRED

    private const val MAX_PEEK_BYTES = 64L * 1024
}
