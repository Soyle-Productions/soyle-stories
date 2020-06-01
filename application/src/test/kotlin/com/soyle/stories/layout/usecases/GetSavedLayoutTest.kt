package com.soyle.stories.layout.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.doubles.LayoutRepositoryDouble
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:17 PM
 */
class GetSavedLayoutTest {

    private var result: Any? = null

    @Test
    fun `happy path`() {
        givenLayoutExists()
        whenSavedLayoutIsRequested()
        assertResponseModel().invoke(result)
    }

    @Test
    fun `layout does not yet exist`() {
        whenSavedLayoutIsRequested()
        assertLayoutSaved()
        assertResponseModel().invoke(result)
    }

    private val projectId = Project.Id().uuid

    private var savedLayout: Layout? = null
    private val layoutRepository = LayoutRepositoryDouble(onSaveLayout = {
        savedLayout = it
    })

    private fun givenLayoutExists()
    {
        layoutRepository.layout = Layout(Project.Id(projectId))
    }

    private fun whenSavedLayoutIsRequested()
    {
        val useCase: GetSavedLayout = GetSavedLayoutUseCase(layoutRepository)
        val output = object : GetSavedLayout.OutputPort {
            override fun receiveGetSavedLayoutResponse(response: GetSavedLayout.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(projectId, output)
        }
    }

    private fun assertLayoutSaved()
    {
        val savedLayout = savedLayout!!
    }

    private fun assertResponseModel(): (Any?) -> Unit = { actual ->
        actual as GetSavedLayout.ResponseModel
        assertEquals(layoutRepository.layout!!.id.uuid, actual.layoutId)
        assertOnlyOpenWindowsIncluded(actual, layoutRepository.layout!!)
        assertOnlyOpenToolsIncluded(actual, layoutRepository.layout!!)
    }

    private fun assertOnlyOpenWindowsIncluded(response: GetSavedLayout.ResponseModel, layout: Layout)
    {
        val windowsInLayout = layout.windows.associateBy { it.id.uuid }
        response.windows.forEach {
            val windowInLayout = windowsInLayout.getValue(it.id)
            assertTrue(windowInLayout.isOpen)
        }
        assertEquals(response.windows.map { it.id }.toSet(), layout.windows.filter { it.isOpen }.map { it.id.uuid }.toSet())
    }

    private fun assertOnlyOpenToolsIncluded(response: GetSavedLayout.ResponseModel, layout: Layout)
    {

    }
}