package com.soyle.stories.theme.usecases.changeThemeDetails

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.repositories.getThemeOrError
import com.soyle.stories.theme.usecases.validateThemeName
import java.util.*

class ChangeThemeDetailsUseCase(
    private val themeRepository: ThemeRepository
) : RenameTheme, ChangeCentralConflict, ChangeCentralMoralQuestion, ChangeThemeLine, ChangeThematicRevelation {

    override suspend fun invoke(themeId: UUID, name: String, output: RenameTheme.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        validateThemeName(name)
        themeRepository.updateTheme(theme.withName(name))
        output.themeRenamed(RenamedTheme(themeId, theme.name, name))
    }

    override suspend fun invoke(themeId: UUID, centralConflict: String, output: ChangeCentralConflict.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        themeRepository.updateTheme(theme.withCentralConflict(centralConflict))
        output.centralConflictChanged(
            ChangeCentralConflict.ResponseModel(
                CentralConflictChanged(
                    themeId,
                    centralConflict
                )
            )
        )
    }

    override suspend fun invoke(themeId: UUID, question: String, output: ChangeCentralMoralQuestion.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        if (theme.centralMoralProblem != question) {
            themeRepository.updateTheme(theme.withMoralProblem(question))
        }
        output.centralMoralQuestionChanged(
            ChangeCentralMoralQuestion.ResponseModel(
                ChangedCentralMoralQuestion(themeId, question)
            )
        )
    }

    override suspend fun invoke(themeId: UUID, themeLine: String, output: ChangeThemeLine.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        themeRepository.updateTheme(theme.withThemeLine(themeLine))
        output.themeLineChanged(ChangeThemeLine.ResponseModel(ChangedThemeLine(themeId, themeLine)))
    }

    override suspend fun invoke(themeId: UUID, revelation: String, output: ChangeThematicRevelation.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        themeRepository.updateTheme(theme.withThematicRevelation(revelation))
        output.thematicRevelationChanged(
            ChangeThematicRevelation.ResponseModel(
                ChangedThematicRevelation(
                    themeId,
                    revelation
                )
            )
        )
    }

}