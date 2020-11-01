package com.soyle.stories.desktop.config.theme

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.theme.changeThemeDetails.*
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangeCentralMoralQuestionControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangeThematicRevelationController
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangeThematicRevelationControllerImpl
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangeThemeLineController
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangeThemeLineControllerImpl
import com.soyle.stories.theme.usecases.changeThemeDetails.*
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralArgumentFrame
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgument
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme

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

        }
    }

}