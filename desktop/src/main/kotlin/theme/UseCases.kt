package com.soyle.stories.desktop.config.theme

import com.soyle.stories.desktop.config.InProjectScope
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.changeThemeDetails.ChangeThemeDetailsOutput
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangeCentralMoralQuestionControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangeThematicRevelationController
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangeThematicRevelationControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangeThemeLineController
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangeThemeLineControllerImpl
import com.soyle.stories.theme.removeSymbolFromTheme.RemoveSymbolFromThemeOutput
import com.soyle.stories.theme.valueWeb.opposition.list.ListAvailableOppositionValuesForCharacterInThemeController
import com.soyle.stories.usecase.theme.changeThemeDetails.*
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInTheme
import com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme.ListAvailableOppositionValuesForCharacterInThemeUseCase
import com.soyle.stories.usecase.theme.outlineMoralArgument.GetMoralArgumentFrame
import com.soyle.stories.usecase.theme.outlineMoralArgument.OutlineMoralArgument
import com.soyle.stories.usecase.theme.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.RemoveSymbolFromThemeUseCase

object UseCases {

    init {
        scoped<ProjectScope> {
            provide(
                GetMoralArgumentFrame::class,
                OutlineMoralArgumentForCharacterInTheme::class
            ) {
                OutlineMoralArgument(get(), get())
            }

            provide<ChangeCentralMoralQuestionController> {
                ChangeCentralMoralQuestionControllerImpl(
                    applicationScope.get(), get(), get()
                )
            }
            provide(
                RenameTheme::class,
                ChangeCentralConflict::class,
                ChangeCentralMoralQuestion::class,
                ChangeThemeLine::class,
                ChangeThematicRevelation::class
            )  {
                ChangeThemeDetailsUseCase(get())
            }

            provide(
                RenameTheme.OutputPort::class,
                ChangeCentralConflict.OutputPort::class,
                ChangeCentralMoralQuestion.OutputPort::class,
                ChangeThemeLine.OutputPort::class,
                ChangeThematicRevelation.OutputPort::class
            ) {
                ChangeThemeDetailsOutput(
                    get(),get(),get(),get(), get()
                )
            }

            provide<ChangeThemeLineController> {
                ChangeThemeLineControllerImpl(
                    applicationScope.get(), get(), get()
                )
            }

            provide<ChangeThematicRevelationController> {
                ChangeThematicRevelationControllerImpl(
                    applicationScope.get(), get(), get()
                )
            }
            listAvailableOppositionValuesForCharacterInTheme()
            removeSymbolFromTheme()
        }
    }

    private fun InProjectScope.listAvailableOppositionValuesForCharacterInTheme()
    {
        provide<ListAvailableOppositionValuesForCharacterInTheme> {
            ListAvailableOppositionValuesForCharacterInThemeUseCase(
                get()
            )
        }
        provide<ListAvailableOppositionValuesForCharacterInThemeController> {
            ListAvailableOppositionValuesForCharacterInThemeController(applicationScope.get(), get())
        }
    }

    private fun InProjectScope.removeSymbolFromTheme()
    {
        provide<RemoveSymbolFromTheme> { RemoveSymbolFromThemeUseCase(get(), get()) }
        provide(RemoveSymbolFromTheme.OutputPort::class) {
            RemoveSymbolFromThemeOutput(get(), get())
        }
    }

}