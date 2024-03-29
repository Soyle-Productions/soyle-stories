package com.soyle.stories.writer.doubles

import com.soyle.stories.domain.writer.Writer
import com.soyle.stories.usecase.writer.WriterRepository

class WriterRepositoryDouble(
  private val onReplaceWriter: (Writer) -> Unit = {}
) : WriterRepository {

	val writers = mutableMapOf<Writer.Id, Writer>()

	override suspend fun addWriter(writer: Writer) {
		writers[writer.id] = writer
	}
	override suspend fun getWriterById(writerId: Writer.Id): Writer? = writers[writerId]
	override suspend fun replaceWriter(writer: Writer) {
		writers[writer.id] = writer
		onReplaceWriter.invoke(writer)
	}
}