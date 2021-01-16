package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.*
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorAssertions
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorAssertions.Companion.assertThat
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.drive
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorDriver.Companion.driver
import com.soyle.stories.desktop.view.type
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.*
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.scene.control.IndexRange
import javafx.scene.control.ListCell
import javafx.scene.control.TextField
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.input.KeyCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.plusAssign
import java.util.*
import kotlin.reflect.KFunction

class `Prose Editor Unit Test` : ApplicationTest() {

    private val scope =
        ProseEditorScope(ProjectScope(ApplicationScope(), ProjectFileViewModel(UUID.randomUUID(), "", "")), Prose.Id(), { _, _ ->

        }) {

        }
    private val proseEditorView: ProseEditorView
    private val viewListener = object : ProseEditorViewListener {

        private val _callLog = mutableMapOf<KFunction<*>, Map<String, Any?>>()
        private val view
            get() = scope.get<ProseEditorState>()

        fun wasCalled(function: KFunction<*>): Boolean = _callLog.containsKey(function)

        fun getCall(function: KFunction<*>): Map<String, Any?> =
            _callLog[function] ?: throw AssertionError("$function was not called")

        override fun getValidState() {
            _callLog[ProseEditorViewListener::getValidState] = mapOf()
        }

        override fun primeMentionQuery(primedIndex: Int) {
            _callLog[ProseEditorViewListener::primeMentionQuery] = mapOf("primedIndex" to primedIndex)
        }

        override fun cancelQuery() {
            _callLog[ProseEditorViewListener::cancelQuery] = mapOf()
        }

        override fun getStoryElementsContaining(query: NonBlankString) {
            _callLog[ProseEditorViewListener::getStoryElementsContaining] = mapOf(
                "query" to query
            )
        }

        override fun selectStoryElement(filteredListIndex: Int, andUseElement: Boolean) {
            _callLog[ProseEditorViewListener::selectStoryElement] = mapOf("filteredListIndex" to filteredListIndex)
        }

        override fun clearAllMentionsOfEntity(entityId: MentionedEntityId<*>) {
            _callLog[ProseEditorViewListener::clearAllMentionsOfEntity] = mapOf("entityId" to entityId)
        }

        override fun save() {
            _callLog[ProseEditorViewListener::save] = mapOf()
        }
    }

    @Nested
    inner class `When Content is Updated` {

        private val mentions = listOf(
            ProseMention(Character.Id().mentioned(), ProseMentionRange(4, 7)),
            ProseMention(Character.Id().mentioned(), ProseMentionRange(18, 6))
        )

        @Test
        fun `content should be displayed`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("I'm "),
                    Mention("content", mentions[0].entityId),
                    BasicText(" to be "),
                    Mention("tested", mentions[1].entityId)
                ), NoQuery)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                hasContent("I'm content to be tested")
            }
        }

        @Test
        fun `if content is not updated, typed characters should not be overridden`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("I'm content to be tested")
                ), NoQuery)
            }
            proseEditorView.drive {
                textArea.requestFocus()
                textArea.moveTo(4)
                type("starting ")
            }
            // typing does not always finish by the end of an interaction block, so delay a bit to allow it to complete
            runBlocking { delay(100) }
            scope.get<ProseEditorState>().updateOrInvalidated {
                copy(versionNumber = 1L)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                hasContent("I'm starting content to be tested")
            }
        }

    }

    @Nested
    inner class `When Mentions are Updated` {

        private val mentions = listOf(
            ProseMention(Character.Id().mentioned(), ProseMentionRange(4, 7)),
            ProseMention(Character.Id().mentioned(), ProseMentionRange(18, 6))
        )

        @Test
        fun `associated text range should be styled`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("I'm "),
                    Mention("content", mentions[0].entityId),
                    BasicText(" to be "),
                    Mention("tested", mentions[1].entityId)
                ), NoQuery)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                hasMention(mentions[0].entityId, mentions[0].position)
                hasMention(mentions[0].entityId, mentions[0].position)
            }
        }

    }

    @Nested
    inner class `When Mention Query is Updated` {

        @Test
        fun `mention list should not be visible when NoQuery is active`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(BasicText("I'm content to be tested")), NoQuery)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                suggestedMentionListIsNotVisible()
            }
        }

        @Test
        fun `mention list should not be visible when mention query is only primed`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(BasicText("I'm content to be tested")), MentionQueryPrimed(0))
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                suggestedMentionListIsNotVisible()
            }
        }

        @Nested
        inner class `When Mention Query is Loading` {

            @Test
            fun `mention list should be visible`() {
                scope.get<ProseEditorState>().update {
                    ProseEditorViewModel(0L, false, listOf(BasicText("I'm content to be tested")), MentionQueryLoading("B", "B", 0))
                }
                ProseEditorAssertions.assertThat(proseEditorView) {
                    suggestedMentionListIsVisible()
                }
            }

        }

        @Nested
        inner class `When Mention Query has Loaded` {

            @Test
            fun `mention list should be visible`() {
                scope.get<ProseEditorState>().update {
                    ProseEditorViewModel(0L, false, listOf(BasicText("I'm content to be tested")), MentionQueryLoaded("B", "B", 0, listOf(), listOf()))
                }
                ProseEditorAssertions.assertThat(proseEditorView) {
                    suggestedMentionListIsVisible()
                }
            }

            @Test
            fun `mention list should contain prioritized items`() {
                scope.get<ProseEditorState>().update {
                    ProseEditorViewModel(
                        0L,
                        false,
                        listOf(BasicText("I'm content to be tested")),
                        MentionQueryLoaded(
                            "B",
                            "B",
                            0,
                            matchesForInitialQuery = listOf(),
                            prioritizedMatches = listOf(storyElement("Bob"), storyElement("Joe Bob"), storyElement("Robert"))
                        )
                    )
                }
                ProseEditorAssertions.assertThat(proseEditorView) {
                    isListingStoryElement(0, "Bob", "test")
                    isListingStoryElement(1, "Joe Bob", "test")
                    isListingStoryElement(2, "Robert", "test")
                }
            }

        }

    }

    @Nested
    inner class `When Locked`
    {

        @Test
        fun `input should be disabled`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(
                    0L,
                    true,
                    listOf(BasicText("I'm content to be tested")),
                    NoQuery
                )
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                isDisabled()
            }
        }

    }

    @Nested
    inner class `When Focus is Lost`
    {

        private val characterId = Character.Id()

        init {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(
                    0L,
                    false,
                    listOf(
                        BasicText("I'm content"),
                        Mention("content", characterId.mentioned()),
                        BasicText(" to be texted")
                    ),
                    NoQuery
                )
            }
            with(ProseEditorDriver.Companion) {
                proseEditorView.drive {
                    textArea.requestFocus()
                }
            }
        }

        @Test
        fun `should save current state`() {
            proseEditorView.drive {
                val newFocus = TextField()
                textArea.scene.root.plusAssign(newFocus)
                newFocus.requestFocus()
            }
            viewListener.getCall(ProseEditorViewListener::save)
        }

        @Test
        fun `state should be upated by edits`() {
            proseEditorView.drive {
                textArea.requestFocus()
                textArea.moveTo(11)
                type(" that is short ") // typing seems to only take place at the end of an interact block
            }
            interact {
                val newFocus = TextField()
                proseEditorView.driver().textArea.scene.root.plusAssign(newFocus)
                newFocus.requestFocus()
            }

            assertEquals(
                listOf(
                    BasicText("I'm content that is short "),
                    Mention("content", characterId.mentioned()),
                    BasicText(" to be texted")
                ),
                scope.get<ProseEditorState>().content.toList()
            )
        }

    }

    @Nested
    inner class `Rule - Cannot place cursor inside a mention`
    {

        init {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("I'm on one side of "),
                    Mention("Frank", Character.Id().mentioned()),
                    BasicText(" and I'm on the other"),
                ), NoQuery)
            }
        }

        @Nested
        inner class `When Mouse clicked inside`
        {

            @Test
            fun `when closer to beginning of mention, should move to beginning of mention`() {
                proseEditorView.drive {
                    textArea.moveTo(21)
                }

                assertEquals(19, proseEditorView.driver().textArea.caretPosition)
            }


            @Test
            fun `when closer to end of mention, should move to end of mention`() {
                proseEditorView.drive {
                    textArea.moveTo(22)
                }

                assertEquals(24, proseEditorView.driver().textArea.caretPosition)
            }
        }

        @Nested
        inner class `When mouse selection dragged over`
        {

            @Test
            fun `should not select mention until over halfway covered by selection`() {
                proseEditorView.drive {
                    textArea.moveTo(3)
                    textArea.caretSelectionBind.underlyingSelection.selectRange(3, 21)
                }
                assertEquals(
                    IndexRange(3, 19), proseEditorView.driver().textArea.selection
                )
            }

            @Test
            fun `Select entire mention once over halfway covered by selection`() {
                proseEditorView.drive {
                    textArea.moveTo(3)
                    textArea.caretSelectionBind.underlyingSelection.selectRange(3, 22)
                }
                assertEquals(
                    IndexRange(3, 24), proseEditorView.driver().textArea.selection
                )
            }

        }

        @Nested
        inner class `When Arrow Keys are pressed at the edge of a mention`
        {

            @Test
            fun `right arrow should jump to end of mention`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(19)
                    type(KeyCode.RIGHT)
                }
                assertEquals(24, proseEditorView.driver().textArea.caretPosition)
            }

            @Test
            fun `left arrow should jump to beginning of mention`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(24)
                    type(KeyCode.LEFT)
                }
                assertEquals(19, proseEditorView.driver().textArea.caretPosition)
            }

            @Test
            fun `shift+right arrow should extend selection to end of mention`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(19)
                    press(KeyCode.SHIFT)
                    type(KeyCode.RIGHT)
                    release(KeyCode.SHIFT)
                }
                assertEquals(IndexRange(19, 24), proseEditorView.driver().textArea.selection)
            }

            @Test
            fun `shift+left arrow should extend selection to end of mention`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(24)
                    press(KeyCode.SHIFT)
                    type(KeyCode.LEFT)
                    release(KeyCode.LEFT)
                }
                assertEquals(IndexRange(19, 24), proseEditorView.driver().textArea.selection)
            }

        }

        @Nested
        inner class `When arrow keys are pressed above or below a mention`
        {

            /* The lines are currently rendered like this:
            I'm on one side of
            Frank and I'm on
            the other
             */

            @Nested
            inner class `When Up Arrow is pressed`
            {

                @Test
                fun `up arrow should jump to nearest edge of mention`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(37)
                        type(KeyCode.UP)
                    }
                    assertEquals(19, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(19, 19), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `up arrow should jump to end of mention if closer`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(39)
                        type(KeyCode.UP)
                    }
                    assertEquals(24, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(24, 24), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `shift+up arrow should extend selection to nearest edge of mention`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(37)
                        press(KeyCode.SHIFT)
                        type(KeyCode.UP)
                        release(KeyCode.SHIFT)
                    }
                    assertEquals(19, proseEditorView.driver().textArea.caretPosition)
                    assertEquals(IndexRange(19, 37), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `shift+up arrow should extend selection to to end of mention if closer`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(39)
                        press(KeyCode.SHIFT)
                        type(KeyCode.UP)
                        release(KeyCode.SHIFT)
                    }
                    assertEquals(24, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(24, 39), proseEditorView.driver().textArea.selection)
                }
            }


            @Nested
            inner class `When Down Arrow is pressed`
            {

                @Test
                fun `down arrow should jump to nearest edge of mention`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(2)
                        type(KeyCode.DOWN)
                    }
                    assertEquals(19, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(19, 19), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `down arrow should jump to end of mention if closer`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(3)
                        type(KeyCode.DOWN)
                    }
                    assertEquals(24, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(24, 24), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `shift+down arrow should extend selection to nearest edge of mention`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(2)
                        press(KeyCode.SHIFT)
                        type(KeyCode.DOWN)
                        release(KeyCode.SHIFT)
                    }
                    assertEquals(19, proseEditorView.driver().textArea.caretPosition)
                    assertEquals(IndexRange(2, 19), proseEditorView.driver().textArea.selection)
                }

                @Test
                fun `shift+down arrow should extend selection to to end of mention if closer`() {
                    proseEditorView.drive {
                        textArea.requestFocus()
                        textArea.moveTo(3)
                        press(KeyCode.SHIFT)
                        type(KeyCode.DOWN)
                        release(KeyCode.SHIFT)
                    }
                    assertEquals(24, proseEditorView.driver().textArea.caretPosition)
                    // should not select anything
                    assertEquals(IndexRange(3, 24), proseEditorView.driver().textArea.selection)
                }
            }

        }

    }

    @Nested
    inner class `Updating state`
    {

        private val frankId = Character.Id()

        init {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("I'm on one side of "),
                    Mention("Frank", frankId.mentioned()),
                    BasicText(" and I'm on the other"),
                ), NoQuery)
            }
        }

        @Nested
        inner class `Enter Text`
        {

            @Test
            fun `typing into a paragraph should update prose after`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(19)
                    type("the character ")
                }

                assertEquals(33, proseEditorView.driver().textArea.caretPosition)
                assertEquals(
                    listOf(
                        BasicText("I'm on one side of the character "),
                        Mention("Frank", frankId.mentioned()),
                        BasicText(" and I'm on the other"),
                    ),
                    proseEditorView.driver().textArea.paragraphs.flatMap { it.segments }
                )
            }

            @Test
            fun `typing into a paragraph should not update mentions before`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(24)
                    type(" and my dog")
                }

                assertEquals(35, proseEditorView.driver().textArea.caretPosition)
                assertEquals(
                    listOf(
                        BasicText("I'm on one side of "),
                        Mention("Frank", frankId.mentioned()),
                        BasicText(" and my dog and I'm on the other"),
                    ),
                    proseEditorView.driver().textArea.paragraphs.flatMap { it.segments }
                )
            }
        }

        @Nested
        inner class `Deleting Mentions`
        {

            @Nested
            inner class `When Delete Key is pressed`
            {

                @Test
                fun `if caret is at beginning of mention, should delete entire mention`() {
                    proseEditorView.drive {
                        textArea.moveTo(19)
                        type(KeyCode.DELETE)
                    }
                    assertThat(proseEditorView) {
                        hasContent("I'm on one side of  and I'm on the other")
                    }
                }

            }

            @Nested
            inner class `When Backspace key is pressed`
            {

                @Test
                fun `if caret is at end of mention, should delete entire mention`() {
                    proseEditorView.drive {
                        textArea.moveTo(24)
                        type(KeyCode.BACK_SPACE)
                    }
                    assertThat(proseEditorView) {
                        hasContent("I'm on one side of  and I'm on the other")
                    }
                }

            }

        }

    }

    @Nested
    inner class `Query for Mentions`
    {

        init {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, false, listOf(
                    BasicText("Starting text")
                ), NoQuery)
            }
        }

        @Test
        fun `when @ symbol typed, should prime query`() {
            proseEditorView.drive {
                textArea.requestFocus()
                textArea.moveTo(9)
                press(KeyCode.SHIFT)
                type(KeyCode.DIGIT2)
                release(KeyCode.SHIFT)
            }

            val callParams = viewListener.getCall(ProseEditorViewListener::primeMentionQuery)
            assertEquals(mapOf("primedIndex" to 9), callParams)
        }

        @Nested
        inner class `When character is typed`
        {

            @Test
            fun `if query is primed, should search for mentions with typed character`() {
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(
                        content = listOf(BasicText("Starting @text")),
                        mentionQueryState = MentionQueryPrimed(9)
                    )
                }
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(10)
                    type(KeyCode.B)
                }

                val callParams = viewListener.getCall(ProseEditorViewListener::getStoryElementsContaining)
                assertEquals(mapOf("query" to NonBlankString.create("b")!!), callParams)
            }

            @Test
            fun `if query is not primed, should not search for mentions`() {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(9)
                    type(KeyCode.B)
                }

                assertFalse(viewListener.wasCalled(ProseEditorViewListener::getStoryElementsContaining))
            }

            @Test
            fun `if there is an active query, should extend the query text`() {
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(
                        content = listOf(BasicText("Starting @btext")),
                        mentionQueryState = MentionQueryLoading("b", "b" , 9)
                    )
                }
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(11)
                    type(KeyCode.R)
                }

                val callParams = viewListener.getCall(ProseEditorViewListener::getStoryElementsContaining)
                assertEquals(mapOf("query" to NonBlankString.create("br")!!), callParams)
            }

            @Test
            fun `if query is primed and @ symbol is typed, should not re-prime`() {
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(
                        content = listOf(BasicText("Starting @text")),
                        mentionQueryState = MentionQueryPrimed(9)
                    )
                }
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(10)
                    press(KeyCode.SHIFT)
                    type(KeyCode.DIGIT2)
                    release(KeyCode.SHIFT)
                }

                assertFalse(viewListener.wasCalled(ProseEditorViewListener::primeMentionQuery))
                val callParams = viewListener.getCall(ProseEditorViewListener::getStoryElementsContaining)
                assertEquals(mapOf("query" to NonBlankString.create("@")!!), callParams)
            }

        }



        @Nested
        inner class `Selecting Mention Option`
        {

            init {
                proseEditorView.drive {
                    textArea.requestFocus()
                    textArea.moveTo(9)
                    press(KeyCode.SHIFT)
                    type(KeyCode.DIGIT2)
                    release(KeyCode.SHIFT)
                }
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(mentionQueryState = MentionQueryPrimed(9))
                }
                proseEditorView.drive {
                    type(KeyCode.B)
                }
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(mentionQueryState = MentionQueryLoading("b", "b", 9))
                }
                scope.get<ProseEditorState>().updateOrInvalidated {
                    copy(
                        mentionQueryState = MentionQueryLoaded("b", "b", 9, listOf(), listOf(
                            MatchingStoryElementViewModel(countLines("Bob") as SingleLine, 0..1, "character", Character.Id().mentioned()),
                            MatchingStoryElementViewModel(countLines("Billy") as SingleLine, 0..1, "character", Character.Id().mentioned()),
                            MatchingStoryElementViewModel(countLines("Boyd") as SingleLine, 0..1, "character", Character.Id().mentioned())
                        ))
                    )
                }
            }

            @Test
            fun `mention query setup should not have modified input text`() {
                assertEquals("Starting @btext", proseEditorView.driver().textArea.text)
            }

            @Test
            fun `pressing enter should call viewListener to select query option`() {
                proseEditorView.drive {
                    type(KeyCode.ENTER)
                }

                val callParams = viewListener.getCall(ProseEditorViewListener::selectStoryElement)
                assertEquals(mapOf("filteredListIndex" to 0), callParams)
            }

            @Test
            fun `double clicking should call viewListener to select query option`() {
                proseEditorView.drive {
                    val cellFlow = mentionMenuList.childrenUnmodifiable.first() as VirtualFlow<ListCell<*>>
                    doubleClickOn(cellFlow.getVisibleCell(1))
                }

                val callParams = viewListener.getCall(ProseEditorViewListener::selectStoryElement)
                assertEquals(mapOf("filteredListIndex" to 1), callParams)
            }

        }

    }

    @Test
    fun playground() {
        scope.get<ProseEditorState>().update {
            ProseEditorViewModel(0L, false, listOf(
                BasicText("I'm on one side of "),
                Mention("Frank", Character.Id().mentioned()),
                BasicText(" and I'm on the other"),
            ), NoQuery)
        }
        while(proseEditorView.currentStage?.isShowing == true) {}
    }

    init {
        //runHeadless()
        scoped<ApplicationScope> {
            provide<ThreadTransformer> { SyncThreadTransformer() }
        }
        scoped<ProseEditorScope> {
            provide<ProseEditorViewListener> {
                viewListener
            }
        }
        FX.setPrimaryStage(FX.defaultScope, FxToolkit.registerPrimaryStage())
        proseEditorView = scope.get()
        interact {
            proseEditorView.openWindow()
            FX.applyStylesheetsTo(proseEditorView.root.scene)
        }
    }

    @AfterEach
    fun `close window`() {
        interact {
            proseEditorView.close()
        }
    }

    private fun storyElement(name: String): MatchingStoryElementViewModel
    {
        return MatchingStoryElementViewModel(countLines(name) as SingleLine, 0..1, "test", Character.Id().mentioned())
    }

}