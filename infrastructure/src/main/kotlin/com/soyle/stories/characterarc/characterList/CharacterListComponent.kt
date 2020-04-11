package com.soyle.stories.characterarc.characterList

import com.soyle.stories.common.ThreadTransformerImpl
import com.soyle.stories.di.characterarc.CharacterArcComponent
import com.soyle.stories.di.project.LayoutComponent
import com.soyle.stories.project.ProjectScope
import tornadofx.Component
import tornadofx.ScopedInstance

class CharacterListComponent : Component(), ScopedInstance {

	override val scope: ProjectScope = super.scope as ProjectScope
	private val characterArcComponent by inject<CharacterArcComponent>()
	private val layoutComponent: LayoutComponent by inject()

	private val characterListPresenter by lazy {
		CharacterListPresenter(
		  ThreadTransformerImpl,
		  find<CharacterListModel>(),
		  characterArcComponent.characterArcEvents
		)
	}

	val characterListViewListener: CharacterListViewListener by lazy {
		CharacterListController(
		  ThreadTransformerImpl,
		  characterArcComponent.listAllCharacterArcs,
		  characterListPresenter,
		  layoutComponent.openTool,
		  layoutComponent.openToolOutputPort,
		  characterArcComponent.removeCharacterFromLocalStory,
		  characterArcComponent.removeCharacterFromStoryOutputPort,
		  characterArcComponent.deleteLocalCharacterArc,
		  characterArcComponent.deleteLocalCharacterArcOutputPort,
		  characterArcComponent.renameCharacter,
		  characterArcComponent.renameCharacterOutputPort,
		  characterArcComponent.renameCharacterArc,
		  characterArcComponent.renameCharacterArcOutputPort
		)
	}

}