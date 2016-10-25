package de.perschon.dropwizardtest

import de.perschon.dropwizardtest.DropwizardTestConfiguration
import de.perschon.dropwizardtest.health.TemplateHealthCheck
import de.perschon.dropwizardtest.resources.HelloWorldResource
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
		if (configuration == null || environment == null) {
			throw NullPointerException()
		}

		val templateHealthCheck = TemplateHealthCheck(checkNotNull(configuration.template))
		environment.healthChecks().register("template", templateHealthCheck)

		val resource = HelloWorldResource(
			checkNotNull(configuration.template),
			checkNotNull(configuration.defaultName)
		)
		environment.jersey().register(resource)
	}

    override fun initialize(
        bootstrap: Bootstrap<DropwizardTestConfiguration>?
    ) {
        // TODO implement
    }

    override fun getName(): String {
        return "dropwizard-test (kotlin)"
    }

}
