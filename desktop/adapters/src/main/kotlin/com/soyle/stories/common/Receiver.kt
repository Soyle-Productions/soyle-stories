package com.soyle.stories.common

fun interface Receiver<T> {
    suspend fun receiveEvent(event: T)
}