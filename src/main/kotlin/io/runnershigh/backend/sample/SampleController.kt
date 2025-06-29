package io.runnershigh.backend.sample

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class SampleController {

    @GetMapping("/test")
    fun test(): ResponseEntity<String> = ResponseEntity.ok("접속 성공")
}