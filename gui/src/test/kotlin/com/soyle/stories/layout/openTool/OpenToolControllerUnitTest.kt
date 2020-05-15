package com.soyle.stories.layout.openTool

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.openTool.OpenTool
import com.soyle.stories.project.layout.openTool.OpenToolController
import com.soyle.stories.project.layout.openTool.OpenToolControllerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class OpenToolControllerUnitTest {

	private val threadTransformer = object : ThreadTransformer {
		override fun async(task: suspend CoroutineScope.() -> Unit) = runBlocking { task() }
		override fun gui(update: suspend CoroutineScope.() -> Unit) = runBlocking { update() }
	}
	private var openToolRequest: OpenTool.RequestModel? = null
	private val openToolUseCase: OpenTool = object : OpenTool {
		override suspend fun invoke(requestModel: OpenTool.RequestModel, output: OpenTool.OutputPort) {
			openToolRequest = requestModel
		}
	}
	private val openToolOutputPort = object : OpenTool.OutputPort {
		override fun receiveOpenToolFailure(failure: Exception) {}
		override fun receiveOpenToolResponse(response: OpenTool.ResponseModel) {}
	}

	@Test
	fun `invalid location id throws error`() {
		val openToolController: OpenToolController = OpenToolControllerImpl(threadTransformer, openToolUseCase, openToolOutputPort)
		assertThrows<IllegalArgumentException> {
			openToolController.openLocationDetailsTool("")
		}
	}

	@Test
	fun `calls open tool use case`() {
		val locationId = UUID.randomUUID()
		val openToolController: OpenToolController = OpenToolControllerImpl(threadTransformer, openToolUseCase, openToolOutputPort)
		openToolController.openLocationDetailsTool(locationId.toString())
		assertThat(openToolRequest)
		  .isNotNull()
		  .hasFieldOrPropertyWithValue("locationId", locationId)
	}

}