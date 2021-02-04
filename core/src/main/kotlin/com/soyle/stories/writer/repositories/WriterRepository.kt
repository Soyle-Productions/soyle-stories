package com.soyle.stories.writer.repositories

import com.soyle.stories.entities.Writer

interface WriterRepository {

	suspend fun addWriter(writer: Writer)
	suspend fun getWriterById(writerId: Writer.Id): Writer?
	suspend fun replaceWriter(writer: Writer)

}