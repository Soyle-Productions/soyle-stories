package com.soyle.stories.common

import javafx.scene.Node
import tornadofx.Scope
import java.lang.ref.WeakReference

class NodeScope(node: Node) : Scope() {

    private val nodeRef = WeakReference(node)



}