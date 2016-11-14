package de.perschon.dropwizardtest

import de.perschon.dropwizardtest.CORSFilter.enableCORSHeaders
import de.perschon.dropwizardtest.health.TemplateHealthCheck
import de.perschon.dropwizardtest.resources.HelloWorldResource
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

fun main(args: Array<String>) {
    DropwizardTestApplication1().run(*args)
}

class DropwizardTestApplication1 : KotlinApplication<DropwizardTestConfiguration>() {

    override fun nullSafeRun(
        config: DropwizardTestConfiguration,
        environment: Environment
    ) {
		enableCORSHeaders(environment)

		val templateHealthCheck = TemplateHealthCheck(config.template)
		environment.healthChecks().register("template", templateHealthCheck)

		val resource = HelloWorldResource(config.template, config.defaultName)
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
