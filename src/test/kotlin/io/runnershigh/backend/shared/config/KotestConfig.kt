package io.runnershigh.backend.shared.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

class KotestConfig : AbstractProjectConfig() {
    override fun extensions() =
        listOf(SpringTestExtension(SpringTestLifecycleMode.Root))
}