package com.soyle.stories.desktop.view.theme.characterValueComparison

import com.soyle.stories.desktop.view.testframework.DesignTest
import com.soyle.stories.desktop.view.testframework.State
import com.soyle.stories.desktop.view.theme.characterComparison.addValueButton.AddValueButtonFactory
import com.soyle.stories.desktop.view.theme.characterComparison.addValueButton.`Add Value Button Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterComparison.doubles.ListAvailableOppositionValuesForCharacterInThemeControllerDouble
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import com.soyle.stories.theme.valueWeb.opposition.list.ListAvailableOppositionValuesForCharacterInThemeController
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableOppositionValueForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableValueWebForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.OppositionValuesAvailableForCharacterInTheme
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.OppositionValueItem
import javafx.scene.Node
import kotlinx.coroutines.runBlocking
import java.util.*

class `Add Value Button Design` : DesignTest() {

    private var loadAvailableOppositionsOutput: ListAvailableOppositionValuesForCharacterInTheme.OutputPort? = null

    private val factory = AddValueButtonFactory()
    override val node: AddValueButton
        get() = factory.invoke(Theme.Id(), Character.Id())

    @State
    fun `default loading`() = verifyDesign()

    @State
    fun `no available value webs`() {
        factory.listAvailableOppositionValuesForCharacterInThemeController =
            ListAvailableOppositionValuesForCharacterInThemeControllerDouble(
                onInvoke = { _, _, output ->
                    runBlocking {
                        output.availableOppositionValuesListedForCharacterInTheme(
                            OppositionValuesAvailableForCharacterInTheme(
                                UUID.randomUUID(),
                                UUID.randomUUID(),
                                emptyList()
                            )
                        )
                    }
                }
            )
        verifyDesign()
    }

    @State
    fun `available value webs`() {
        factory.listAvailableOppositionValuesForCharacterInThemeController =
            ListAvailableOppositionValuesForCharacterInThemeControllerDouble(
                onInvoke = { _, _, output ->
                    runBlocking {
                        output.availableOppositionValuesListedForCharacterInTheme(
                            OppositionValuesAvailableForCharacterInTheme(UUID.randomUUID(), UUID.randomUUID(), List(5) {
                                AvailableValueWebForCharacterInTheme(
                                    UUID.randomUUID(),
                                    "Value Web ${it + 1}",
                                    null,
                                    List(5) {
                                        AvailableOppositionValueForCharacterInTheme(
                                            UUID.randomUUID(),
                                            "Opposition Value ${it + 1}"
                                        )
                                    })
                            })
                        )
                    }
                }
            )
        verifyDesign()
    }

    @State
    fun `available value webs and opposition value used from value web`() {
        factory.listAvailableOppositionValuesForCharacterInThemeController =
            ListAvailableOppositionValuesForCharacterInThemeControllerDouble(
                onInvoke = { _, _, output ->
                    runBlocking {
                        output.availableOppositionValuesListedForCharacterInTheme(
                            OppositionValuesAvailableForCharacterInTheme(UUID.randomUUID(), UUID.randomUUID(), List(5) {
                                val oppositionValues = List(5) {
                                    AvailableOppositionValueForCharacterInTheme(
                                        UUID.randomUUID(),
                                        "Opposition Value ${it + 1}"
                                    )
                                }
                                val oppositionValueUsed = oppositionValues.random().oppositionValueId
                                AvailableValueWebForCharacterInTheme(
                                    UUID.randomUUID(),
                                    "Value Web ${it + 1}",
                                    OppositionValueItem(oppositionValueUsed, ""),
                                    oppositionValues
                                )
                            })
                        )
                    }
                }
            )
        verifyDesign()
    }

}