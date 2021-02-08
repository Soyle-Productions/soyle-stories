package com.soyle.stories.writer.usecases

import com.soyle.stories.domain.writer.Writer
import com.soyle.stories.usecase.writer.WriterNotRegistered
import com.soyle.stories.usecase.writer.WriterRepository
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.UnexpectedPreferenceValue
import java.util.*

internal suspend fun WriterRepository.getWriter(writerId: UUID): Writer =
  getWriterById(Writer.Id(writerId))
	?: throw WriterNotRegistered(writerId)

internal fun getWriterPreferenceFor(writer: Writer, request: DialogType): Boolean
{
	val preference = writer.preferences["dialog.$request"] ?: true
	if (preference !is Boolean)
		throw UnexpectedPreferenceValue(request.name, preference)
	return preference
}