package com.soyle.stories.desktop.view.theme.valueWeb

import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.theme.valueWeb.create.CreateValueWebFormLocaleMock
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.desktop.view.theme.valueWeb.create.`Create Value Web Form Access`.Companion.access
import com.soyle.stories.desktop.view.theme.valueWeb.create.`Create Value Web Form Access`.Companion.drive
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addValueWebToTheme.AddValueWebToThemeController
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import javafx.event.ActionEvent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*

class `Create Value Web Form Unit Test` : NodeTest<CreateValueWebForm>() {

    private val themeId = Theme.Id()

    private val locale = CreateValueWebFormLocaleMock()

    private var valueWebAddedToTheme: ValueWebAddedToTheme? = null
    private var createValueWebRequest: NonBlankString? = null
    private val createValueWeb = AddValueWebToThemeControllerDouble(
        onAddValueWebToTheme = { themeId, name ->
            assertEquals(this.themeId.uuid.toString(), themeId)
            createValueWebRequest = name
            CompletableDeferred()
        }
    )

    override val view: CreateValueWebForm = CreateValueWebForm(
        themeId,
        ::valueWebAddedToTheme::set,
        locale,
        createValueWeb
    )

    init {
        showView()
    }

    @Test
    fun `should show name label when first created`() {
        interact { locale.name.set("The name of the thing") }
        assertEquals("The name of the thing", view.access().nameLabel.text)
    }

    @Test
    fun `name input should not be disabled when first created`() {
        assertFalse(view.access().nameInput.isDisable)
    }

    @Test
    fun `name input should be focused when first created`() {
        assertTrue(view.access().nameInput.isFocused)
    }

    @Test
    fun `should not show error message when first created`() {
        assertNull(view.access().errorMessage)
    }

    @Nested
    inner class `When Enter Key is Pressed in the Name Input Field` {

        @Test
        fun `should not disable name input`() {
            view.drive { nameInput.fireEvent(ActionEvent()) }

            assertFalse(view.access().nameInput.isDisable)
        }

        @Test
        fun `should display name cannot be blank error`() {
            view.drive { nameInput.fireEvent(ActionEvent()) }

            interact { locale.nameCannotBeBlank.set("The name can't be blank") }
            assertEquals("The name can't be blank", view.access().errorMessage!!.text)
        }

        @Test
        fun `should not display error message when another attempt is made`() {
            view.drive { nameInput.fireEvent(ActionEvent()) }
            view.drive {
                nameInput.text = "Banana"
                nameInput.fireEvent(ActionEvent())
            }

            assertNull(view.access().errorMessage)
        }

        @Nested
        inner class `Given Name is not Blank` {

            init {
                view.drive { nameInput.text = "Banana" }
            }

            @Test
            fun `should disable name input`() {
                view.drive { nameInput.fireEvent(ActionEvent()) }

                assertTrue(view.access().nameInput.isDisable)
            }

            @Test
            fun `should generate create value web request`() {
                view.drive { nameInput.fireEvent(ActionEvent()) }

                assertEquals("Banana", createValueWebRequest!!.value)
            }

            @Test
            fun `should not display error message yet`() {
                view.drive { nameInput.fireEvent(ActionEvent()) }

                assertNull(view.access().errorMessage)
            }

            @Nested
            inner class `Given Value Web Successfully Created` {

                private val createdValueWebId = ValueWeb.Id()

                init {
                    ensureRequestSucceeds()
                    view.drive { nameInput.fireEvent(ActionEvent()) }
                }

                @Test
                fun `should send event to callback`() {
                    assertEquals(themeId.uuid, valueWebAddedToTheme!!.themeId)
                    assertEquals(createdValueWebId.uuid, valueWebAddedToTheme!!.valueWebId)
                }

                @Test
                fun `should clear name input and enable it`() {
                    assertTrue(view.access().nameInput.text.isEmpty())
                    assertFalse(view.access().nameInput.isDisable)
                }

                private fun ensureRequestSucceeds() {
                    val currentHandler = createValueWeb.onAddValueWebToTheme
                    createValueWeb.onAddValueWebToTheme = { themeId, name ->
                        currentHandler(themeId, name)
                        CompletableDeferred(
                            ValueWebAddedToTheme(
                                this@`Create Value Web Form Unit Test`.themeId.uuid,
                                createdValueWebId.uuid,
                                name.value,
                                OppositionAddedToValueWeb(
                                    this@`Create Value Web Form Unit Test`.themeId.uuid,
                                    createdValueWebId.uuid,
                                    UUID.randomUUID(),
                                    name.value,
                                    false
                                )
                            )
                        )
                    }
                }

            }

            @Nested
            inner class `Given Value Web Fails to be Created` {

                private val expectedErrorText = "I failed asynchronously!"

                init {
                    ensureRequestFails()
                    view.drive { nameInput.fireEvent(ActionEvent()) }
                }

                @Test
                fun `error message should display error text`() {
                    assertEquals(expectedErrorText, view.access().errorMessage!!.text)
                }

                @Test
                fun `name input should be enabled to resolve issue and try again`() {
                    assertFalse(view.access().nameInput.isDisable)
                }

                @Test
                fun `should not display error message when another attempt is made`() {
                    createValueWeb.onAddValueWebToTheme = { themeId, name -> CompletableDeferred() }
                    view.drive { nameInput.fireEvent(ActionEvent()) }

                    assertNull(view.access().errorMessage)
                }

                private fun ensureRequestFails() {
                    val currentHandler = createValueWeb.onAddValueWebToTheme
                    createValueWeb.onAddValueWebToTheme = { themeId, name ->
                        currentHandler(themeId, name)
                        CoroutineScope(Dispatchers.Main).async {
                            throw Error(expectedErrorText)
                        }
                    }
                }

            }

        }

    }

}