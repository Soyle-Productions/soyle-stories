package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class BuildNewCharacterControllerImpl(
    projectId: String,
    private val threadTransformer: ThreadTransformer,
    private val buildNewCharacter: BuildNewCharacter,
    private val buildNewCharacterOutputPort: BuildNewCharacter.OutputPort
) : BuildNewCharacterController {

    private val projectId = UUID.fromString(projectId)

    override fun buildNewCharacter(
        name: String,
        includeInTheme: String?,
        useAsOpponentForCharacter: String?,
        onError: (Throwable) -> Unit
    ) {
        val preparedThemeId = includeInTheme?.let(UUID::fromString)
        val preparedPerspectiveCharacterId = useAsOpponentForCharacter?.let(UUID::fromString)
        threadTransformer.async {
            try {
                if (preparedThemeId != null) {
                    if (preparedPerspectiveCharacterId != null) {
                        buildNewCharacter.createAndUseAsOpponent(
                            name,
                            preparedThemeId,
                            preparedPerspectiveCharacterId,
                            buildNewCharacterOutputPort
                        )
                    } else {
                        buildNewCharacter.createAndIncludeInTheme(name, preparedThemeId, buildNewCharacterOutputPort)
                    }
                } else {
                    buildNewCharacter.invoke(projectId, name, buildNewCharacterOutputPort)
                }
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

}