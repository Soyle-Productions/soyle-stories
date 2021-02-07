package com.soyle.stories.usecase.writer

import com.soyle.stories.domain.writer.Writer

interface WriterRepository {

	suspend fun addWriter(writer: Writer)
	suspend fun getWriterById(writerId: Writer.Id): Writer?
	suspend fun replaceWriter(writer: Writer)

}