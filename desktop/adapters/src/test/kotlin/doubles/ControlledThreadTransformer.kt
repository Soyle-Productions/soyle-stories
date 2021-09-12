package doubles

import com.soyle.stories.common.ThreadTransformer
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

class ControlledThreadTransformer : ThreadTransformer {

    private var asyncDelay: Long = 0

    fun ensureRunAsync(trackedResult: () -> Any?, task: suspend CoroutineScope.() -> Unit) {
        asyncDelay = 5
        runBlocking {
            task()

            assertNull(trackedResult()) { "Should not have received async result yet.  Code completed synchronously" }
            delay(asyncDelay * 2)
            assertNotNull(trackedResult()) { "Should have received async result by now.  Code did not complete." }
        }
    }

    override fun isGuiThread(): Boolean {
        TODO("Not yet implemented")
    }

    override fun async(task: suspend CoroutineScope.() -> Unit): Job {
        if (asyncDelay == 0L) return runBlocking {
            task()
            Job().apply { complete() }
        }
        else {
            return CoroutineScope(Dispatchers.Default).launch {
                delay(asyncDelay)
                task()
            }
        }
    }

    override fun gui(update: suspend CoroutineScope.() -> Unit) {}
}