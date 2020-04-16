package com.soyle.stories.swing.soylestories

import com.soyle.stories.swing.di.find
import javax.swing.*

class WelcomeScreen {

	private val model= find<Any>()

	private val frame = JFrame().apply {
		defaultCloseOperation = JFrame.HIDE_ON_CLOSE


		contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)



		setLocationRelativeTo(null)
		isVisible = true
	}

	val root = JPanel(BoxLayout(null, BoxLayout.Y_AXIS)).apply {

		val url = this@WelcomeScreen.javaClass.getResource("/com/soyle/stories/soylestories/bronze logo.png")
		val label = JLabel(ImageIcon(url))

		JLabel()
	}

}