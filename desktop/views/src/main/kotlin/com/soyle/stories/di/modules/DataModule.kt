package com.soyle.stories.di.modules

import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.repositories.*
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.stores.ProjectFileStore
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.project.ProjectRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.writer.WriterRepository
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository

object DataModule {

	val workerId: String = System.getProperty("user.name")

	init {
		scoped<ApplicationScope> {

			provide<WorkspaceRepository> {
				WorkspaceRepositoryImpl()
			}

			provide {
				ProjectFileStore()
			}

			provide(
			  ProjectRepository::class,
			  com.soyle.stories.workspace.repositories.ProjectRepository::class,
			  FileRepository::class
			) {
				ProjectFileRepository(get<ProjectFileStore>())
			}
		}

		scoped<ProjectScope> {

			provide<LayoutRepository> { LayoutRepositoryImpl() }

			provide(CharacterArcRepository::class) {
				CharacterArcRepositoryImpl()
			}

			provide<CharacterRepository> {
				CharacterRepositoryImpl()
			}

			provide<ThemeRepository> { ThemeRepositoryImpl() }

			provide<LocationRepository> {
				LocationRepositoryImpl()
			}

			provide<Context> {
				ContextDouble(this)
			}

			provide<SceneRepository> {
				SceneRepositoryImpl()
			}

			provide<StoryEventRepository> {
				StoryEventRepositoryImpl()
			}

			provide<WriterRepository> {
				WriterRepositoryImpl(applicationScope.writerId)
			}

			provide<ProseRepository> {
				ProseRepositoryImpl()
			}

			provide<OpenToolContext> {
				object : OpenToolContext {
					override val characterRepository: CharacterRepository = get()
					override val locationRepository: LocationRepository = get()
					override val sceneRepository: SceneRepository = get()
					override val storyEventRepository: StoryEventRepository = get()
					override val themeRepository: ThemeRepository = get()
				}
			}
		}
	}
}

class ContextDouble(scope: ProjectScope) : Context {
	override val layoutRepository: LayoutRepository by DI.resolveLater(scope)
}