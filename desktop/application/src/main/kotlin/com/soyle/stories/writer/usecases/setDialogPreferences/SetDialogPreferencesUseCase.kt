package com.soyle.stories.writer.usecases.setDialogPreferences

import com.soyle.stories.domain.writer.Writer
import com.soyle.stories.usecase.writer.WriterNotRegistered
import com.soyle.stories.usecase.writer.WriterRepository
import com.soyle.stories.writer.DialogType
import java.util.*

class SetDialogPreferencesUseCase(
  writerId: UUID,
  private val writerRepository: WriterRepository
) : SetDialogPreferences {

	private val writerId = Writer.Id(writerId)

	override suspend fun invoke(dialog: DialogType, preference: Boolean, outputPort: SetDialogPreferences.OutputPort) {
		val response = try { execute(dialog, preference) }
		catch (e: Exception) { return outputPort.failedToSetDialogPreferences(e) }
		outputPort.dialogPreferenceSet(response)
	}

	private suspend fun execute(dialog: DialogType, preference: Boolean): SetDialogPreferences.ResponseModel
	{
		val writer = getWriter()
		if (writer.preferences["dialog.$dialog"] != preference) {
			writerRepository.replaceWriter(writer.withPreferenceFor("dialog.$dialog", preference))
		}
		return SetDialogPreferences.ResponseModel(dialog.name, preference)
	}

	private suspend fun getWriter() = (writerRepository.getWriterById(writerId)
	  ?: throw WriterNotRegistered(writerId.uuid))

}