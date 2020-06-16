package com.soyle.stories.di

import org.junit.jupiter.api.Test
import tornadofx.Scope

class DIUnitTest {

	interface Testable
	class TestableImpl : Testable

	@Test
	fun `provided classes are available`() {
		scoped<Scope> {
			provide {
				TestableImpl()
			}
		}
		DI.resolve<TestableImpl>(Scope())
	}

	@Test
	fun `additional classes are available`() {
		scoped<Scope> {
			provide(Testable::class) {
				TestableImpl()
			}
		}
		DI.resolve<Testable>(Scope())
		DI.resolve<TestableImpl>(Scope())
	}

	@Test
	fun `module objects are available`() {
		val module = object {
			init {
				scoped<Scope> {
					provide(Testable::class) {
						TestableImpl()
					}
				}
			}
		}
		DI.resolve<Testable>(Scope())
		DI.resolve<TestableImpl>(Scope())
	}

}