package com.soyle.stories.project.layout

import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.tools.ToolType
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

data class WindowViewModel(val id: String, val child: WindowChildViewModel)
sealed class WindowChildViewModel {
    abstract val id: String
}
data class GroupSplitterViewModel(val splitterId: String, val orientation: Boolean, val children: List<Pair<Int, WindowChildViewModel>>) : WindowChildViewModel() {
    override val id: String
        get() = splitterId
}
data class ToolGroupViewModel(val groupId: String, val focusedToolId: String?, val tools: List<ToolViewModel>) : WindowChildViewModel() {
    override val id: String
        get() = groupId

    override fun toString(): String {
        return "${super.toString()}(groupId = $groupId, focusedToolId = $focusedToolId, tools = $tools)"
    }
}

data class StaticToolViewModel(val type: FixedTool, val isOpen: Boolean, val name: String)
data class ToolViewModel(val toolId: String, val type: ToolType, val name: String)

sealed class Dialog {
    object CreateCharacter : Dialog()
    object CreateCharacterArc : Dialog()
    object CreateLocation : Dialog()

    object DeleteCharacter : Dialog()
    object DeleteCharacterArc : Dialog()
    class DeleteLocation(val locationId: String, val locationName: String) : Dialog()
}