package com.yonatankarp.cat.fact.service.controllers

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.RestController

/**
 * Default endpoints per application.
 */
@RestController
class RootController {
    fun helloWorld(): ResponseEntity<Any> =
        ok("hello world")
}
