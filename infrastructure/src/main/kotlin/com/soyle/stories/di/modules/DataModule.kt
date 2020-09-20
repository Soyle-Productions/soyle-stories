package com.soyle.stories.di.modules

import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.repositories.*
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.theme.Context
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.writer.repositories.WriterRepository
import com.soyle.stories.stores.ProjectFileStore

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

			provide(
			  CharacterArcRepository::class
			) {
				CharacterArcRepositoryImpl()
			}

			provide(
			  CharacterRepository::class,
			  com.soyle.stories.theme.repositories.CharacterRepository::class,
			  com.soyle.stories.character.repositories.CharacterRepository::class
			) {
				CharacterRepositoryImpl()
			}

			provide(
			  ThemeRepository::class,
			  com.soyle.stories.characterarc.repositories.ThemeRepository::class,
			  com.soyle.stories.character.repositories.ThemeRepository::class
			) { ThemeRepositoryImpl() }

			provide<LocationRepository> {
				LocationRepositoryImpl()
			}

			provide(
			  Context::class,
			  com.soyle.stories.layout.Context::class
			) {
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

			provide<OpenToolContext> {
				object : OpenToolContext {
					override val characterRepository: com.soyle.stories.character.repositories.CharacterRepository = get()
					override val locationRepository: LocationRepository = get()
					override val sceneRepository: SceneRepository = get()
					override val storyEventRepository: StoryEventRepository = get()
					override val themeRepository: ThemeRepository = get()
				}
			}
		}
	}
}

class ContextDouble(scope: ProjectScope) : Context, com.soyle.stories.layout.Context {
	override val characterArcRepository: com.soyle.stories.theme.repositories.CharacterArcRepository by DI.resolveLater(scope)
	override val characterRepository: com.soyle.stories.theme.repositories.CharacterRepository by DI.resolveLater(scope)
	override val themeRepository: ThemeRepository by DI.resolveLater(scope)
	override val layoutRepository: LayoutRepository by DI.resolveLater(scope)
}