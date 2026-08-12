package com.dminus14.app.feature.interview.di

import com.dminus14.app.feature.interview.device.AndroidInterviewStorageChecker
import com.dminus14.app.feature.interview.device.InterviewStorageChecker
import com.dminus14.app.feature.interview.media.CameraXInterviewVideoRecorder
import com.dminus14.app.feature.interview.media.InterviewAudioPlayer
import com.dminus14.app.feature.interview.media.InterviewMediaTransformer
import com.dminus14.app.feature.interview.media.InterviewVideoRecorder
import com.dminus14.app.feature.interview.media.Media3InterviewAudioPlayer
import com.dminus14.app.feature.interview.media.Media3InterviewMediaTransformer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterviewMediaModule {
    @Binds
    @Singleton
    abstract fun bindAudioPlayer(impl: Media3InterviewAudioPlayer): InterviewAudioPlayer

    @Binds
    @Singleton
    abstract fun bindVideoRecorder(impl: CameraXInterviewVideoRecorder): InterviewVideoRecorder

    @Binds
    @Singleton
    abstract fun bindMediaTransformer(
        impl: Media3InterviewMediaTransformer,
    ): InterviewMediaTransformer

    @Binds
    @Singleton
    abstract fun bindStorageChecker(impl: AndroidInterviewStorageChecker): InterviewStorageChecker
}
