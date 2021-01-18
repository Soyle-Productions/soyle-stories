package com.soyle.stories.theme.themeList

import com.soyle.stories.common.SyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.skin.TreeViewSkin
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

class ThemeListViewTest : ApplicationTest() {

    val stage = FxToolkit.registerPrimaryStage()
    val projectScope = makeProjectScope()


    init {
        setupMockDI()
        interact {
            val themeList = projectScope.get<ThemeList>()
            val splitPane = BorderPane(SplitPane(SplitPane(
                Pane().apply { minWidth = 100.0 },
                TabPane(
                    Tab("", themeList.root)
                )
            ), Pane().apply { minHeight = 100.0 }).apply {
                orientation = Orientation.VERTICAL
            })
            if (stage.scene != null) stage.scene.root = splitPane
            else stage.scene = Scene(splitPane)

            stage.show()
        }
    }

    @Test
    fun `all items rendered`() {
        projectScope.get<ThemeListModel>().update {
            ThemeListViewModel(
                "",
                "",
                listOf(),
                "",
                "",
                ""
            )
        }
        projectScope.get<ThemeListModel>().updateOrInvalidated {
            copy(
                themes = listOf(
                    ThemeListItemViewModel(
                        "Theme 1",
                        "The first Theme",
                        listOf(
                            SymbolListItemViewModel(
                                "Symbol 1",
                                "The first symbol"
                            )
                        )
                    )
                )
            )
        }

        val themeList = projectScope.get<ThemeList>()


        ThemeListDriver(themeList).run {
            val themeItem = getThemeItemOrError("The first Theme")
            val symbolItem = getSymbolItemOrError(themeItem, "The first symbol")

            interact { themeItem.isExpanded = true }

            println(projectScope.get<ThemeListModel>().item)

            Thread.sleep(2000)

            val flow = (getTree().skin as TreeViewSkin<*>).children.first() as VirtualFlow<*>
            assertEquals(2, flow.cellCount)
        }
    }

    private fun setupMockDI() {
        DI.registerTypeFactory<ThreadTransformer, ApplicationScope> {
            SyncThreadTransformer()
        }
        DI.registerTypeFactory<ThemeListViewListener> {
            object : ThemeListViewListener {
                override fun getValidState() {

                }

                override fun openValueWeb(themeId: String) {}

                override fun openCharacterComparison(themeId: String) {}

                override fun openCentralConflict(themeId: String) {}

                override fun openMoralArgument(themeId: String) {}

                override fun renameTheme(themeId: String, newName: String) {}

                override fun renameSymbol(symbolId: String, newName: String) {}
            }
        }
    }

    private fun makeProjectScope(): ProjectScope {
        return ProjectScope(
            ApplicationScope(),
            ProjectFileViewModel(UUID.randomUUID(), "", "")
        )
    }

}