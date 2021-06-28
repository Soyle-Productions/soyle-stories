package com.soyle.stories.writer.usecases.getAllDialogPreferences

import com.soyle.stories.writer.DialogType
import com.soyle.stories.usecase.writer.WriterRepository
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getWriter
import com.soyle.stories.writer.usecases.getWriterPreferenceFor
import java.util.*

class GetAllDialogPreferencesUseCase(
  private val writerId: UUID,
  private val writerRepository: WriterRepository
) : GetAllDialogPreferences {

	override suspend fun invoke(output: GetAllDialogPreferences.OutputPort) {
		val writer = writerRepository.getWriter(writerId)
		output.receiveAllDialogPreferences(GetAllDialogPreferences.ResponseModel(
		  DialogType.values().map {
			  DialogPreference(
				it,
				getWriterPreferenceFor(writer, it)
			  )
		  }
		))
	}
}