package com.app.mlkit.di

import android.content.Context
import com.app.mlkit.data.dataSource.DocumentLocalDataSource
import com.app.mlkit.data.dataSource.MlKitTextRecognizer
import com.app.mlkit.data.repository.DocumentRepositoryImpl
import com.app.mlkit.domain.repository.DocumentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMlKitTextRecognizer(@ApplicationContext context: Context): MlKitTextRecognizer {
        return MlKitTextRecognizer(context)
    }

    @Provides
    @Singleton
    fun provideDocumentLocalDataSource(@ApplicationContext context: Context): DocumentLocalDataSource {
        return DocumentLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideDocumentRepository(
        localDataSource: DocumentLocalDataSource,
        textRecognizer: MlKitTextRecognizer
    ): DocumentRepository {
        return DocumentRepositoryImpl(textRecognizer, localDataSource)
    }
}