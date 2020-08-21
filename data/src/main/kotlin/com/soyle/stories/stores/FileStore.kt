package com.soyle.stories.stores

interface FileStore<Data : Any> {

    suspend fun createFile(location: String, data: Data)
    suspend fun getFileAt(location: String): Data?

}