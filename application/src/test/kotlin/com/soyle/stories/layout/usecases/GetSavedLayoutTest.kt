package com.soyle.stories.layout.usecases

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayoutUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 12:17 PM
 */
class GetSavedLayoutTest {

    private val projectId = Project.Id(UUID.randomUUID())

    lateinit var layoutRepository: MockLayoutRepository
    private lateinit var outputSpy: GetSavedLayoutOutputSpy

    @BeforeEach
    fun createOutputSpy() {
        outputSpy = GetSavedLayoutOutputSpy()
    }

    private fun whenUseCaseExecuted(projectId: UUID) = runBlocking {
        GetSavedLayoutUseCase(layoutRepository).invoke(projectId, outputSpy)
    }

    @Nested
    inner class GivenLayoutDoesNotExist {

        @BeforeEach
        fun setLayout() {
            layoutRepository = MockLayoutRepository()
        }

        @Test
        fun organizedLayoutIsPersisted() {
            whenUseCaseExecuted(UUID.randomUUID())
            assert(layoutRepository.wasCalled(layoutRepository::saveLayout))
        }

    }

    @Nested
    inner class GivenLayoutExists {

        private val layout = layout(projectId, Layout.Id(UUID.randomUUID())) {
            window {
                primaryStack { }
            }
        }

        @BeforeEach
        fun setLayout() {
            layoutRepository = MockLayoutRepository(getLayoutForProject = layout)
        }

    }

    @Nested
    inner class SavedLayoutIsMappedToStoredLayout {

        private val layoutId = UUID.randomUUID()
        val layout = layout(projectId, Layout.Id(layoutId)) {
            window {
                primaryStack {  }
            }
            window {
                stack {
                    tool(Tool.CharacterList(Tool.Id(), projectId, false))
                }
            }
            window {
                stackSplitter(true) {
                    stack(1) {
                        tool(Tool.CharacterList(Tool.Id(), projectId, true))
                        tool(Tool.CharacterList(Tool.Id(), projectId, false))
                    }
                    stack {
                        tool(Tool.CharacterList(Tool.Id(), projectId, false))
                    }
                }
            }
        }

        @BeforeEach
        fun setLayout() {
            layoutRepository = MockLayoutRepository(getLayoutForProject = layout)
        }

        @Test
        fun onlyOpenWindowsAreIncluded() {
            whenUseCaseExecuted(UUID.randomUUID())
            val (savedLayout) = outputSpy.result as Either.Right
            assertEquals(2, savedLayout.windows.size)
        }

        @Test
        fun onlyOpenStacksAndSplittersAreIncluded() {
            whenUseCaseExecuted(UUID.randomUUID())
            val (savedLayout) = outputSpy.result as Either.Right

            val stacksAndSplitters = mutableListOf<Any>()
            fun addAllStacksAndSplitters(stackOrSplitter: OpenWindowChild) {
                stacksAndSplitters.add(stackOrSplitter)
                if (stackOrSplitter is OpenToolGroupSplitter) {
                    stackOrSplitter.children.forEach {
                        addAllStacksAndSplitters(it.second)
                    }
                }
            }

            savedLayout.windows.forEach {
                addAllStacksAndSplitters(it.child)
            }

            assertEquals(3, stacksAndSplitters.size)
        }

        @Test
        fun onlyOpenToolsAreIncluded() {
            whenUseCaseExecuted(UUID.randomUUID())
            val (savedLayout) = outputSpy.result as Either.Right

            val activeTools  = mutableListOf<OpenTool>()
            fun collectAllTools(stackOrSplitter: Any) {
                if (stackOrSplitter is OpenToolGroupSplitter) {
                    stackOrSplitter.children.forEach {
                        collectAllTools(it.second)
                    }
                } else if (stackOrSplitter is OpenToolGroup) {
                    activeTools.addAll(stackOrSplitter.tools)
                }
            }

            savedLayout.windows.forEach {
                collectAllTools(it.child)
            }

            assertEquals(1, activeTools.size)
        }

    }


    private class GetSavedLayoutOutputSpy : GetSavedLayout.OutputPort {
        var result: Either<*, GetSavedLayout.ResponseModel>? = null
            get() {
                if (field == null) error("No output received")
                return field
            }
            private set

        override fun receiveGetSavedLayoutResponse(response: GetSavedLayout.ResponseModel) {
            result = response.right()
        }
    }

}