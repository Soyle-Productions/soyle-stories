package com.soyle.stories.project.layout

import kotlin.reflect.KClass

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 2:49 PM
 */
data class LayoutViewModel(
    val staticTools: List<StaticToolViewModel> = emptyList(),
    val primaryWindow: WindowViewModel? = null,
    val secondaryWindows: List<WindowViewModel> = emptyList(),
    val isValid: Boolean = false,
    val openDialogs: Map<KClass<out Dialog>, Dialog> = mapOf()
)

class WindowViewModel(val id: String, val child: WindowChildViewModel)
sealed class WindowChildViewModel {
    abstract val id: String
}
class GroupSplitterViewModel(val splitterId: String, val orientation: Boolean, val children: List<Pair<Int, WindowChildViewModel>>) : WindowChildViewModel() {
    override val id: String
        get() = splitterId
}
class ToolGroupViewModel(val groupId: String, val focusedToolId: String?, val tools: List<ToolViewModel>) : WindowChildViewModel() {
    override val id: String
        get() = groupId

    override fun toString(): String {
        return "${super.toString()}(groupId = $groupId, focusedToolId = $focusedToolId, tools = $tools)"
    }
}
class StaticToolViewModel(val toolId: String, val isOpen: Boolean, val name: String)
sealed class ToolViewModel {
    abstract val toolId: String
}
class CharacterListToolViewModel(override val toolId: String) : ToolViewModel()
class LocationListToolViewModel(override val toolId: String) : ToolViewModel()
class SceneListToolViewModel(override val toolId: String) : ToolViewModel()
class StoryEventListToolViewModel(override val toolId: String) : ToolViewModel()

class BaseStoryStructureToolViewModel(override val toolId: String, val characterId: String, val themeId: String) : ToolViewModel()
class CharacterComparisonToolViewModel(override val toolId: String, val themeId: String, val characterId: String) : ToolViewModel()
class LocationDetailsToolViewModel(override val toolId: String, val locationId: String) : ToolViewModel()
class StoryEventDetailsToolViewModel(override val toolId: String, val storyEventId: String) : ToolViewModel()

sealed class Dialog {
    object CreateCharacter : Dialog()
    object CreateCharacterArc : Dialog()
    object CreateLocation : Dialog()

    object DeleteCharacter : Dialog()
    object DeleteCharacterArc : Dialog()
    class DeleteLocation(val locationId: String, val locationName: String) : Dialog()
}