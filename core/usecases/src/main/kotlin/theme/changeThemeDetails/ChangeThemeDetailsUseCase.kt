package com.soyle.stories.usecase.theme.changeThemeDetails

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ChangeThemeDetailsUseCase(
    private val themeRepository: ThemeRepository
) : RenameTheme, ChangeCentralConflict, ChangeCentralMoralQuestion, ChangeThemeLine, ChangeThematicRevelation {

    override suspend fun invoke(themeId: UUID, name: NonBlankString, output: RenameTheme.OutputPort) {
        val theme = themeRepository.getThemeOrError(Theme.Id(themeId))
        themeRepository.updateTheme(theme.withName(name.value))
        output.themeRenamed(RenamedTheme(themeId, theme.name, name.value))
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