package com.soyle.stories.writer.usecases

import com.soyle.stories.domain.writer.Writer
import com.soyle.stories.domain.writer.Preferences
import com.soyle.stories.usecase.writer.WriterRepository
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.UnexpectedPreferenceValue
import com.soyle.stories.writer.doubles.WriterRepositoryDouble
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferencesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetDialogPreferencesUnitTest {

	private val writerId = Writer.Id().uuid

	@Test
	fun `writer not registered`() {
		whenUseCaseIsExecuted()
		writerNotRegistered(writerId).invoke(result)
	}

	@Test
	fun `dialog doesn't exist`() {
		val dialogThatDoesNotExist = DialogType.values().random()
		givenWriterExists()
		whenUseCaseIsExecuted(dialog = dialogThatDoesNotExist.name)
		responseModel(dialogThatDoesNotExist, true).invoke(result)
	}

	@Test
	fun `stored value is not boolean`() {
		val dialogThatExists = DialogType.values().random().name
		val storedValue = object {}
		givenPreferenceSet("dialog.$dialogThatExists", storedValue)
		whenUseCaseIsExecuted(dialog=dialogThatExists)
		unexpectedPreferenceValue(dialogThatExists, storedValue).invoke(result)
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `output stored value`(shouldShowDialog: Boolean) {
		val dialogThatExists = DialogType.values().random()
		givenPreferenceForDialogVisibilitySet(dialogThatExists.name, shouldShowDialog)
		whenUseCaseIsExecuted(dialog=dialogThatExists.name)
		responseModel(dialogThatExists, shouldShowDialog).invoke(result)
	}

	private val writerRepository: WriterRepository = WriterRepositoryDouble()

	private fun givenWriterExists() {
		runBlocking {
			writerRepository.addWriter(Writer(Writer.Id(writerId), Preferences()))
		}
	}

	private fun givenPreferenceSet(key: String, value: Any) {
		runBlocking {
			val writer =writerRepository.getWriterById(Writer.Id(writerId)) ?: Writer(Writer.Id(writerId), Preferences())
			writerRepository.addWriter(writer.withPreferenceFor(key, value))
		}
	}

	private fun givenPreferenceForDialogVisibilitySet(dialog: String, shouldShow: Boolean) {
		givenPreferenceSet("dialog.$dialog", shouldShow)
	}

	private var result: Any? = null

	private fun whenUseCaseIsExecuted(dialog: String = DialogType.values().first().name)
	{
		val useCase: GetDialogPreferences = GetDialogPreferencesUseCase(writerId, writerRepository)
		val output = object : GetDialogPreferences.OutputPort {
			override fun failedToGetDialogPreferences(failure: Exception) {
				result = failure
			}

			override fun gotDialogPreferences(response: DialogPreference) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(DialogType.valueOf(dialog), output)
		}
	}

	private fun unexpectedPreferenceValue(dialog: String, value: Any): (Any?) -> Unit = { actual ->
		actual as UnexpectedPreferenceValue
		assertEquals(dialog, actual.dialog)
		assertEquals(value, actual.value)
	}

	private fun responseModel(dialog: DialogType, shouldShow: Boolean): (Any?) -> Unit = { actual ->
		actual as DialogPreference
		assertEquals(dialog, actual.id)
		assertEquals(shouldShow, actual.shouldShow)
	}

}