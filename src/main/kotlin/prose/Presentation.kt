package com.soyle.stories.desktop.config.prose

import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterNotifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.prose.editProse.ContentReplacedNotifier
import com.soyle.stories.prose.invalidateRemovedMentions.DetectInvalidatedMentionsOutput
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedNotifier
import com.soyle.stories.prose.proseEditor.ProseEditorController
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.proseEditor.ProseEditorViewListener
import com.soyle.stories.theme.deleteTheme.ThemeDeletedNotifier
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeNotifier

object Presentation {

    init {
        scoped<ProseEditorScope> {
            provide<ProseEditorViewListener> {
                ProseEditorController(
                    proseId,
                    get<ProseEditorState>(),
                    projectScope.get(),
                    projectScope.get(),
                    get(),
                    onLoadMentionQuery,
                    onUseStoryElement,
                    onLoadMentionReplacements
                ).also {
                    it listensTo projectScope.get<ContentReplacedNotifier>()
                    it listensTo projectScope.get<MentionTextReplacedNotifier>()
                    it listensTo projectScope.get<DetectInvalidatedMentionsOutput>()
                    it listensTo projectScope.get<RemovedCharacterNotifier>()
                    it listensTo projectScope.get<DeletedLocationNotifier>()
                    it listensTo projectScope.get<ThemeDeletedNotifier>()
                    it listensTo projectScope.get<SymbolRemovedFromThemeNotifier>()
                }
            }
        }
    }

}