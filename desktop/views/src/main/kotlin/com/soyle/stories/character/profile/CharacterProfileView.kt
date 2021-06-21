package com.soyle.stories.character.profile

import com.soyle.stories.character.create.characterNameInput
import com.soyle.stories.character.nameVariant.addNameVariant.AddCharacterNameVariantController
import com.soyle.stories.character.nameVariant.list.ListCharacterNameVariantsController
import com.soyle.stories.character.nameVariant.remove.RemoveCharacterNameVariantController
import com.soyle.stories.character.nameVariant.rename.RenameCharacterNameVariantController
import com.soyle.stories.characterarc.components.characterIcon
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.components.buttons.ButtonVariant
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.DI.resolve
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterNamesMustBeUnique
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.SoyleStoriesException
import com.soyle.stories.domain.validation.ValidationException
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*

class CharacterProfileView : View() {

    override val scope = super.scope as CharacterProfileScope

    private val state = scope.get<CharacterProfileState>()
    val props: ObjectProperty<CharacterProfileProps>
        get() = state.itemProperty

    private fun startCreatingAltName() = state.isCreatingName.set(true)
    private fun cancelCreateAltName() = state.isCreatingName.set(false)

    private fun createNewAlternativeName(altName: NonBlankString) {
        state.executingNameChange.set(true)
        val controller = scope.projectScope.get<AddCharacterNameVariantController>()
        scope.projectScope.applicationScope.get<ThreadTransformer>().gui {
            try {
                controller.addCharacterNameVariant(state.characterId.value, altName).join()
                state.isCreatingName.set(false)
            } catch (exception: ValidationException) {
                state.creationFailure.set(exception.localizedMessage)
            }
            state.executingNameChange.set(false)
        }
    }

    private fun renameAlternativeName(currentName: NonBlankString, newName: NonBlankString) {
        state.executingNameChange.set(true)
        val controller = scope.projectScope.get<RenameCharacterNameVariantController>()
        scope.projectScope.applicationScope.get<ThreadTransformer>().gui {
            controller.renameCharacterNameVariant(state.characterId.value, currentName, newName)
                .join()
            state.executingNameChange.set(false)
        }
    }

    private fun removeAlternativeName(altName: NonBlankString) {
        state.executingNameChange.set(true)
        val controller = scope.projectScope.get<RemoveCharacterNameVariantController>()
        scope.projectScope.applicationScope.get<ThreadTransformer>().gui {
            controller.removeCharacterNameVariant(state.characterId.value, altName)
                .join()
            state.executingNameChange.set(false)
        }
    }

    override val root: Parent = vbox {
        addClass("character-profile")
        surface {
            isFillWidth = false
            alignment = Pos.CENTER
            spacing = 16.0
            padding = Insets(64.0, 32.0, 32.0, 32.0)
            asSurface {
                inheritedElevationProperty().bind(this@vbox.asSurface().absoluteElevationProperty())
                relativeElevation = Elevation.getValue(4)
            }

            add(characterIcon(state.characterImageResource).apply {
                prefHeight = 64.0
                prefWidth = 64.0
                minHeight = 64.0
                minWidth = 64.0
            })
            toolTitle(state.characterDisplayName)
        }
        progressindicator {
            existsWhen(state.alternativeNames.isNull)
        }
        vbox {
            existsWhen(state.alternativeNames.isNotNull)
            padding = Insets(16.0)
            spacing = 16.0
            secondaryButton("ALSO KNOWN AS", variant = null) {
                id = "create-alt-name-button"
                action(::startCreatingAltName)
            }
            val initialNameValue = SimpleStringProperty("").apply {
                state.isCreatingName.onChange { set(if (it) "" else " ") }
            }
            characterNameInput(initialNameValue, onValid = ::createNewAlternativeName).apply {
                id = "create-alt-name-input"
                existsWhen(state.isCreatingName)
                visibleProperty().onChange { if (it) requestFocus() }
                onLoseFocus(::cancelCreateAltName)
                disableWhen(state.executingNameChange)

                val creationFailureDecorator = state.creationFailure.objectBinding { if (it != null) SimpleMessageDecorator(it, ValidationSeverity.Error) else null }
                properties["creationFailureBinding"] = creationFailureDecorator
                creationFailureDecorator.addListener { _, oldValue, newValue ->
                    oldValue?.let(::removeDecorator)
                    newValue?.let(::addDecorator)
                }

            }
            vbox {
                spacing = 12.0
                bindChildren(state.alternativeNames) { altName ->

                    val isEditing = SimpleBooleanProperty(false)

                    fun editAlternativeName() = isEditing.set(true)
                    fun cancelEdit() = isEditing.set(false)

                    fun commitAltNameRename(newName: NonBlankString) {
                        renameAlternativeName(NonBlankString.create(altName)!!, newName)
                    }

                    fun deleteAlternativeName() {
                        removeAlternativeName(NonBlankString.create(altName)!!)
                    }

                    /*
                    hbox adds an HBox to the parent child list normally, which would cause unexpected behavior because
                    we're calling this during a binding to the child list.  Due to this, HBox would make more sense to
                    call here and let the binding add it to the child list.  That works, but tornadofx, during a child
                    list binding, ignores the first builder call to avoid the unexpected behavior.  So, if we use HBox
                    and then try to build the children of it, the first child is NEVER ADDED.  So, we call hbox like
                    normal, tornadofx ignores the builder call, and we get all of our hbox children added correctly.
                     */
                    hbox {
                        addClass("character-alt-name-item")
                        alignment = Pos.CENTER_LEFT
                        spacing = 8.0
                        secondaryButton(graphic = MaterialIconView(MaterialIcon.EDIT, "1.5em"), variant = null) {
                            addClass("edit-button")
                            action { editAlternativeName() }
                        }
                        characterNameInput(initialValue = altName, onValid = ::commitAltNameRename).apply {
                            existsWhen(isEditing)
                            visibleProperty().onChange { if (it) requestFocus() }
                            onLoseFocus(::cancelEdit)
                            disableWhen(state.executingNameChange)
                        }
                        label(altName ?: "") { existsWhen(isEditing.not()) }
                        secondaryButton(graphic = MaterialIconView(MaterialIcon.DELETE, "1.5em"), variant = null) {
                            addClass("delete-button")
                            action { deleteAlternativeName() }
                        }
                    }
                }
            }
        }
    }

    init {
    }

}