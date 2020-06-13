package com.soyle.stories.writer.usecases

import com.soyle.stories.entities.Preferences
import com.soyle.stories.entities.Writer
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.doubles.WriterRepositoryDouble
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferences
import com.soyle.stories.writer.usecases.getAllDialogPreferences.GetAllDialogPreferencesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAllDialogPreferencesUnitTest {

	private val writerId = Writer.Id().uuid

	private var result: Any? = null

	@Test
	fun `writer not registered`() {
		whenUseCaseIsExecuted()
		writerNotRegistered(writerId).invoke(result)
	}

	@Test
	fun `not all dialog types exist`() {
		val dialogsExist = DialogType.values().let {
			(it.drop(2).map {
				it to listOf(true, false).random()
			} +
			  (it.component1() to true) + // guarantees at least one will exist
			  (it.component2() to false)) // guarantees at least one will not exist
			  .groupBy {
				  it.second
			  }
		}
		givenWriterExists()
		givenDialogsExist(dialogsExist.getValue(true).map { it.first })
		whenUseCaseIsExecuted()
		responseModel().invoke(result)
	}


	private val writerRepository = WriterRepositoryDouble()

	private fun givenWriterExists() {
		Writer(Writer.Id(writerId), Preferences()).let {
			writerRepository.writers[it.id] = it
		}
	}

	private fun givenDialogsExist(dialogs: List<DialogType>)
	{
		dialogs.fold(Writer(Writer.Id(writerId), Preferences())) { writer, dialog ->
			writer.withPreferenceFor("dialog.${dialog.name}", true)
		}.let {
			writerRepository.writers[it.id] = it
		}
	}

	private fun whenUseCaseIsExecuted()
	{
		val useCase: GetAllDialogPreferences = GetAllDialogPreferencesUseCase(writerId, writerRepository)
		val output = object : GetAllDialogPreferences.OutputPort {
			override fun receiveAllDialogPreferences(response: GetAllDialogPreferences.ResponseModel) {
				result = response
			}
		}
		runBlocking {
			try {
				useCase.invoke(output)
			} catch (t: Throwable) {
				result = t
			}
		}
	}


	private fun responseModel(): (Any?) -> Unit = { actual ->
		actual as GetAllDialogPreferences.ResponseModel
		assertEquals(
		  DialogType.values().toSet(),
		  actual.dialogs.map { it.id }.toSet()
		)
	}


}