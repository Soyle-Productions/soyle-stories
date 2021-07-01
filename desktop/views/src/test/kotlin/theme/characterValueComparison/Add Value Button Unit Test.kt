package com.soyle.stories.desktop.view.theme.characterValueComparison

import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.theme.characterComparison.addValueButton.AddValueButtonLocaleMock
import com.soyle.stories.desktop.view.theme.characterComparison.doubles.ListAvailableOppositionValuesForCharacterInThemeControllerDouble
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.characterValueComparison.components.addValueButton.AddValueButton
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.desktop.view.theme.characterComparison.addValueButton.`Add Value Button Access`.Companion.access
import com.soyle.stories.desktop.view.theme.characterComparison.addValueButton.`Add Value Button Access`.Companion.drive
import com.soyle.stories.desktop.view.theme.characterComparison.doubles.AddSymbolicItemToOppositionControllerDouble
import com.soyle.stories.desktop.view.theme.valueWeb.create.CreateValueWebFormFactory
import com.soyle.stories.desktop.view.theme.valueWeb.create.getOpenCreateValueWebDialog
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.CreateOppositionValueFormFactory
import com.soyle.stories.desktop.view.theme.valueWeb.opposition.create.getOpenCreateOppositionValueDialog
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.theme.addSymbolicItemToOpposition.AddSymbolicItemToOppositionController
import com.soyle.stories.theme.valueWeb.create.CreateValueWebForm
import com.soyle.stories.theme.valueWeb.opposition.create.CreateOppositionValueForm
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.OppositionAddedToValueWeb
import com.soyle.stories.usecase.theme.addValueWebToTheme.ValueWebAddedToTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableOppositionValueForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.AvailableValueWebForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.OppositionValuesAvailableForCharacterInTheme
import com.soyle.stories.usecase.theme.listOppositionsInValueWeb.OppositionValueItem
import javafx.scene.control.CheckBox
import javafx.scene.control.RadioButton
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tornadofx.hasClass
import java.util.*

class `Add Value Button Unit Test` : NodeTest<AddValueButton>() {

    private val themeId = Theme.Id()
    private val characterId = Character.Id()

    private var loadAvailableOppositionsOutput: ListAvailableOppositionValuesForCharacterInTheme.OutputPort? = null
    private var onCreateValueWeb: (suspend (ValueWebAddedToTheme) -> Unit)? = null
    private var applyOppositionValueRequest: OppositionValue.Id? = null
    private var createOppositionValueForValueWebRequestId: ValueWeb.Id? = null
    private var onCreateOppositionValue: (suspend (OppositionAddedToValueWeb) -> Unit)? = null

    private val locale = AddValueButtonLocaleMock()
    override val view: AddValueButton = AddValueButton(
        themeId,
        characterId,
        locale,
        ListAvailableOppositionValuesForCharacterInThemeControllerDouble(
            onInvoke = { themeId, characterId, output ->
                assertEquals(this.themeId, themeId)
                assertEquals(this.characterId, characterId)
                loadAvailableOppositionsOutput = output
            }
        ),
        AddSymbolicItemToOppositionControllerDouble(
            onAddCharacterToOpposition = { oppositionId: String, characterId: String ->
                assertEquals(this.characterId.uuid.toString(), characterId)
                applyOppositionValueRequest = OppositionValue.Id(UUID.fromString(oppositionId))
            },
            onAddLocationToOpposition = { _, _ -> fail("Should not have added location to opposition") },
            onAddSymbolToOpposition = { _, _ -> fail("Should not have added symbol to opposition") }
        ),
        CreateValueWebFormFactory(
            onInvoke = { themeId, callback ->
                assertEquals(this.themeId, themeId)
                onCreateValueWeb = callback
            }
        ),
        CreateOppositionValueFormFactory(
            onInvoke = { valueWebId, it ->
                createOppositionValueForValueWebRequestId = valueWebId
                onCreateOppositionValue = it
            }
        )
    )

    init {
        showView()
        interact {
            listWindows().asSequence()
                .filter { it.scene.window?.isShowing == true }
                .filter { it.scene.root is CreateOppositionValueForm || it.scene.root is CreateValueWebForm }
                .forEach { it.hide() }
        }
    }

    @Test
    fun `should be loading given not yet shown`() {
        assertTrue(view.hasClass(loading))
    }

    @Test
    fun `should display text from locale`() {
        interact { locale.addValue.set("Put a new value in here") }
        assertEquals("Put a new value in here", view.text)
    }

    @Test
    fun `should load items when shown`() {
        interact { view.fire() }
        assertNotNull(loadAvailableOppositionsOutput)

        interact { locale.loading.set("Be with you in a moment") }
        view.access().loadingItem!!.run {
            assertTrue(isDisable)
            assertEquals("Be with you in a moment", text)
        }
    }

    @Nested
    inner class `Given was Opened` {

        init {
            if (!view.isShowing) interact { view.show() }
        }

        @Test
        fun `should show create value web option when loaded`() {
            loadAvailableValueWebs(emptyList())

            // expected wrong thread exception, but nothing was thrown.  If this test fails later, wrap in interact {  }
            locale.createNewValueWeb.set("Let's make another value web")
            view.access().createValueWebItem!!.run {
                assertEquals("Let's make another value web", text)
            }
        }

        @Test
        fun `should not be loading when loaded`() {
            loadAvailableValueWebs(emptyList())

            assertFalse(view.hasClass(loading))
            assertNull(view.access().loadingItem)
        }

        @Test
        fun `should show no available value web item when loaded value webs is empty`() {
            loadAvailableValueWebs(emptyList())

            // expected wrong thread exception, but nothing was thrown.  If this test fails later, wrap in interact {  }
            locale.themeHasNoValueWebs.set("This theme be empty, yo")
            view.access().noAvailableValueWebsItem!!.run {
                assertTrue(isDisable)
                assertEquals("This theme be empty, yo", text)
            }
        }

        @Test
        fun `should open create value web dialog when create value web item is selected`() {
            loadAvailableValueWebs(emptyList())
            interact { view.access().createValueWebItem!!.fire() }

            assertNotNull(getOpenCreateValueWebDialog())
            assertNotNull(onCreateValueWeb)
        }

        @Nested
        inner class `Given Create Value Web Dialog has been Opened` {

            init {
                loadAvailableValueWebs(emptyList())
                interact { view.access().createValueWebItem!!.fire() }
            }

            @Test
            fun `should apply first opposition value from created value web to character`() {
                val expectedOppositionId = OppositionValue.Id()
                valueWebAdded(createdOppositionId = expectedOppositionId.uuid)

                assertEquals(expectedOppositionId, applyOppositionValueRequest)
            }

            @Test
            fun `should close the create value web dialog when value web is created`() {
                valueWebAdded()

                assertNull(getOpenCreateValueWebDialog())
            }

            private fun valueWebAdded(createdOppositionId: UUID = UUID.randomUUID()) {
                val valueWebId = UUID.randomUUID()
                runBlocking {
                    onCreateValueWeb!!.invoke(
                        ValueWebAddedToTheme(
                            themeId.uuid,
                            valueWebId,
                            "",
                            OppositionAddedToValueWeb(themeId.uuid, valueWebId, createdOppositionId, "", false)
                        )
                    )
                }
            }
        }

        @Nested
        inner class `Given Value Webs are Available` {

            private val valueWebs = List(5) { ValueWeb(themeId, NonBlankString.create("value web $it")!!) }
            private val valueWebIdSet = valueWebs.map { it.id.toString() }.toSet()

            init {
                loadAvailableValueWebs(valueWebs.map {
                    AvailableValueWebForCharacterInTheme(
                        it.id.uuid,
                        it.name.value,
                        null,
                        emptyList()
                    )
                })
            }

            @Test
            fun `should show all available value webs`() {
                assertNull(view.access().noAvailableValueWebsItem)

                assertEquals(5, view.access().valueWebItems.size)
                assertEquals(valueWebIdSet, view.access().valueWebItems.map { it.id }.toSet())
                view.access().valueWebItems.forEach { menuItem ->
                    val backingValueWeb = valueWebs.single { it.id.toString() == menuItem.id }
                    assertEquals(backingValueWeb.name.value, menuItem.text)
                }
            }

            @Test
            fun `should provide create opposition value option for each value web`() {
                view.access {
                    valueWebItems.forEach {
                        it.createOppositionValueItem
                    }
                }

                // expected wrong thread exception, but nothing was thrown.  If this test fails later, wrap in interact {  }
                locale.createOppositionValue.set("Make another opposition")
                view.access {
                    valueWebItems.forEach {
                        assertEquals("Make another opposition", it.createOppositionValueItem.text)
                    }
                }
            }

            @Test
            fun `should open create opposition value dialog when create opposition item is selected`() {
                val valueWeb = valueWebs.random()
                view.drive {
                    valueWebItems.single { it.id == valueWeb.id.toString() }.createOppositionValueItem.fire()
                }

                assertNotNull(getOpenCreateOppositionValueDialog())
                assertEquals(valueWeb.id, createOppositionValueForValueWebRequestId)
                assertNotNull(onCreateOppositionValue)
            }

            @Nested
            inner class `Given Create Opposition Value Dialog has been Opened` {

                private val valueWeb = valueWebs.random()

                init {
                    view.drive {
                        valueWebItems.single { it.id == valueWeb.id.toString() }.createOppositionValueItem.fire()
                    }
                }

                @Test
                fun `should apply created opposition value to character`() {
                    val expectedOppositionId = OppositionValue.Id()
                    oppositionValueAdded(createdOppositionId = expectedOppositionId.uuid)

                    assertEquals(expectedOppositionId, applyOppositionValueRequest)
                }

                @Test
                fun `should close create opposition value dialog`() {

                    oppositionValueAdded(createdOppositionId = UUID.randomUUID())

                    interact {  }

                    assertNull(getOpenCreateOppositionValueDialog())
                }

                private fun oppositionValueAdded(createdOppositionId: UUID) {
                    runBlocking {
                        onCreateOppositionValue!!.invoke(
                            OppositionAddedToValueWeb(
                                themeId.uuid,
                                valueWeb.id.uuid,
                                createdOppositionId,
                                "",
                                false
                            )
                        )
                    }
                }
            }

            @Nested
            inner class `Given Value Webs have Available Oppositions` {

                private val oppositionValues = valueWebs.associate { web ->
                    web.id to List((1..6).random()) {
                        OppositionValue(NonBlankString.create("Opposition Value ${web.id} $it")!!)
                    }
                }

                init {
                    loadAvailableValueWebs(valueWebs.map {
                        AvailableValueWebForCharacterInTheme(
                            it.id.uuid,
                            it.name.value,
                            null,
                            oppositionValues.getValue(it.id).map {
                                AvailableOppositionValueForCharacterInTheme(
                                    it.id.uuid,
                                    it.name.value
                                )
                            }
                        )
                    })
                }

                @Test
                fun `should display all available opposition values for each value web`() {
                    view.access {
                        valueWebItems.forEach { menu ->
                            val backingValueWeb = valueWebs.single { it.id.toString() == menu.id }
                            val backingOppositions = oppositionValues.getValue(backingValueWeb.id)

                            assertEquals(backingOppositions.size, menu.oppositionValueItems.size)
                            assertEquals(
                                backingOppositions.map { it.id.toString() }.toSet(),
                                menu.oppositionValueItems.map { it.id }.toSet()
                            )
                            menu.oppositionValueItems.forEach { menuItem ->
                                val backingOppositionValue =
                                    backingOppositions.single { it.id.toString() == menuItem.id }
                                assertEquals(backingOppositionValue.name.value, menuItem.text)
                            }
                        }
                    }
                }

                @Test
                fun `should apply opposition value to character when available opposition value is selected`() {
                    val valueWeb = valueWebs.random()
                    val oppositionValue = oppositionValues.getValue(valueWeb.id).random()
                    view.drive {
                        valueWebItems.single { it.id == valueWeb.id.toString() }
                            .oppositionValueItems.single { it.id == oppositionValue.id.toString() }
                            .fire()
                    }

                    assertEquals(oppositionValue.id, applyOppositionValueRequest)
                }

                @Nested
                inner class `Given Character Already has Opposition Value for Value Web` {

                    val selectedOppositionValues = valueWebs.associate {
                        it.id to oppositionValues.getValue(it.id).random()
                    }
                    val selectedOppositionValueIds = selectedOppositionValues.map { it.value.id.toString() }.toSet()

                    init {
                        loadAvailableValueWebs(valueWebs.map {
                            AvailableValueWebForCharacterInTheme(
                                it.id.uuid,
                                it.name.value,
                                selectedOppositionValues.getValue(it.id).let {
                                    OppositionValueItem(it.id.uuid, it.name.value)
                                },
                                oppositionValues.getValue(it.id).map {
                                    AvailableOppositionValueForCharacterInTheme(
                                        it.id.uuid,
                                        it.name.value
                                    )
                                }
                            )
                        })
                    }

                    @Test
                    fun `should show all other opposition values as unselected`() {
                        view.access {
                            valueWebItems.forEach { menu ->
                                menu.oppositionValueItems.filterNot { it.id in selectedOppositionValueIds }
                                    .forEach {
                                        assertFalse((it.graphic as RadioButton).isSelected)
                                    }
                            }
                        }
                    }

                    @Test
                    fun `should opposition value as selected`() {
                        view.access {
                            valueWebItems.forEach { menu ->
                                val item = menu.oppositionValueItems.single { it.id in selectedOppositionValueIds }
                                assertTrue((item.graphic as RadioButton).isSelected)
                            }
                        }
                    }

                }

            }

        }

        private fun loadAvailableValueWebs(valueWebs: List<AvailableValueWebForCharacterInTheme>) {
            runBlocking {
                loadAvailableOppositionsOutput!!.availableOppositionValuesListedForCharacterInTheme(
                    OppositionValuesAvailableForCharacterInTheme(
                        themeId.uuid,
                        characterId.uuid,
                        valueWebs
                    )
                )
            }
        }

    }

}