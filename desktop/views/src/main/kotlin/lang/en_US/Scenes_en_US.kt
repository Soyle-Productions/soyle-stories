package lang.en_US

import com.soyle.stories.scene.Locale
import java.util.*

class Scenes_en_US : Locale, ListResourceBundle() {

	override val sceneNameCannotBeBlank: String = "Scene name cannot be blank"
	override val sceneDoesNotExist: String = "Scene does not exist"

	override fun getContents(): Array<Array<Any>> = this::class.java.declaredFields.map {
		arrayOf(it.name, it.get(this))
	}.toTypedArray()


}