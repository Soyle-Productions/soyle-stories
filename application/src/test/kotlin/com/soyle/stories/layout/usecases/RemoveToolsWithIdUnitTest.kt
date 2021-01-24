package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.assertLayoutDoesNotExist
import com.soyle.stories.layout.assertResponseModel
import com.soyle.stories.layout.doubles.LayoutRepositoryDouble
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.DynamicTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithId
import com.soyle.stories.layout.usecases.removeToolsWithId.RemoveToolsWithIdUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class RemoveToolsWithIdUnitTest {

	private val id = UUID.randomUUID()

	@Test
	fun `remove tools`() {
		givenALayoutExists()
		givenDynamicToolsExistInLayoutWithId(isOpen = true)
		whenToolsWithIdAreRemoved()
		layoutShouldNotHaveAnyToolsWithId()
		assertResponseModel(savedLayout!!).invoke(result)
	}

	@Test
	fun `layout doesn't exist`() {
		whenToolsWithIdAreRemoved()
		assertNull(savedLayout)
		assertLayoutDoesNotExist().invoke(result)
	}

	@Test
	fun `no tools with id`() {
		givenALayoutExists()
		whenToolsWithIdAreRemoved()
		assertNull(savedLayout)
		assertResponseModel(layoutRepository.layout!!).invoke(result)
	}

	@Test
	fun `no tools open`() {
		givenALayoutExists()
		givenDynamicToolsExistInLayoutWithId(isOpen = false)
		whenToolsWithIdAreRemoved()
		layoutShouldNotHaveAnyToolsWithId()
		assertResponseModel(savedLayout!!).invoke(result)
	}

	private var savedLayout: Layout? = null
	private val layoutRepository = LayoutRepositoryDouble(onSaveLayout = {
		savedLayout = it
	})

	private val layoutId = Layout.Id().uuid
	private val projectId = Project.Id().uuid

	private fun givenALayoutExists()
	{
		layoutRepository.layout = Layout(Project.Id(projectId)).let {
			Layout(Layout.Id(layoutId), it.projectId, it.windows)
		}
	}

	private var dynamicTools: List<DynamicTool>? = null

	private fun givenDynamicToolsExistInLayoutWithId(isOpen: Boolean)
	{
		dynamicTools = List(3) {
			object : DynamicTool() {
				override fun identifiedWithId(id: UUID): Boolean = id == this@RemoveToolsWithIdUnitTest.id
				override suspend fun validate(context: OpenToolContext) {}
			}
		}
		layoutRepository.layout = dynamicTools!!.fold(layoutRepository.layout!!) { layout, tool ->
			layout.withToolAddedToStack(Tool(tool, isOpen), layout.primaryStack.id)
		}
	}

	private var result: Any? = null

	private fun whenToolsWithIdAreRemoved()
	{
		val useCase: RemoveToolsWithId = RemoveToolsWithIdUseCase(projectId, layoutRepository)
		val output = object : RemoveToolsWithId.OutputPort {
			override fun toolsRemovedWithId(response: GetSavedLayout.ResponseModel) {
				result = response
			}

			override fun failedToRemoveToolsWithId(failure: Exception) {
				result = failure
			}
		}
		runBlocking {
			useCase.invoke(id, output)
		}
	}

	private fun layoutShouldNotHaveAnyToolsWithId()
	{
		val layout = savedLayout!!
		dynamicTools!!.forEach {
			assertNull(layout.getToolByType(it)) { "Layout still contains $it even though it is identified with $id" }
		}
	}

}