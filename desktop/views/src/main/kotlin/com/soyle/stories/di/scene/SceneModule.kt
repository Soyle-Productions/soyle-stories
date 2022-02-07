package com.soyle.stories.di.scene

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import kotlinx.coroutines.CompletableDeferred

object SceneModule {

    init {

        scoped<ProjectScope> {

            provide<suspend (DialogType) -> DialogPreference> {
                {
                    val deferred = CompletableDeferred<DialogPreference>()
                    get<GetDialogPreferences>().invoke(it, object : GetDialogPreferences.OutputPort {
                        override fun gotDialogPreferences(response: DialogPreference) {
                            deferred.complete(response)
                        }

                        override fun failedToGetDialogPreferences(failure: Exception) {
                            deferred.complete(DialogPreference(it, true))
                        }
                    })
                    deferred.await()
                }
            }

        }

    }
}