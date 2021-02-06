package com.soyle.stories.domain.scene


import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

class Scene private constructor(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val storyEventId: StoryEvent.Id,
    val settings: Set<Location.Id>,
    val proseId: Prose.Id,
    private val charactersInScene: List<CharacterInScene>,
    private val symbols: Collection<TrackedSymbol>,

    defaultConstructorMarker: Unit = Unit
) : Entity<Scene.Id> {

    constructor(
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id
    ) : this(Id(), projectId, name, storyEventId, setOf(), proseId, listOf(), listOf())

    constructor(
        id: Id,
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        settings: Set<Location.Id>,
        proseId: Prose.Id,
        charactersInScene: List<CharacterInScene>,
        symbols: Collection<TrackedSymbol>
    ) : this(
        id, projectId, name, storyEventId, settings, proseId, charactersInScene, symbols, defaultConstructorMarker = Unit
    ) {
        if (trackedSymbols.size != symbols.size) {
            error("Cannot track the same symbol more than once in a scene.\n${symbols.groupBy { it.symbolId }.filter { it.value.size > 1 }}")
        }
    }

    private val charactersById by lazy { charactersInScene.associateBy { it.characterId } }

    val trackedSymbols: TrackedSymbols by lazy { TrackedSymbols() }

    fun includesCharacter(characterId: Character.Id): Boolean {
        return charactersById.containsKey(characterId)
    }

    fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation? {
        return charactersById[characterId]?.let {
            CharacterMotivation(it.characterId, it.characterName, it.motivation)
        }
    }

    fun getCoveredCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection.Id>? {
        return charactersById[characterId]?.coveredArcSections
    }

    private val allCharacterArcSections by lazy {
        charactersById.values.flatMap { it.coveredArcSections }.toSet()
    }

    fun isCharacterArcSectionCovered(characterArcSectionId: CharacterArcSection.Id): Boolean {
        return allCharacterArcSections.contains(characterArcSectionId)
    }

    val includedCharacters: List<IncludedCharacter> by lazy {
        charactersInScene.map { IncludedCharacter(it.characterId, it.characterName) }
    }

    val coveredArcSectionIds by lazy {
        charactersInScene.flatMap { it.coveredArcSections }
    }

    fun hasCharacters(): Boolean = charactersInScene.isNotEmpty()

    private fun copy(
        name: NonBlankString = this.name,
        settings: Set<Location.Id> = this.settings,
        charactersInScene: List<CharacterInScene> = this.charactersInScene,
        symbols: Collection<TrackedSymbol> = this.symbols
    ) = Scene(id, projectId, name, storyEventId, settings, this.proseId, charactersInScene, symbols, defaultConstructorMarker = Unit)

    fun withName(newName: NonBlankString) = copy(name = newName)

    fun withCharacterIncluded(character: Character): Scene {
        if (includesCharacter(character.id)) throw SceneAlreadyContainsCharacter(id.uuid, character.id.uuid)
        return copy(
            charactersInScene = charactersInScene + CharacterInScene(
                character.id,
                id,
                character.name.value,
                null,
                listOf()
            )
        )
    }

    fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene {
        if (!includesCharacter(characterId)) throw CharacterNotInScene(id.uuid, characterId.uuid)
        return copy(charactersInScene = charactersInScene.map {
            if (it.characterId == characterId) CharacterInScene(
                it.characterId,
                id,
                it.characterName,
                motivation,
                listOf()
            )
            else it
        })
    }

    fun withLocationLinked(locationId: Location.Id) = copy(settings = settings + locationId)
    fun withoutLocation(locationId: Location.Id) = copy(settings = settings.minus(locationId))
    fun withoutCharacter(characterId: Character.Id) =
        copy(charactersInScene = charactersInScene.filterNot { it.characterId == characterId })

    fun withCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        charactersById[characterArcSection.characterId] ?: throw CharacterNotInScene(
            id.uuid,
            characterArcSection.characterId.uuid
        )
        return copy(
            charactersInScene = charactersInScene.map {
                if (it.characterId != characterArcSection.characterId) it
                else it.withCoveredArcSection(characterArcSection)
            }
        )
    }

    fun withoutCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        return copy(
            charactersInScene = charactersInScene.map {
                if (it.characterId != characterArcSection.characterId) it
                else it.withoutCoveredArcSection(characterArcSection)
            }
        )
    }

    fun withSymbolTracked(theme: Theme, symbol: Symbol, pin: Boolean = false): SceneUpdate<SymbolTrackedInScene> {
        theme.symbols.find { it.id == symbol.id } ?: throw IllegalArgumentException("Symbol ${symbol.name} is not contained within the ${theme.name} theme")
        val newTrackedSymbol = TrackedSymbol(symbol.id, symbol.name, theme.id, pin)
        return if (trackedSymbols.isSymbolTracked(symbol.id)) noUpdate()
        else {
            Updated(copy(symbols = symbols + newTrackedSymbol), SymbolTrackedInScene(id, theme.name, newTrackedSymbol))
        }
    }

    fun withSymbolRenamed(symbolId: Symbol.Id, newName: String): SceneUpdate<TrackedSymbolRenamed>
    {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.symbolName == newName) return noUpdate()
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId)!!.copy(symbolName = newName)
        return Updated(copy(symbols = symbols + trackedSymbol), TrackedSymbolRenamed(id, trackedSymbol))
    }

    fun withSymbolPinned(symbolId: Symbol.Id): SceneUpdate<SymbolPinnedToScene>
    {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = true)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolPinnedToScene(id, trackedSymbol))
    }

    fun withSymbolUnpinned(symbolId: Symbol.Id): SceneUpdate<SymbolUnpinnedFromScene>
    {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (! existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = false)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolUnpinnedFromScene(id, trackedSymbol))
    }

    fun withoutSymbolTracked(symbolId: Symbol.Id): SceneUpdate<TrackedSymbolRemoved> {
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId) ?: return noUpdate()
        return Updated(copy(symbols = trackedSymbols.withoutSymbol(symbolId)), TrackedSymbolRemoved(id, trackedSymbol))
    }

    fun noUpdate() = NoUpdate(this)

    data class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun toString(): String = "Scene($uuid)"
    }

    class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {
        fun isInherited() = motivation == null
    }

    class IncludedCharacter(val characterId: Character.Id, val characterName: String)

    inner class TrackedSymbols private constructor(private val symbolsById: Map<Symbol.Id, TrackedSymbol>) : Collection<TrackedSymbol> by symbolsById.values {
        internal constructor() : this(symbols.associateBy { it.symbolId })

        fun isSymbolTracked(symbolId: Symbol.Id): Boolean = symbolsById.containsKey(symbolId)
        fun getSymbolById(symbolId: Symbol.Id) = symbolsById[symbolId]
        fun getSymbolByIdOrError(symbolId: Symbol.Id) = symbolsById.getOrElse(symbolId) { throw SceneDoesNotTrackSymbol(id, symbolId) }
        internal fun withoutSymbol(symbolId: Symbol.Id): Collection<TrackedSymbol> = symbolsById.minus(symbolId).values
    }

    data class TrackedSymbol(val symbolId: Symbol.Id, val symbolName: String, val themeId: Theme.Id, val isPinned: Boolean = false)
}

sealed class SceneUpdate<out T> {
    abstract val scene: Scene
    operator fun component1() = scene
}
class NoUpdate(override val scene: Scene) : SceneUpdate<Nothing>()
class Updated<out T : SceneEvent>(override val scene: Scene, val event: T) : SceneUpdate<T>() {
    operator fun component2() = event
}

abstract class SceneEvent
{
    abstract val sceneId: Scene.Id
}
data class SymbolTrackedInScene(override val sceneId: Scene.Id, val themeName: String, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()
data class SymbolPinnedToScene(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()
data class SymbolUnpinnedFromScene(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()
data class TrackedSymbolRenamed(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()
data class TrackedSymbolRemoved(override val sceneId: Scene.Id, val trackedSymbol: Scene.TrackedSymbol) : SceneEvent()