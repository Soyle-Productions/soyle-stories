package com.soyle.stories.domain.scene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.*
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.validation.entitySetOf
import java.util.*

class Scene private constructor(
    override val id: Id,
    val projectId: Project.Id,
    val name: NonBlankString,
    val coveredStoryEvents: Set<StoryEvent.Id>,
    val settings: EntitySet<SceneSettingLocation>,
    val proseId: Prose.Id,
    private val charactersInScene: EntitySet<CharacterInScene>,
    private val symbols: Collection<TrackedSymbol>,
    val conflict: SceneConflict,
    val resolution: SceneResolution,

    @Suppress("UNUSED_PARAMETER")
    defaultConstructorMarker: Unit = Unit
) : Entity<Scene.Id> {

    companion object {
        @JvmStatic
        internal fun create(
            projectId: Project.Id,
            name: NonBlankString,
            storyEventId: StoryEvent.Id,
            proseId: Prose.Id
        ): SceneUpdate<SceneCreated> {
            val newScene = Scene(projectId, name, storyEventId, proseId)
            return Updated(newScene, SceneCreated(newScene.id, newScene.name.value, proseId, storyEventId))
        }

        @JvmStatic
        private val equalityProps
            get() = listOf(
                Scene::id,
                Scene::projectId,
                Scene::name,
                Scene::coveredStoryEvents,
                Scene::settings,
                Scene::proseId,
                Scene::charactersInScene,
                Scene::symbols,
                Scene::conflict,
                Scene::resolution
            )
    }

    private constructor(
        projectId: Project.Id,
        name: NonBlankString,
        storyEventId: StoryEvent.Id,
        proseId: Prose.Id
    ) : this(
        Id(),
        projectId,
        name,
        setOf(storyEventId),
        entitySetOf(),
        proseId,
        entitySetOf(),
        listOf(),
        SceneConflict(""),
        SceneResolution("")
    )

    constructor(
        id: Id,
        projectId: Project.Id,
        name: NonBlankString,
        coveredStoryEvents: Set<StoryEvent.Id>,
        settings: EntitySet<SceneSettingLocation>,
        proseId: Prose.Id,
        charactersInScene: EntitySet<CharacterInScene>,
        symbols: Collection<TrackedSymbol>,
        conflict: SceneConflict,
        resolution: SceneResolution,
    ) : this(
        id,
        projectId,
        name,
        coveredStoryEvents,
        settings,
        proseId,
        charactersInScene,
        symbols,
        conflict,
        resolution,
        defaultConstructorMarker = Unit
    ) {
        if (trackedSymbols.size != symbols.size) {
            error(
                "Cannot track the same symbol more than once in a scene.\n${
                    symbols.groupBy { it.symbolId }.filter { it.value.size > 1 }
                }"
            )
        }
    }

    val includedCharacters by lazy { IncludedCharacters() }
    val trackedSymbols: TrackedSymbols by lazy { TrackedSymbols() }

    fun includesCharacter(characterId: Character.Id): Boolean {
        return charactersInScene.containsEntityWithId(characterId)
    }

    fun getMotivationForCharacter(characterId: Character.Id): CharacterMotivation? {
        return charactersInScene.getEntityById(characterId)?.let {
            CharacterMotivation(it.characterId, it.characterName, it.motivation)
        }
    }

    fun getCoveredCharacterArcSectionsForCharacter(characterId: Character.Id): List<CharacterArcSection.Id>? {
        return charactersInScene.getEntityById(characterId)?.coveredArcSections
    }

    private val coveredArcSectionIds by lazy {
        charactersInScene.flatMap { it.coveredArcSections }.toSet()
    }

    fun isCharacterArcSectionCovered(characterArcSectionId: CharacterArcSection.Id): Boolean {
        return coveredArcSectionIds.contains(characterArcSectionId)
    }

    fun hasCharacters(): Boolean = charactersInScene.isNotEmpty()

    operator fun contains(locationId: Location.Id) = settings.containsEntityWithId(locationId)

    private fun copy(
        name: NonBlankString = this.name,
        settings: EntitySet<SceneSettingLocation> = this.settings,
        charactersInScene: EntitySet<CharacterInScene> = this.charactersInScene,
        symbols: Collection<TrackedSymbol> = this.symbols,
        conflict: SceneConflict = this.conflict,
        resolution: SceneResolution = this.resolution,
        coveredStoryEvents: Set<StoryEvent.Id> = this.coveredStoryEvents
    ) = Scene(
        id,
        projectId,
        name,
        coveredStoryEvents,
        settings,
        this.proseId,
        charactersInScene,
        symbols,
        conflict,
        resolution,
        defaultConstructorMarker = Unit
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Scene

        return equalityProps.all { it.get(this) == it.get(other) }
    }

    private val cachedHashCode: Int by lazy {
        equalityProps.drop(1)
            .fold(equalityProps.first().get(this).hashCode()) { result, prop ->
                31 * result + prop.get(this).hashCode()
            }
    }

    override fun hashCode(): Int = cachedHashCode

    override fun toString(): String {
        return "Scene(${equalityProps.joinToString(", ") { "${it.name}=${it.get(this)}" }})"
    }

    fun withName(newName: NonBlankString): SceneUpdate<SceneRenamed> {
        if (newName == name) return noUpdate()
        return Updated(copy(name = newName), SceneRenamed(id, newName.value))
    }

    fun withSceneFrameValue(value: SceneFrameValue): SceneUpdate<SceneFrameValueChanged> {
        when (value) {
            is SceneConflict -> {
                if (value == conflict) return WithoutChange(this)
                return Updated(copy(conflict = value), SceneFrameValueChanged(id, value))
            }
            is SceneResolution -> {
                if (value == resolution) return WithoutChange(this)
                return Updated(copy(resolution = value), SceneFrameValueChanged(id, value))
            }
        }
    }

    fun withCharacterIncluded(character: Character): SceneUpdate<IncludedCharacterInScene> {
        if (includesCharacter(character.id)) return noUpdate()
        val characterInScene = CharacterInScene(id, character.id, character.name.value)
        return Updated(
            copy(
                charactersInScene = charactersInScene + characterInScene
            ),
            IncludedCharacterInScene(id, IncludedCharacter(character.id, character.name.value))
        )
    }

    fun withCharacterRenamed(character: Character): SceneUpdate<RenamedCharacterInScene> {
        val characterInScene = includedCharacters.getOrError(character.id)
        if (characterInScene.characterName == character.name.value) return noUpdate()

        return Updated(
            copy(
                charactersInScene = charactersInScene
                    .minus(character.id)
                    .plus(characterInScene.withName(character.name.value))
            ),
            RenamedCharacterInScene(id, IncludedCharacter(character.id, character.name.value))
        )
    }

    fun withRoleForCharacter(
        characterId: Character.Id,
        roleInScene: RoleInScene?
    ): SceneUpdate<CompoundEvent<CharacterRoleInSceneChanged>> {
        val characterInScene = includedCharacters.getOrError(characterId)
        if (characterInScene.roleInScene == roleInScene) return noUpdate()

        val newCharacter = characterInScene.withRoleInScene(roleInScene)
        val event = when (roleInScene) {
            null -> CharacterRoleInSceneCleared(id, characterId)
            else -> CharacterAssignedRoleInScene(id, characterId, roleInScene)
        }

        if (roleInScene == RoleInScene.IncitingCharacter && includedCharacters.incitingCharacter != null) {
            return withRoleForCharacter(includedCharacters.incitingCharacter!!.id, null)
                .then { withRoleForCharacter(characterId, roleInScene) }
        }

        return Updated(
            copy(charactersInScene = charactersInScene.minus(characterId).plus(newCharacter)),
            CompoundEvent(listOf(event))
        )
    }

    fun withDesireForCharacter(characterId: Character.Id, desire: String): SceneUpdate<CharacterDesireInSceneChanged> {
        val includedCharacter = includedCharacters.getOrError(characterId)
        if (includedCharacter.desire == desire) return noUpdate()
        return Updated(
            copy(charactersInScene = charactersInScene.minus(characterId).plus(includedCharacter.withDesire(desire))),
            CharacterDesireInSceneChanged(id, characterId, desire)
        )
    }

    fun withMotivationForCharacter(characterId: Character.Id, motivation: String?): Scene {
        val characterInScene = includedCharacters.getOrError(characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterId)
                .plus(characterInScene.withMotivation(motivation))
        )
    }

    fun withLocationLinked(locationId: Location.Id, locationName: String): SceneUpdate<LocationUsedInScene> {
        if (settings.containsEntityWithId(locationId)) return noUpdate()
        val sceneSetting = SceneSettingLocation(locationId, locationName)
        return Updated(copy(settings = settings + sceneSetting), LocationUsedInScene(id, sceneSetting))
    }

    fun withLocationLinked(location: Location): SceneUpdate<LocationUsedInScene> =
        withLocationLinked(location.id, location.name.value)

    fun withoutLocation(locationId: Location.Id): SceneUpdate<LocationRemovedFromScene> {
        val sceneSetting = settings.getEntityById(locationId) ?: return noUpdate()
        return Updated(
            copy(settings = settings.minus(sceneSetting)),
            LocationRemovedFromScene(id, sceneSetting)
        )
    }

    fun withLocationRenamed(locationId: Location.Id, locationName: String): SceneUpdate<SceneSettingLocationRenamed> {
        val sceneSetting = getSceneSettingOrError(locationId)
        if (sceneSetting.locationName == locationName) return noUpdate()
        val newSceneSetting = sceneSetting.copy(locationName = locationName)
        return Updated(
            copy(settings = settings.minus(sceneSetting).plus(newSceneSetting)),
            SceneSettingLocationRenamed(id, newSceneSetting)
        )
    }

    fun withLocationRenamed(location: Location): SceneUpdate<SceneSettingLocationRenamed> =
        withLocationRenamed(location.id, location.name.value)

    fun withSetting(settingId: Location.Id): SceneSettingLocationOperations? {
        val sceneSetting = settings.getEntityById(settingId) ?: return null
        return object : SceneSettingLocationOperations {
            override fun replacedWith(location: Location): SceneUpdate<LocationRemovedFromScene> {
                if (location.id == sceneSetting.id) return noUpdate(
                    reason = SceneSettingCannotBeReplacedBySameLocation(id, location.id)
                )
                return Updated(
                    copy(
                        settings = settings
                            .minus(sceneSetting)
                            .plus(SceneSettingLocation(location.id, location.name.value))
                    ),
                    LocationRemovedFromScene(
                        id,
                        sceneSetting.id,
                        replacedBy = LocationUsedInScene(id, location.id, location.name.value)
                    )
                )
            }
        }
    }

    private fun getSceneSettingOrError(locationId: Location.Id): SceneSettingLocation {
        return settings.getEntityById(locationId) ?: throw SceneDoesNotUseLocation(id, locationId)
    }

    fun withoutCharacter(characterId: Character.Id) =
        copy(charactersInScene = charactersInScene.minus(characterId))

    fun withStoryEvent(storyEventId: StoryEvent.Id): SceneUpdate<StoryEventAddedToScene> {
        if (storyEventId in coveredStoryEvents) return noUpdate(SceneAlreadyCoversStoryEvent(id, storyEventId))
        return copy(coveredStoryEvents = coveredStoryEvents + storyEventId)
            .updatedBy(StoryEventAddedToScene(id, storyEventId))
    }

    fun withoutStoryEvent(storyEventId: StoryEvent.Id): SceneUpdate<StoryEventRemovedFromScene> {
        if (storyEventId !in coveredStoryEvents) return noUpdate(SceneDoesNotCoverStoryEvent(id, storyEventId))
        return copy(coveredStoryEvents = coveredStoryEvents - storyEventId)
            .updatedBy(StoryEventRemovedFromScene(id, storyEventId))
    }

    fun withCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        val characterInScene = includedCharacters.getOrError(characterArcSection.characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterInScene.characterId)
                .plus(characterInScene.withCoveredArcSection(characterArcSection))
        )
    }

    fun withoutCharacterArcSectionCovered(characterArcSection: CharacterArcSection): Scene {
        val characterInScene = includedCharacters.getOrError(characterArcSection.characterId)
        return copy(
            charactersInScene = charactersInScene
                .minus(characterInScene.characterId)
                .plus(characterInScene.withoutCoveredArcSection(characterArcSection))
        )
    }

    fun withSymbolTracked(theme: Theme, symbol: Symbol, pin: Boolean = false): SceneUpdate<SymbolTrackedInScene> {
        theme.symbols.find { it.id == symbol.id }
            ?: throw IllegalArgumentException("Symbol ${symbol.name} is not contained within the ${theme.name} theme")
        val newTrackedSymbol = TrackedSymbol(symbol.id, symbol.name, theme.id, pin)
        return if (trackedSymbols.isSymbolTracked(symbol.id)) noUpdate()
        else {
            Updated(copy(symbols = symbols + newTrackedSymbol), SymbolTrackedInScene(id, theme.name, newTrackedSymbol))
        }
    }

    fun withSymbolRenamed(symbolId: Symbol.Id, newName: String): SceneUpdate<TrackedSymbolRenamed> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.symbolName == newName) return noUpdate()
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId)!!.copy(symbolName = newName)
        return Updated(copy(symbols = symbols + trackedSymbol), TrackedSymbolRenamed(id, trackedSymbol))
    }

    fun withSymbolPinned(symbolId: Symbol.Id): SceneUpdate<SymbolPinnedToScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = true)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolPinnedToScene(id, trackedSymbol))
    }

    fun withSymbolUnpinned(symbolId: Symbol.Id): SceneUpdate<SymbolUnpinnedFromScene> {
        val existingSymbol = trackedSymbols.getSymbolByIdOrError(symbolId)
        if (!existingSymbol.isPinned) return noUpdate()
        val trackedSymbol = existingSymbol.copy(isPinned = false)
        return Updated(copy(symbols = symbols + trackedSymbol), SymbolUnpinnedFromScene(id, trackedSymbol))
    }

    fun withoutSymbolTracked(symbolId: Symbol.Id): SceneUpdate<TrackedSymbolRemoved> {
        val trackedSymbol = trackedSymbols.getSymbolById(symbolId) ?: return noUpdate()
        return Updated(copy(symbols = trackedSymbols.withoutSymbol(symbolId)), TrackedSymbolRemoved(id, trackedSymbol))
    }

    private fun <E : SceneEvent> updatedBy(event: E) = Updated<E>(this, event)
    fun noUpdate(reason: Throwable? = null) = WithoutChange(this, reason)

    data class Id(val uuid: UUID = UUID.randomUUID()) {

        override fun toString(): String = "Scene($uuid)"
    }

    class CharacterMotivation(val characterId: Character.Id, val characterName: String, val motivation: String?) {

        fun isInherited() = motivation == null
    }

    class IncludedCharacter(val characterId: Character.Id, val characterName: String)

    inner class IncludedCharacters internal constructor() : Collection<CharacterInScene> by charactersInScene {

        val incitingCharacter by lazy { includedCharacters.find { it.roleInScene == RoleInScene.IncitingCharacter } }

        operator fun get(characterId: Character.Id) = charactersInScene.getEntityById(characterId)
        fun getOrError(characterId: Character.Id): CharacterInScene =
            get(characterId) ?: throw SceneDoesNotIncludeCharacter(id, characterId)
    }

    inner class TrackedSymbols private constructor(private val symbolsById: Map<Symbol.Id, TrackedSymbol>) :
        Collection<TrackedSymbol> by symbolsById.values {

        internal constructor() : this(symbols.associateBy { it.symbolId })

        fun isSymbolTracked(symbolId: Symbol.Id): Boolean = symbolsById.containsKey(symbolId)
        fun getSymbolById(symbolId: Symbol.Id) = symbolsById[symbolId]
        fun getSymbolByIdOrError(symbolId: Symbol.Id) =
            symbolsById.getOrElse(symbolId) { throw SceneDoesNotTrackSymbol(id, symbolId) }

        internal fun withoutSymbol(symbolId: Symbol.Id): Collection<TrackedSymbol> = symbolsById.minus(symbolId).values
    }

    interface SceneSettingLocationOperations {

        fun replacedWith(location: Location): SceneUpdate<LocationRemovedFromScene>
    }

    data class TrackedSymbol(
        val symbolId: Symbol.Id,
        val symbolName: String,
        val themeId: Theme.Id,
        val isPinned: Boolean = false
    )
}
