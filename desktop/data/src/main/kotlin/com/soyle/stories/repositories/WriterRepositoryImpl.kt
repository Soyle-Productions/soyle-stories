package com.soyle.stories.repositories

import com.soyle.stories.entities.Preferences
import com.soyle.stories.entities.Writer
import com.soyle.stories.writer.repositories.WriterRepository
import java.util.*

class WriterRepositoryImpl(
  writerId: UUID
) : WriterRepository {

	private var writer = Writer(Writer.Id(writerId), Preferences())

	override suspend fun addWriter(writer: Writer) {
		// no-op
	}

	override suspend fun getWriterById(writerId: Writer.Id): Writer? {
		if (writer.id == writerId) return writer
		return null
	}

	override suspend fun replaceWriter(writer: Writer) {
		if (this.writer isSameEntityAs writer) this.writer = writer
	}

}