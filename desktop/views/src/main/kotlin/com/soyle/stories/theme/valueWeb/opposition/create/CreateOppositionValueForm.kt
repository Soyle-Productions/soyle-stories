package com.soyle.stories.theme.valueWeb.opposition.create

import com.soyle.stories.common.components.text.Caption
import com.soyle.stories.common.existsWhen
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*

class CreateOppositionValueForm(
    // props
    private val valueWebId: ValueWeb.Id,
    private val onCreateOppositionValue: suspend (OppositionAddedToValueWeb) -> Unit,
    // locale
    private val locale: CreateOppositionValueFormLocale,
    // actions
    private val createOppositionValue: AddOppositionToValueWebController
) : VBox() {

    interface Factory {

        operator fun invoke(
            valueWebId: ValueWeb.Id,
            onCreateOppositionValue: suspend (OppositionAddedToValueWeb) -> Unit = {}
        ): CreateOppositionValueForm
    }

    /* * * * * * * */
    // region State
    /* * * * * * * */

    private val errorMessage = stringProperty(null)
    private val creatingOpposition = booleanProperty(false)

    // endregion

    /* * * * * * * * * * * */
    // region Initialization
    /* * * * * * * * * * * */

    // initialize styles
    init {
        addClass(Styles.createOppositionValueForm)
    }

    // initialize children
    init {
        label(locale.name)
        vbox {
            addClass(Stylesheet.field)

            add(nameInput())
            add(errorLabel())
        }
    }

    //endregion

    /* * * * * * * * * * * */
    // region SubComponents
    /* * * * * * * * * * * */

    private fun nameInput() = TextField().apply {
        disableWhen(creatingOpposition)
        action { tryToCreateOpposition(text) }
        requestFocus()
    }

    private fun errorLabel() = Caption().apply {
        addClass(Stylesheet.error)

        visibleWhen(errorMessage.isNotEmpty)
        textProperty().bind(errorMessage)
    }

    //endregion

    /* * * * * * * * * */
    // region Behaviors
    /* * * * * * * * * */

    private fun tryToCreateOpposition(name: String) {
        errorMessage.unbind()
        val nonBlankName = NonBlankString.create(name) ?: return errorMessage.bind(locale.nameCannotBeBlank)
        tryToCreateOpposition(nonBlankName)
    }

    private val handleCreationFailure = CoroutineExceptionHandler { context, failure -> }

    private fun tryToCreateOpposition(nonBlankName: NonBlankString) {
        creatingOpposition.set(true)
        CoroutineScope(Dispatchers.JavaFx)
            .launch(handleCreationFailure) { awaitOppositionCreation(nonBlankName) }
    }

    private suspend fun awaitOppositionCreation(nonBlankName: NonBlankString) {
        val oppositionAddedToValueWeb = try {
            createOppositionValue.addOpposition(valueWebId.uuid.toString(), nonBlankName).await()
        } catch (t: Throwable) {
            errorMessage.set(t.localizedMessage)
            return
        } finally {
            creatingOpposition.set(false)
        }
        errorMessage.set(null)
        onCreateOppositionValue(oppositionAddedToValueWeb)
    }

    //endregion

    override fun getUserAgentStylesheet(): String = Styles().externalForm

    class Styles : Stylesheet() {

        companion object {

            val createOppositionValueForm by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }
        }

        init {
            createOppositionValueForm {
                spacing = 8.px

                field {
                    spacing = 4.px

                    error {
                        textFill = Color.RED
                        padding = box(0.em, 0.766666.em)
                        backgroundInsets += box(0.px, 1.px)
                    }
                }
            }
        }

    }

}