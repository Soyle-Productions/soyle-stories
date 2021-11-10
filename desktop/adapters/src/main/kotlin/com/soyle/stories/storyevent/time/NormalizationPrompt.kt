package com.soyle.stories.storyevent.time

interface NormalizationPrompt {
    suspend fun confirmNormalization(): Boolean
}