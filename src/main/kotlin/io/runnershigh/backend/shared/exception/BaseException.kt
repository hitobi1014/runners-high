package io.runnershigh.backend.shared.exception

import org.springframework.http.HttpStatus

abstract class BaseException(
    val errorCode: String,
    val httpStatus: HttpStatus,
    override val message: String,
) : RuntimeException(message)