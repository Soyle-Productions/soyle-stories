package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.EntityId
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.common.SyncThreadTransformer
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.prose.proseEditor.ProseEditorAssertions
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.ProseMention
import com.soyle.stories.entities.ProseMentionRange
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import java.util.*
import kotlin.reflect.KFunction

class `Prose Editor Unit Test` : ApplicationTest() {

    private val scope =
        ProseEditorScope(ProjectScope(ApplicationScope(), ProjectFileViewModel(UUID.randomUUID(), "", "")), Prose.Id())
    private val proseEditorView: ProseEditorView
    private val viewListener = object : ProseEditorViewListener {

        private val _callLog = mutableMapOf<KFunction<*>, Map<String, Any?>>()
        private val view
            get() = scope.get<ProseEditorState>()

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
                "query" to query.toString()
            )
        }
    }

    @Nested
    inner class `When Content is Updated` {

        @Test
        fun `content should be displayed`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, "I'm content to be tested", listOf(), NoQuery)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                hasContent("I'm content to be tested")
            }
        }

    }

    @Nested
    inner class `When Mentions are Updated` {

        private val mentions = listOf(
            ProseMention(EntityId.of(Prose.create().prose), ProseMentionRange(4, 7)),
            ProseMention(EntityId.of(Prose.create().prose), ProseMentionRange(18, 6))
        )

        @Test
        fun `associated text range should be styled`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, "I'm content to be tested", mentions, NoQuery)
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
                ProseEditorViewModel(0L, "I'm content to be tested", listOf(), NoQuery)
            }
            ProseEditorAssertions.assertThat(proseEditorView) {
                suggestedMentionListIsNotVisible()
            }
        }

        @Test
        fun `mention list should not be visible when mention query is only primed`() {
            scope.get<ProseEditorState>().update {
                ProseEditorViewModel(0L, "I'm content to be tested", listOf(), MentionQueryPrimed(0))
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
                    ProseEditorViewModel(0L, "I'm content to be tested", listOf(), MentionQueryLoading("B", "B", 0))
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
                    ProseEditorViewModel(0L, "I'm content to be tested", listOf(), MentionQueryLoaded("B", "B", 0, listOf(), listOf()))
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
                        "I'm content to be tested",
                        listOf(),
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

            private fun storyElement(name: String): MatchingStoryElementViewModel
            {
                return MatchingStoryElementViewModel(name, 0..1, "test", EntityId.of(Character::class).id(Character.Id()))
            }

        }

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
        }
    }

    @AfterEach
    fun `close window`() {
        interact {
            proseEditorView.close()
        }
    }

}