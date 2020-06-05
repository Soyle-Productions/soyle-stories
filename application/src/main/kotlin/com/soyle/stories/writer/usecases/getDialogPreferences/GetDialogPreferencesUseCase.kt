package com.soyle.stories.writer.usecases.getDialogPreferences

import com.soyle.stories.entities.Writer
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.UnexpectedPreferenceValue
import com.soyle.stories.writer.WriterNotRegistered
import com.soyle.stories.writer.repositories.WriterRepository
import java.util.*

class GetDialogPreferencesUseCase(
  writerId: UUID,
  private val preferencesRepository: WriterRepository
) : GetDialogPreferences {

	private val writerId = Writer.Id(writerId)

	override suspend fun invoke(request: DialogType, output: GetDialogPreferences.OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToGetDialogPreferences(e) }
		output.gotDialogPreferences(response)
	}

	private suspend fun execute(request: DialogType): GetDialogPreferences.ResponseModel {
		val writer = getWriter()
		val preference = getWriterPreferenceFor(writer, request)
		return GetDialogPreferences.ResponseModel(request.name, preference)
	}

	private suspend fun getWriter() = (preferencesRepository.getWriterById(writerId)
	  ?: throw WriterNotRegistered(writerId.uuid))

	private fun getWriterPreferenceFor(writer: Writer, request: DialogType): Boolean
	{
		val preference = writer.preferences["dialog.$request"] ?: true
		if (preference !is Boolean)
			throw UnexpectedPreferenceValue(request.name, preference)
		return preference
	}

}