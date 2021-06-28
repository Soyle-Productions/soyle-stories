package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.*
import com.soyle.stories.common.EntityId.Companion.asIdOf
import com.soyle.stories.entities.*
import com.soyle.stories.gui.View
import com.soyle.stories.prose.ContentReplaced
import com.soyle.stories.prose.TextInsertedIntoProse
import com.soyle.stories.prose.editProse.EditProseController
import com.soyle.stories.prose.readProse.ReadProseController
import com.soyle.stories.prose.usecases.readProse.ReadProse
import com.soyle.stories.scene.usecases.getStoryElementsToMention.GetStoryElementsToMentionInScene
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction

class `Prose Editor Controller Unit Test` {

    private val proseId = Prose.Id()
    private var viewModel: ProseEditorViewModel? = null
    private val view = object : View.Nullable<ProseEditorViewModel> {

        @Synchronized
        override fun update(update: ProseEditorViewModel?.() -> ProseEditorViewModel) {
            this@`Prose Editor Controller Unit Test`.viewModel = viewModel.update()
        }

        @Synchronized
        override fun updateOrInvalidated(update: ProseEditorViewModel.() -> ProseEditorViewModel) {
            this@`Prose Editor Controller Unit Test`.viewModel = viewModel?.update()
        }

        override val viewModel: ProseEditorViewModel?
            get() = this@`Prose Editor Controller Unit Test`.viewModel
    }
    private val readProseController = object : ReadProseController {

        private var proseRevisionNumber = 0L
        private var proseBody = ""
        private var proseMentions = listOf<ProseMention<*>>()

        fun givenProseRevisionNumber(revision: Long) {
            proseRevisionNumber = revision
        }

        fun givenProseBody(body: String) {
            proseBody = body
        }

        fun givenProseMentions(mentions: List<ProseMention<*>>) {
            proseMentions = mentions
        }

        override fun readProse(proseId: Prose.Id, receiver: ReadProse.OutputPort) {
            runBlocking {
                receiver.receiveProse(ReadProse.ResponseModel(proseId, proseRevisionNumber, proseBody, proseMentions))
            }
        }
    }
    private val editProseController = object : EditProseController {

        private val callLog = mutableMapOf<KFunction<*>, Map<String, Any?>>()
        private val callJobs = mutableMapOf<KFunction<*>, CompletableJob>()
        fun getCall(function: KFunction<*>): Map<String, Any?> =
            callLog[function] ?: throw AssertionError("$function was not called")

        fun completeCall(function: KFunction<*>, exception: Throwable? = null) {
            val job = callJobs[function] ?: throw AssertionError("$function was not called")
            if (exception == null) job.complete()
            else job.completeExceptionally(exception)
        }

        override fun updateProse(proseId: Prose.Id, content: List<ProseContent>): Job {
            val job = Job()
            callJobs[EditProseController::updateProse] = job
            callLog[EditProseController::updateProse] = mapOf("proseId" to proseId, "content" to content)
            return job
        }
    }
    private val potentialMentionsLoader = object : (NonBlankString, OnLoadMentionQueryOutput) -> Unit {

        private val options = mutableListOf<GetStoryElementsToMentionInScene.MatchingStoryElement>()

        fun givenMentionOption(entityId: EntityId<*>, name: String) {
            options.add(GetStoryElementsToMentionInScene.MatchingStoryElement(entityId, name))
        }

        override fun invoke(query: NonBlankString, output: OnLoadMentionQueryOutput) {
            output.invoke(options.filter { it.name.contains(query, ignoreCase = true) })
        }
    }
    private val controller = ProseEditorController(
        proseId,
        view,
        readProseController,
        editProseController,
        potentialMentionsLoader
    ) {}

    @Nested
    inner class `When State is Invalidated` {

        private val bobId = Character.Id().asIdOf(Character::class)

        init {
            readProseController.givenProseRevisionNumber(42L)
            readProseController.givenProseBody("I'm the prose body\nthat mentions Bob, the character and\nstarts another line")
            readProseController.givenProseMentions(listOf(ProseMention(bobId, ProseMentionRange(33, 3))))
        }


        @Test
        fun `should read prose`() {
            controller.getValidState()

            viewModel!!.let {
                assertEquals(42L, it.versionNumber)
                assertEquals(
                    listOf(
                        BasicText("I'm the prose body\nthat mentions"),
                        Mention("Bob", bobId),
                        BasicText(", the character and\nstarts another line")
                    ), it.content
                )
            }
        }

        @Test
        fun `should handle mentions up against end of body`() {
            val frankId = Character.Id().asIdOf(Character::class)
            readProseController.givenProseBody("I'm the prose body that mentions Bob, and Frank")
            readProseController.givenProseMentions(listOf(
                ProseMention(bobId, ProseMentionRange(33, 3)),
                ProseMention(frankId, ProseMentionRange(42, 5))
            ))
            controller.getValidState()

            viewModel!!.let {
                assertEquals(42L, it.versionNumber)
                assertEquals(
                    listOf(
                        BasicText("I'm the prose body that mentions "),
                        Mention("Bob", bobId),
                        BasicText(", and "),
                        Mention("Frank", frankId),
                    ), it.content
                )
            }
        }

    }

    @Nested
    inner class `Text Inserted Event Received` {

        private val bobId = Character.Id().asIdOf(Character::class)

        init {
            readProseController.givenProseBody("Starting content")
        }

        @Test
        fun `should update version number and text`() {
            controller.getValidState()
            runBlocking {
                controller.receiveContentReplacedEvent(
                    ContentReplaced(
                        Prose.build(
                            proseId,
                            "I'm the prose body that mentions Bob, the character",
                            listOf(ProseMention(bobId, ProseMentionRange(33, 3))),
                            1L
                        )
                    )
                )
            }

            viewModel!!.let {
                assertEquals(
                    listOf(
                        BasicText("I'm the prose body that mentions "),
                        Mention("Bob", bobId),
                        BasicText(", the character")
                    ), it.content)
            }
        }

        @Test
        fun `wrong prose id event should have no effect`() {
            controller.getValidState()
            runBlocking {
                controller.receiveContentReplacedEvent(
                    ContentReplaced(
                        Prose.build(
                            Prose.Id(),
                            "I'm the prose body that mentions Bob, the character",
                            listOf(ProseMention(bobId, ProseMentionRange(33, 3))),
                            1L
                        )
                    )
                )
            }

            viewModel!!.let {
                assertEquals(0L, it.versionNumber)
                assertEquals("Starting content", it.content.single().text)
            }
        }

    }

    @Nested
    inner class `Select Story Element from Query List` {

        private val billyId = EntityId.of(Character::class).id(Character.Id())

        init {
            controller.getValidState()

        }

        @Test
        fun `should add new mention to view model and remove @ symbol`() {
            view.updateOrInvalidated { copy(content = listOf(BasicText("@"))) } // simulate view typing the @ symbol
            controller.primeMentionQuery(0)

            potentialMentionsLoader.givenMentionOption(EntityId.of(Character::class).id(Character.Id()), "Bob")
            potentialMentionsLoader.givenMentionOption(billyId, "Billy")
            potentialMentionsLoader.givenMentionOption(EntityId.of(Character::class).id(Character.Id()), "Boyd")

            view.updateOrInvalidated { copy(content = listOf(BasicText("@B"))) } // simulate view typing
            controller.getStoryElementsContaining(NonBlankString.create("B")!!)
            controller.selectStoryElement(1, false)
            assertEquals(
                listOf(Mention("Billy", billyId)),
                viewModel!!.content
            )
        }

        @Test
        fun `should only replace content in query`() {
            view.updateOrInvalidated { copy(content = listOf(BasicText("Let's talk about  because he's cool"))) }
            view.updateOrInvalidated { copy(content = listOf(BasicText("Let's talk about @ because he's cool"))) } // simulate view typing the @ symbol
            controller.primeMentionQuery(17)

            potentialMentionsLoader.givenMentionOption(EntityId.of(Character::class).id(Character.Id()), "Bob")
            potentialMentionsLoader.givenMentionOption(billyId, "Billy")
            potentialMentionsLoader.givenMentionOption(EntityId.of(Character::class).id(Character.Id()), "Boyd")

            view.updateOrInvalidated { copy(content = listOf(BasicText("Let's talk about @B because he's cool"))) } // simulate view typing
            controller.getStoryElementsContaining(NonBlankString.create("B")!!)
            controller.selectStoryElement(1, false)
            assertEquals(
                listOf(
                    BasicText("Let's talk about "),
                    Mention("Billy", billyId),
                    BasicText(" because he's cool")
                ),
                viewModel!!.content
            )
        }


    }

    @Nested
    inner class `Save` {

        init {
            readProseController.givenProseBody("Starting content")
        }

        @Test
        fun `should update view model content immediately`() {
            controller.getValidState()
            view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
            controller.save()

            viewModel!!.let {
                assertEquals(0L, it.versionNumber) // should not have updated version number yet
                assertEquals("Starting content with", it.content.single().text)
            }
        }

        @Test
        fun `when associated event is returned, should only update version number`() {
            controller.getValidState()
            view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
            controller.save()
            runBlocking {
                controller.receiveContentReplacedEvent(
                    ContentReplaced(
                        Prose.build(
                            proseId,
                            "Starting content with",
                            listOf(),
                            1L
                        )
                    )
                )
            }

            viewModel!!.let {
                assertEquals(1L, it.versionNumber)
                assertEquals("Starting content with", it.content.single().text)
            }
        }

        @Nested
        inner class `call edit prose controller` {
            @Test
            fun `No mention should send the entire content string`() {
                controller.getValidState()
                view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
                controller.save()

                val updateProseCall = editProseController.getCall(EditProseController::updateProse)
                assertEquals(proseId, updateProseCall.getValue("proseId"))
                assertEquals(
                    listOf(
                        ProseContent("Starting content with", null)
                    ), updateProseCall.getValue("content")
                )
            }

            @Test
            fun `adjacent basic text blocks should be joined with a newline character`() {
                val bobId = Character.Id().asIdOf(Character::class)
                controller.getValidState()
                view.updateOrInvalidated { copy(content = listOf(
                    BasicText("Starting content with"),
                    BasicText("another line of text that mentions "),
                    Mention("Bob", bobId),
                    BasicText(" and then has"),
                    BasicText("another line of text after")
                )) }
                controller.save()

                val updateProseCall = editProseController.getCall(EditProseController::updateProse)
                assertEquals(proseId, updateProseCall.getValue("proseId"))
                assertEquals(
                    listOf(
                        ProseContent("Starting content with\nanother line of text that mentions ", bobId to countLines("Bob") as SingleLine),
                        ProseContent(" and then has\nanother line of text after", null)
                    ), updateProseCall.getValue("content")
                )
            }

            @Test
            fun `should lock input until event is received`() {
                controller.getValidState()
                view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
                controller.save()
                assertTrue(viewModel!!.isLocked)
            }

            @Test
            fun `should unlock when event is received`() {
                controller.getValidState()
                view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
                controller.save()
                runBlocking {
                    controller.receiveContentReplacedEvent(
                        ContentReplaced(
                            Prose.build(
                                proseId,
                                "Starting content with",
                                listOf(),
                                1L
                            )
                        )
                    )
                }
                assertFalse(viewModel!!.isLocked)
            }

            @Test
            fun `unlock if failed to save`() {
                controller.getValidState()
                view.updateOrInvalidated { copy(content = listOf(BasicText("${content.single().text} with"))) }
                controller.save()
                editProseController.completeCall(EditProseController::updateProse, object : SoyleStoriesException() {})
                assertFalse(viewModel!!.isLocked)
            }

            @Test
            fun `Should break up content by mentions`() {
                controller.getValidState()
                val characterEntityId = EntityId.of(Character::class)
                val bobId = Character.Id()
                val frankId = Character.Id()
                view.updateOrInvalidated {
                    copy(
                        content = listOf(
                            Mention("Bob", characterEntityId.id(bobId)),
                            BasicText(" is annoying, but "),
                            Mention("Frank", characterEntityId.id(frankId)),
                            BasicText(" thinks he's fine.")
                        )
                    )
                }
                controller.save()

                val updateProseCall = editProseController.getCall(EditProseController::updateProse)
                assertEquals(proseId, updateProseCall.getValue("proseId"))
                assertEquals(
                    listOf(
                        ProseContent("", characterEntityId.id(bobId) to countLines("Bob") as SingleLine),
                        ProseContent(
                            " is annoying, but ",
                            characterEntityId.id(frankId) to countLines("Frank") as SingleLine
                        ),
                        ProseContent(" thinks he's fine.", null)
                    ), updateProseCall.getValue("content")
                )
            }
        }

    }

    private fun textInsertedIntoProse(
        revision: Long,
        insertedText: String,
        index: Int,
        proseId: Prose.Id = this.proseId
    ) =
        TextInsertedIntoProse(Prose.build(proseId, "", listOf(), revision), insertedText, index)

}