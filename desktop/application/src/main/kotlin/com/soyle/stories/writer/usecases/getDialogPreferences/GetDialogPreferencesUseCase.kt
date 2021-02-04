package com.soyle.stories.writer.usecases.getDialogPreferences

import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.repositories.WriterRepository
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getWriter
import com.soyle.stories.writer.usecases.getWriterPreferenceFor
import java.util.*

class GetDialogPreferencesUseCase(
  private val writerId: UUID,
  private val writerRepository: WriterRepository
) : GetDialogPreferences {

	override suspend fun invoke(request: DialogType, output: GetDialogPreferences.OutputPort) {
		val response = try { execute(request) }
		catch (e: Exception) { return output.failedToGetDialogPreferences(e) }
		output.gotDialogPreferences(response)
	}

	private suspend fun execute(request: DialogType): DialogPreference {
		val writer = writerRepository.getWriter(writerId)
		val preference = getWriterPreferenceFor(writer, request)
		return DialogPreference(request, preference)
	}

}