package de.perschon.dropwizardtest

import de.perschon.dropwizardtest.DropwizardTestConfiguration
import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

fun main(args: Array<String>) {
    DropwizardTestApplication1().run(*args)
}

class DropwizardTestApplication1 : Application<DropwizardTestConfiguration>() {
    override fun run(
            configuration: DropwizardTestConfiguration?,
            environment: Environment?
    ) {
        // TODO implement
    }

    override fun initialize(bootstrap: Bootstrap<DropwizardTestConfiguration>?) {

    }
    override fun getName(): String {
        return "dropwizard-test (kotlin)"
    }

}
