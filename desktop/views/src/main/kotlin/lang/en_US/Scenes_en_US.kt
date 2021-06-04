package lang.en_US

import com.soyle.stories.domain.scene.SceneLocale
import java.util.ListResourceBundle

class Scenes_en_US : SceneLocale, ListResourceBundle() {

	override val sceneDoesNotExist: String = "Scene does not exist"

	override fun getContents(): Array<Array<Any>> = this::class.java.declaredFields.map {
		arrayOf(it.name, it.get(this))
	}.toTypedArray()
}
