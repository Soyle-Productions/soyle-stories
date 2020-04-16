package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import java.util.*

sealed class Tool<T>(
    override val id: Id,
    val identifyingData: T,
    val isOpen: Boolean,
    val type: ToolType,
    val associatedData: Any? = null
) : Entity<Tool.Id> {

    data class Id(val uuid: UUID) {
        override fun toString(): String = "Tool($uuid)"
    }

    abstract fun open(): Tool<T>
    abstract fun close(): Tool<T>
}

class CharacterListTool(id: Tool.Id, projectId: Project.Id, isOpen: Boolean) :
    Tool<Project.Id>(id, projectId, isOpen, ToolType.CharacterList, null) {
    override fun open(): Tool<Project.Id> =
        CharacterListTool(id, identifyingData, true)
    override fun close(): Tool<Project.Id> =
        CharacterListTool(id, identifyingData, false)

}
class LocationListTool(id: Id, projectId: Project.Id, isOpen: Boolean) :
  Tool<Project.Id>(id, projectId, isOpen, ToolType.LocationList, null) {
    override fun open(): Tool<Project.Id> =
      LocationListTool(id, identifyingData, true)
    override fun close(): Tool<Project.Id> =
      LocationListTool(id, identifyingData, false)
}

class BaseStoryStructureTool(id: Tool.Id, themeId: Theme.Id, characterId: Character.Id, isOpen: Boolean) :
    Tool<Pair<Theme.Id, Character.Id>>(id, themeId to characterId, isOpen, ToolType.BaseStoryStructure, null) {
    override fun open(): Tool<Pair<Theme.Id, Character.Id>> =
        BaseStoryStructureTool(id, identifyingData.first, identifyingData.second, true)

    override fun close(): Tool<Pair<Theme.Id, Character.Id>> =
        BaseStoryStructureTool(id, identifyingData.first, identifyingData.second, false)
}

class CharacterComparisonTool(id: Tool.Id, themeId: Theme.Id, characterId: Character.Id?, isOpen: Boolean) :
    Tool<Theme.Id>(id, themeId, isOpen, ToolType.CharacterComparison, characterId) {
    override fun open(): Tool<Theme.Id> =
        CharacterComparisonTool(id, identifyingData, associatedData as Character.Id?, true)

    override fun close(): Tool<Theme.Id> =
        CharacterComparisonTool(id, identifyingData, associatedData as Character.Id?, false)
}