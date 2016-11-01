package de.perschon.dropwizardtest

import de.perschon.dropwizardtest.health.TemplateHealthCheck
import de.perschon.dropwizardtest.resources.HelloWorldResource
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

fun main(args: Array<String>) {
    DropwizardTestApplication1().run(*args)
}

class DropwizardTestApplication1 : KotlinApplication<DropwizardTestConfiguration>() {

    override fun nullSafeRun(
        configuration: DropwizardTestConfiguration,
        environment: Environment
    ) {
		val template = checkNotNull(configuration.template)
		val defaultName = checkNotNull(configuration.defaultName)

		val templateHealthCheck = TemplateHealthCheck(template)
		environment.healthChecks().register("template", templateHealthCheck)

		val resource = HelloWorldResource(template, defaultName)
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
