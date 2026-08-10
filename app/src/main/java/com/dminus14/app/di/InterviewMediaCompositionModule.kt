package com.dminus14.app.di

import com.dminus14.app.data.di.remote.interview.InterviewAudioOkHttpClient
import com.dminus14.app.feature.interview.media.InterviewQuestionAudioClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
}
