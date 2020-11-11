package com.soyle.stories.theme.moralArgument

import kotlin.reflect.KFunction

class MoralArgumentViewListenerMock  : MoralArgumentViewListener {

    private val _callLog = mutableMapOf<KFunction<*>, Map<String, Any?>>()
    val callLog: Map<KFunction<*>, Map<String, Any?>>
        get() = _callLog.toMap()

    override fun getValidState() {
        _callLog[MoralArgumentViewListener::getValidState] = mapOf()
    }

    override fun getPerspectiveCharacters() {
        _callLog[MoralArgumentViewListener::getPerspectiveCharacters] = mapOf()
    }

    override fun outlineMoralArgument(characterId: String) {
        _callLog[MoralArgumentViewListener::outlineMoralArgument] = mapOf(
            "characterId" to characterId
        )
    }

    override fun getAvailableArcSectionTypesToAdd(characterId: String) {
        _callLog[MoralArgumentViewListener::getAvailableArcSectionTypesToAdd] = mapOf(
            "characterId" to characterId
        )
    }

    override fun addCharacterArcSectionType(characterId: String, sectionTemplateId: String) {
        _callLog[MoralArgumentViewListener::addCharacterArcSectionType] = mapOf(
            "characterId" to characterId,
            "sectionTemplateId" to sectionTemplateId
        )
    }

    override fun addCharacterArcSectionTypeAtIndex(
        characterId: String,
        sectionTemplateId: String,
        index: Int
    ) {
        _callLog[MoralArgumentViewListener::addCharacterArcSectionTypeAtIndex] = mapOf(
            "characterId" to characterId,
            "sectionTemplateId" to sectionTemplateId,
            "index" to index,
        )
    }

    override fun setMoralProblem(problem: String) {
        _callLog[MoralArgumentViewListener::setMoralProblem] = mapOf(
            "problem" to problem
        )
    }

    override fun setThemeLine(themeLine: String) {
        _callLog[MoralArgumentViewListener::setThemeLine] = mapOf(
            "themeLine" to themeLine
        )
    }

    override fun setValueOfArcSection(characterId: String, arcSectionId: String, value: String) {
        _callLog[MoralArgumentViewListener::setValueOfArcSection] = mapOf(
            "characterId" to characterId,
            "arcSectionId" to arcSectionId,
            "value" to value
        )
    }

    override fun setThematicRevelation(revelation: String) {
        _callLog[MoralArgumentViewListener::setThematicRevelation] = mapOf(
            "revelation" to revelation
        )
    }

    override fun moveSectionTo(arcSectionId: String, characterId: String, index: Int) {
        _callLog[MoralArgumentViewListener::moveSectionTo] = mapOf(
            "arcSectionId" to arcSectionId,
            "characterId" to characterId,
            "index" to index
        )
    }

    override fun removeSection(arcSectionId: String) {
        _callLog[MoralArgumentViewListener::removeSection] = mapOf(
            "arcSectionId" to arcSectionId
        )
    }

}