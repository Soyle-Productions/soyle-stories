package com.soyle.studio.common

import arrow.core.Either
import org.junit.jupiter.api.Assertions

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 10:20 PM
 */


inline infix fun <T, A, B> T.`when`(op: T.() -> Either<A, B>) = op()
inline infix fun <A, B> Either<A, B>.then(expected: B.() -> Unit): Either.Right<B> {
	(this as Either.Right).b.expected()
	return this
}

inline infix fun <A, B, T> Either<A, B>.thenFailWith(expectedFailure: () -> T) {
	this as Either.Left
	Assertions.assertEquals(expectedFailure(), a)
}