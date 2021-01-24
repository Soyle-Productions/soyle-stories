package com.soyle.stories.scene

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView
import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogViewListener
import com.soyle.stories.common.SyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroup
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.config.dynamic.SceneDetails
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneDetails.includedCharacter.*
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneState
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.scene.control.Menu
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import tornadofx.FX
import tornadofx.singleAssign
import tornadofx.uiComponent
import java.util.*

class IncludedCharacterInSceneTest {

    private val characterId = UUID.randomUUID().toString()
    private val themeId = UUID.randomUUID().toString()
    private val sceneId = UUID.randomUUID()

    private val robot = FxRobot()

    private var includedCharacterView: IncludedCharacterView by singleAssign()

    @Test
    fun `create character arc section item _ onAction _ should display Create Character Arc Section Dialog`() {
        val chipGroup = robot.from(includedCharacterView.root).lookup(".position-on-arc").query<MenuChipGroup>()
        robot.interact {
            (chipGroup.items.component2() // component1() is Create Character Arc item
                    as Menu).items.first() // first listed item should be Create Character Arc Section
                .fire()
        }

        val dialog = robot.listWindows().asSequence().mapNotNull {
            if (it.isShowing) it.scene.root.uiComponent<CreateArcSectionDialogView>()
            else null
        }.firstOrNull() ?: error("No open windows containing CreateArcSectionDialogView")

        assertEquals(characterId, dialog.characterId)
        assertEquals(themeId, dialog.themeId)
        assertEquals(sceneId.toString(), dialog.sceneId)

    }


    @BeforeEach
    fun `initialized all the bullshit`() {
        FX.setPrimaryStage(FX.defaultScope, FxToolkit.registerPrimaryStage())

        scoped<ApplicationScope> {
            provide<ThreadTransformer> {
                SyncThreadTransformer()
            }
        }
        scoped<ProjectScope> {
            provide<CreateArcSectionDialogViewListener> {
                object : CreateArcSectionDialogViewListener {
                    override fun getValidState(themeUUID: String, characterUUID: String) {}
                    override fun createArcSection(
                        characterId: String,
                        themeId: String,
                        sectionTemplateId: String,
                        sceneId: String,
                        description: String
                    ) {}

                    override fun modifyArcSection(
                        characterId: String,
                        themeId: String,
                        arcSectionId: String,
                        sceneId: String,
                        description: String
                    ) {}
                }
            }
        }
        scoped<IncludedCharacterScope> {
            provide<IncludedCharacterInSceneViewListener> {
                object : IncludedCharacterInSceneViewListener {
                    override fun coverCharacterArcSectionInScene(
                        characterArcSectionIds: List<String>,
                        sectionsToUnCover: List<String>
                    ) { }

                    override fun getAvailableCharacterArcSections() {

                    }

                    override fun openSceneDetails(sceneId: String) {
                    }

                    override fun removeCharacter() {
                    }

                    override fun resetMotivation() {
                    }

                    override fun setMotivation(motivation: String) {

                    }
                }
            }
        }

        val includedCharactersState = SceneDetailsScope(
            ProjectScope(ApplicationScope(), ProjectFileViewModel(UUID.randomUUID(), "", "")),
            "",
            SceneDetails(sceneId, object : Locale {
                override val sceneDoesNotExist: String = ""
                override val sceneNameCannotBeBlank: String = ""
            })
        ).get<IncludedCharactersInSceneState>()

        robot.interact {
            includedCharactersState.updateOrInvalidated {
                IncludedCharactersInSceneViewModel(
                    "", "", "", "", "", "", "", "", listOf(
                        IncludedCharacterInSceneViewModel(
                            characterId, "", "", false, null, listOf(), listOf(
                                AvailableCharacterArcViewModel(
                                    "", themeId, "", 0, false, listOf(
                                        AvailableArcSectionViewModel(
                                            "", "", false, ""
                                        )
                                    )
                                )
                            )
                        )
                    ), listOf()
                )
            }
        }

        includedCharacterView = includedCharactersState.includedCharacterScopes.first().get<IncludedCharacterView>()
    }
}