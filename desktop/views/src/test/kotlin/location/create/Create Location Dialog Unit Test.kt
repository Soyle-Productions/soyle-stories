package com.soyle.stories.desktop.view.location.create

import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.location.create.CreateLocationDialogViewAccess.Companion.access
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.Labeled
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import javafx.stage.Window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import tornadofx.SimpleMessageDecorator
import tornadofx.decorators
import tornadofx.onChange
import tornadofx.stringBinding

class `Create Location Dialog Unit Test` : NodeTest<CreateLocationDialog.View>() {

    private val dialogFactory = CreateLocationDialogFactory()
    private val locale = dialogFactory.locale
    private val createNewLocationController = dialogFactory.createNewLocation
    private var createLocationRequest: Pair<SingleNonBlankLine, String>? = null
    init {
        createNewLocationController.onCreateNewLocation = { name, description ->
            createLocationRequest = name to description
        }
    }
    private val threadTransformer = dialogFactory.threadTransformer

    private var createdLocationResponse: CreateNewLocation.ResponseModel? = null
    private val dialog = dialogFactory.invoke {
        createdLocationResponse = it
    }


    init {
        interact {
            dialog.show(primaryStage)
        }
    }

    override val view: CreateLocationDialog.View by lazy {
        listWindows().asSequence().mapNotNull {
            if (! it.isShowing) return@mapNotNull null
            it.scene.root as? CreateLocationDialog.View
        }.single()
    }

    @AfterEach
    fun `remove stage`() {
        interact { view.scene.window.hide() }
    }

    @Test
    fun `text should come from locale`() {
        (view.scene.window as Stage).titleProperty()
            .shouldUpdateToMatch(locale.newLocation, whenChangedTo = "Create a new location, dude")

        with (view.access()) {
            nameLabel.textProperty().shouldUpdateToMatch(locale.name, whenChangedTo = "The location name")
            descriptionLabel.textProperty().shouldUpdateToMatch(locale.description, whenChangedTo = "The location description")
            createButton.textProperty().shouldUpdateToMatch(locale.create, whenChangedTo = "Create a location")
            cancelButton.textProperty().shouldUpdateToMatch(locale.cancel, whenChangedTo = "Cancel creating a location")
        }
    }

    @Test
    fun `initial state`() {
        assertTrue(view.access().createButton.isDisable)
        assertTrue(view.access().nameInput.isFocused)
    }

    @Test
    fun `should close window when cancel button is selected`() {
        interact { view.access().cancelButton.fire() }
        assertFalse(view.scene.window.isShowing)
    }

    @Nested
    inner class `When enter key is pressed in name input`
    {

        init {
            interact { type(KeyCode.ENTER) }
        }

        @Test
        fun `should show error in name field`() {
            view.access().nameError!!
            interact {
                locale.pleaseProvideALocationName.set("You need some text in here")
            }
            assertEquals(
                "You need some text in here",
                view.access().nameError!!.message
            )
        }

        @Test
        fun `should only show one error if key is pressed multiple times`() {
            interact {
                type(KeyCode.ENTER)
                type(KeyCode.ENTER)
            }
            view.access().nameError!!
        }

        @Nested
        inner class `Given Text is input into name field`
        {
            init {
                interact { view.access().nameInput.text = "Some locaiton name" }
            }

            @Test
            fun `should not show error when key is pressed`() {
                interact { type(KeyCode.ENTER) }
                assertNull(view.access().nameError)
            }

            @Test
            fun `should not show error when create button is pressed`() {
                interact { view.access().createButton.fire() }
                assertNull(view.access().nameError)
            }

        }

    }

    @Nested
    inner class `Given Text has been Entered into Name Field`
    {

        private val inputNameText = "A potential location name"

        init {
            interact { view.access().nameInput.text = inputNameText }
        }

        @Test
        fun `create button should be enabled`() {
            assertFalse(view.access().createButton.isDisable)
        }

        @Test
        fun `should generate create location request when create button is clicked`() {
            interact { view.access().createButton.fire() }
            assertEquals((inputNameText to "").toString(), createLocationRequest.toString())
        }

        @Test
        fun `should generate create location request when enter pressed in name input`() {
            interact { type(KeyCode.ENTER) }
            assertEquals((inputNameText to "").toString(), createLocationRequest.toString())
        }

        @TestFactory
        fun `should disable entire dialog while create location request is processing`() = listOf(
            DynamicTest.dynamicTest("when enter key pressed in name input") {
                interact { type(KeyCode.ENTER) }
                assertTrue(view.isDisable)
            },
            DynamicTest.dynamicTest("when create button is pressed") {
                interact { view.access().createButton.fire() }
                assertTrue(view.isDisable)
            }
        )

        @Test
        fun `should include description in request`() {
            interact {
                view.access().descriptionInput.text = "This description"
                view.access().createButton.fire()
            }
            assertEquals(
                "This description",
                createLocationRequest!!.second
            )
        }

        @Nested
        inner class `When create location request fails`
        {

            init {
                createNewLocationController.deferred = CompletableDeferred()
                threadTransformer.exceptionHandler = CoroutineExceptionHandler { _, failure -> }
                interact { view.access().createButton.fire() }
                createNewLocationController.deferred.completeExceptionally(Error("Some uncaught error"))
                interact {  }
            }

            @Test
            fun `should be ready to generate another request`() {
                assertFalse(view.isDisable)
                assertNull(view.access().nameError)
                assertFalse(view.access().createButton.isDisable)
            }

        }

        @Nested
        inner class `When create location request succeeds`
        {

            private val createLocationId = Location.Id()
            private val createLocationName = "Some Location"

            init {
                createNewLocationController.deferred = CompletableDeferred()
                interact { view.access().createButton.fire() }
                createNewLocationController.deferred.complete(CreateNewLocation.ResponseModel(createLocationId.uuid, createLocationName))
                interact {  }
            }

            @Test
            fun `should hide window`() {
                assertFalse(view.scene.window.isShowing)
            }

            @Test
            fun `should inform caller of created location`() {
                createdLocationResponse!!
            }

        }

    }

    private fun ObservableValue<String>.shouldUpdateToMatch(localeProp: StringProperty, whenChangedTo: String) {
        interact { localeProp.set(whenChangedTo) }
        assertEquals(whenChangedTo, this.value)
    }



}