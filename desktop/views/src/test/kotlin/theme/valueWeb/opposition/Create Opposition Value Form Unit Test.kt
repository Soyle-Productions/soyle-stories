package com.soyle.stories.desktop.view.theme.valueWeb.opposition

import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.AddOppositionToValueWebControllerDouble
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.CreateOppositionValueFormLocaleMock
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.`Create Opposition Value Form Access`.Companion.access
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.`Create Opposition Value Form Access`.Companion.drive
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addOppositionToValueWeb.AddOppositionToValueWebController
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import javafx.application.Platform
import javafx.event.ActionEvent
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class `Create Opposition Value Form Unit Test` : NodeTest<CreateOppositionValueForm>() {

    private val valueWebId = ValueWeb.Id()

    private val locale = CreateOppositionValueFormLocaleMock()

    private var createOppositionValueRequest: Pair<ValueWeb.Id, NonBlankString>? = null
    private val addOppositionToValueWeb = AddOppositionToValueWebControllerDouble(
        onAddOpposition = { valueWebId, name, characterId ->
            assertNull(characterId)
            assertEquals(this.valueWebId.uuid.toString(), valueWebId)
            createOppositionValueRequest = ValueWeb.Id(UUID.fromString(valueWebId)) to name!!
            CompletableDeferred()
        }
    )

    private var oppositionAddedToValueWeb: OppositionAddedToValueWeb? = null
    override val view: CreateOppositionValueForm = CreateOppositionValueForm(
        valueWebId,
        ::oppositionAddedToValueWeb::set,
        locale,
        addOppositionToValueWeb
    )

    init {
        showView()
    }

    @Test
    fun `should show text from locale for name input label`() {
        interact { locale.name.set("The name of the thing") }
        assertEquals("The name of the thing", view.access().nameLabel.text)
    }

    @Test
    fun `error message should not be visible`() {
        assertNull(view.access().errorMessage)
    }

    @Test
    fun `name input should be enabled and focused`() {
        assertFalse(view.access().nameInput.isDisable)
        assertTrue(view.access().nameInput.isFocused)
    }

    @Test
    fun `should should name cannot be blank error when enter is pressed given text is blank`() {
        view.drive {
            nameInput.fireEvent(ActionEvent())
        }

        interact { locale.nameCannotBeBlank.set("you can't NOT provide a name, dude") }
        assertEquals("you can't NOT provide a name, dude", view.access().errorMessage!!.text)
    }

    @Test
    fun `should create opposition value when enter is pressed given text is not blank`() {
        view.drive {
            nameInput.text = "Banana"
            nameInput.fireEvent(ActionEvent())
        }

        assertEquals("Banana", createOppositionValueRequest!!.second.value)
    }

    @Test
    fun `should show error if create opposition value request fails`() {
        val currentHandler = addOppositionToValueWeb.onAddOpposition
        addOppositionToValueWeb.onAddOpposition = { a, b, c ->
            currentHandler(a, b, c)
            CoroutineScope(Dispatchers.Main).async {
                throw Error("Some error with a locale message")
            }
        }
        view.drive {
            nameInput.text = "Banana"
            nameInput.fireEvent(ActionEvent())
        }

        assertEquals("Some error with a locale message", view.access().errorMessage!!.text)
        assertFalse(view.access().nameInput.isDisable)
    }

    @Test
    fun `should disable name input while creating opposition`() {
        view.drive {
            nameInput.text = "Banana"
            nameInput.fireEvent(ActionEvent())
        }

        assertTrue(view.access().nameInput.isDisable)
    }

    @Test
    fun `should notify creator when opposition is created`() {
        val currentHandler = addOppositionToValueWeb.onAddOpposition
        addOppositionToValueWeb.onAddOpposition = { a, b, c ->
            currentHandler(a, b, c)
            CoroutineScope(Dispatchers.Main).async {
                OppositionAddedToValueWeb(UUID.randomUUID(), valueWebId.uuid, UUID.randomUUID(), b!!.value, false)
            }
        }
        view.drive {
            nameInput.text = "Banana"
            nameInput.fireEvent(ActionEvent())
        }

        assertNotNull(oppositionAddedToValueWeb)
        assertFalse(view.access().nameInput.isDisable)
        assertNull(view.access().errorMessage)
    }

    @Nested
    inner class `Given Previously Tried to Use Blank Name` {

        init {
            view.drive {
                nameInput.fireEvent(ActionEvent())
            }
        }

        @Test
        fun `should still display new error message if next attempt fails`() {
            val currentHandler = addOppositionToValueWeb.onAddOpposition
            addOppositionToValueWeb.onAddOpposition = { a, b, c ->
                currentHandler(a, b, c)
                CoroutineScope(Dispatchers.Main).async {
                    throw Error("Some error with a locale message")
                }
            }
            view.drive {
                nameInput.text = "Banana"
                nameInput.fireEvent(ActionEvent())
            }

            assertEquals("Some error with a locale message", view.access().errorMessage!!.text)
            assertFalse(view.access().nameInput.isDisable)
        }

        @Test
        fun `should no longer show error message when opposition is created`() {
            val currentHandler = addOppositionToValueWeb.onAddOpposition
            addOppositionToValueWeb.onAddOpposition = { a, b, c ->
                currentHandler(a, b, c)
                CoroutineScope(Dispatchers.Main).async {
                    OppositionAddedToValueWeb(UUID.randomUUID(), valueWebId.uuid, UUID.randomUUID(), b!!.value, false)
                }
            }
            view.drive {
                nameInput.text = "Banana"
                nameInput.fireEvent(ActionEvent())
            }

            assertNull(view.access().errorMessage)
        }

    }

}