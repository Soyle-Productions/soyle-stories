package com.soyle.stories.theme.characterValueComparison.components.addValueButton

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.guiUpdate
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import com.soyle.stories.theme.valueWeb.opposition.list.ListAvailableOppositionValuesForCharacterInThemeController
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.AddSymbolicItemToOpposition
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableOppositionValueForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableValueWebForCharacterInTheme
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioButton
import javafx.stage.Stage
import tornadofx.*
import java.util.*

class AddValueButton(
    // input props
    private val themeId: Theme.Id,
    private val characterId: Character.Id,
    // locale
    private val locale: AddValueButtonLocale,
    // controllers
    private val getAvailableOppositionValues: ListAvailableOppositionValuesForCharacterInThemeController,
    private val addSymbolicItemToOpposition: AddSymbolicItemToOppositionController,
    // other components
    private val makeCreateValueWebForm: CreateValueWebForm.Factory,
    private val makeCreateOppositionValueWebForm: CreateOppositionValueForm.Factory
) : MenuButton() {

    interface Factory {

        operator fun invoke(
            themeId: Theme.Id,
            characterId: Character.Id,
        ): AddValueButton
    }

    /* * * * * * * * * * * */
    // region Initialization
    /* * * * * * * * * * * */

    // initialize style
    init {
        addClass(Styles.addValueButton)
        addClass(ButtonStyles.noArrow)
        addClass(ComponentsStyles.outlined)
        addClass(ComponentsStyles.primary)
        addClass(ComponentsStyles.loading)
    }

    // initialize properties
    init {
        textProperty().bind(locale.addValue)
    }

    // initialize behavior
    init {
        setOnShowing { reloadOppositionValues() }
    }

    //endregion

    /* * * * * * * */
    // region Items
    /* * * * * * * */

    private fun loadingItem(): MenuItem = MenuItem().apply {
        id = "loading"

        isDisable = true
        textProperty().bind(locale.loading)
    }

    private fun createValueWebItem(): MenuItem = MenuItem().apply {
        id = "create-value-web"

        textProperty().bind(locale.createNewValueWeb)
        action { openCreateValueWebDialog() }
    }

    private fun noAvailableValueWebsItem(): MenuItem = MenuItem().apply {
        id = "no-available-value-webs"

        isDisable = true
        textProperty().bind(locale.themeHasNoValueWebs)
    }

    private fun availableValueWebItem(availableValueWeb: AvailableValueWebForCharacterInTheme): MenuItem =
        Menu(availableValueWeb.valueWebName).apply {
            id = ValueWeb.Id(availableValueWeb.valueWebId).toString()
            addClass(Styles.availableValueWebItem)

            items.add(createOppositionValueItem(ValueWeb.Id(availableValueWeb.valueWebId)))
            items.addAll(availableValueWeb.map {
                availableOppositionValueItem(it, availableValueWeb.oppositionCharacterRepresents?.oppositionValueId)
            })
        }

    private fun availableOppositionValueItem(
        availableOppositionValue: AvailableOppositionValueForCharacterInTheme,
        selectedOppositionValueId: UUID?
    ): MenuItem {
        return MenuItem(availableOppositionValue.oppositionValueName).apply {
            id = OppositionValue.Id(availableOppositionValue.oppositionValueId).toString()
            addClass(Styles.availableOppositionItem)

            graphic = RadioButton().apply {
                isSelected = availableOppositionValue.oppositionValueId == selectedOppositionValueId
            }

            action { applyOppositionValueToCharacter(availableOppositionValue.oppositionValueId) }
        }
    }

    private fun createOppositionValueItem(valueWebId: ValueWeb.Id): MenuItem = MenuItem("", RadioButton()).apply {
        id = "create-opposition-value"

        textProperty().bind(locale.createOppositionValue)

        action { openCreateOppositionValueDialog(valueWebId) }
    }

    // endregion

    /* * * * * * * * * */
    // region Behaviors
    /* * * * * * * * * */

    private fun reloadOppositionValues() {
        toggleClass(ComponentsStyles.loading, true)
        items.setAll(loadingItem())
        getAvailableOppositionValues.listAvailableOppositionValuesForCharacter(themeId, characterId) {
            guiUpdate {
                toggleClass(ComponentsStyles.loading, false)
                items.setAll(createValueWebItem())
                if (it.isEmpty()) items.add(noAvailableValueWebsItem())
                items.addAll(it.map(::availableValueWebItem))
            }
        }
    }

    private fun openCreateValueWebDialog() {
        Stage().apply {
            scene = Scene(makeCreateValueWebForm(themeId) {
                guiUpdate { close() }
                applyOppositionValueToCharacter(it.oppositionAddedToValueWeb.oppositionValueId)
            })
            show()
        }
    }

    private fun openCreateOppositionValueDialog(valueWebId: ValueWeb.Id) {
        Stage().apply {
            scene = Scene(makeCreateOppositionValueWebForm(valueWebId) {
                guiUpdate { close() }
                applyOppositionValueToCharacter(it.oppositionValueId)
            })
            show()
        }
    }

    private fun applyOppositionValueToCharacter(oppositionValueId: UUID) {
        addSymbolicItemToOpposition.addCharacterToOpposition(oppositionValueId.toString(), characterId.uuid.toString())
    }

    // endregion

    /* * * * * * * * * * * * * */
    // region Style Definitions
    /* * * * * * * * * * * * * */

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {
        companion object {

            val addValueButton by cssclass()
            val availableValueWebItem by cssclass()
            val availableOppositionItem by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }
    }

    // endregion

}