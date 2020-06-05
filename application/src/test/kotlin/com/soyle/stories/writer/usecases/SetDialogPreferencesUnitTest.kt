package com.soyle.stories.writer.usecases

import com.soyle.stories.entities.Preferences
import com.soyle.stories.entities.Writer
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.doubles.WriterRepositoryDouble
import com.soyle.stories.writer.repositories.WriterRepository
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferences
import com.soyle.stories.writer.usecases.setDialogPreferences.SetDialogPreferencesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SetDialogPreferencesUnitTest {

	private val writerId = Writer.Id().uuid

	@Test
	fun `writer not registered`() {
		whenUseCaseIsExecuted()
		writerNotRegistered(writerId).invoke(result)
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `output new value`(newValue: Boolean) {
		givenWriterExists()
		val dialog = DialogType.values().random().name
		whenUseCaseIsExecuted(dialog = dialog, preference = newValue)
		assertPreferenceSaved(dialog, newValue)
		responseModel(dialog, newValue).invoke(result)
	}

	@ParameterizedTest
	@ValueSource(booleans = [true, false])
	fun `don't save if value already set`(setValue: Boolean) {
		val dialog = DialogType.values().random().name
		givenWriterExists()
		givenPreferenceForDialogVisibilitySet(dialog, setValue)
		whenUseCaseIsExecuted(dialog = dialog, preference = setValue)
		assertNull(savedWriter)
		responseModel(dialog, setValue).invoke(result)
	}

	private var savedWriter: Writer? = null

	private val writerRepository: WriterRepository = WriterRepositoryDouble(onReplaceWriter = {
		savedWriter = it
	})

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

	private fun whenUseCaseIsExecuted(dialog: String = DialogType.values().first().name, preference: Boolean = true)
	{
		val useCase: SetDialogPreferences = SetDialogPreferencesUseCase(writerId, writerRepository)
		val output = object : SetDialogPreferences.OutputPort {
			override fun failedToSetDialogPreferences(failure: Exception) {
				result = failure
			}

			override fun dialogPreferenceSet(response: SetDialogPreferences.ResponseModel) {
				result = response
			}
		}
		runBlocking {
			useCase.invoke(DialogType.valueOf(dialog), preference, output)
		}
	}

	private fun assertPreferenceSaved(dialog: String, preference: Boolean)
	{
		val savedWriter = savedWriter!!
		assertEquals(preference, savedWriter.preferences["dialog.$dialog"])
	}

	private fun responseModel(dialog: String, preference: Boolean): (Any?) -> Unit = { actual ->
		actual as SetDialogPreferences.ResponseModel
		assertEquals(dialog, actual.dialog)
		assertEquals(preference, actual.preference)
	}
}