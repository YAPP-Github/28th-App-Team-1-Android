package com.dminus14.app.di

import com.dminus14.app.data.di.remote.interview.InterviewAudioOkHttpClient
import com.dminus14.app.data.local.interview.InterviewFileStore
import com.dminus14.app.feature.interview.media.InterviewMediaFileResolver
import com.dminus14.app.feature.interview.media.InterviewQuestionAudioClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterviewMediaCompositionModule {
    @Provides
    @Singleton
    @InterviewQuestionAudioClient
    fun provideInterviewQuestionAudioClient(
        @InterviewAudioOkHttpClient client: OkHttpClient,
    ): OkHttpClient = client

    /** data 파일 저장소를 Feature 소유 미디어 파일 해석 계약에 연결한다. */
    @Provides
    @Singleton
    fun provideInterviewMediaFileResolver(
        fileStore: InterviewFileStore,
    ): InterviewMediaFileResolver =
        InterviewMediaFileResolver { mediaRef ->
            withContext(Dispatchers.IO) { fileStore.resolve(mediaRef) }
        }
}
