package com.soyle.stories.di

import com.soyle.stories.di.characterarc.CharacterArcModule
import com.soyle.stories.di.layout.LayoutModule
import com.soyle.stories.di.location.LocationModule
import com.soyle.stories.di.modules.ApplicationModule
import com.soyle.stories.di.modules.DataModule
import com.soyle.stories.di.modules.LocaleModule
import com.soyle.stories.di.project.ProjectModule
import com.soyle.stories.di.scene.SceneModule
import com.soyle.stories.di.storyevent.StoryEventModule
import com.soyle.stories.di.theme.ThemeModule
import com.soyle.stories.di.writer.WriterModule

fun configureDI() {
	ApplicationModule
	DataModule
	LocaleModule
	ProjectModule
	LayoutModule
	LocationModule
	CharacterArcModule
	SceneModule
	StoryEventModule
	ThemeModule
	WriterModule
}