package com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene

import com.soyle.stories.characterarc.repositories.CharacterArcTemplateRepository
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreateCharacterArcAndCoverSectionsInScene.*
import java.util.*

class CreateCharacterArcAndCoverSectionsInSceneUseCase(
    private val characterArcTemplateRepository: CharacterArcTemplateRepository
) : CreateCharacterArcAndCoverSectionsInScene {

    override suspend fun listAvailableCharacterArcSectionTypesForCharacterArc(output: OutputPort) {
        getDefaultCharacterArcTemplate()
            .takeOnlyRequiredArcSections()
            .convertEachToResponseModel()
            .let(::RequiredCharacterArcSectionTypes)
            .let { output.receiveRequiredCharacterArcSectionTypes(it) }
    }

    private suspend fun getDefaultCharacterArcTemplate() = characterArcTemplateRepository.getDefaultTemplate()

    private fun CharacterArcTemplate.takeOnlyRequiredArcSections() = sections.filter { it.isRequired }

    private fun List<CharacterArcTemplateSection>.convertEachToResponseModel() = map {
        RequiredCharacterArcSectionType(
            it.id.uuid, it.name
        )
    }

    override suspend fun invoke() {

    }

}