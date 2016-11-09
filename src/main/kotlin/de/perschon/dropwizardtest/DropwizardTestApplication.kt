package de.perschon.dropwizardtest

import de.perschon.dropwizardtest.health.TemplateHealthCheck
import de.perschon.dropwizardtest.resources.HelloWorldResource
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.*
import javax.servlet.DispatcherType
import javax.servlet.FilterRegistration

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

	private fun enableCORSHeaders(environment: Environment) {
		val filter = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
		with(filter) {
			addMappingForUrlPatterns(
				EnumSet.allOf(DispatcherType::class.java),
				false,
				environment.applicationContext.contextPath + "*"
			)
			println("HIER ${environment.applicationContext.contextPath + "*"}")
			setInitParameter("allowedOrigins", "http://localhost:3000")
			setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
			setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS")
			setInitParameter("Access-Control-Allow-Origin", "/*")
			setInitParameter("Access-Control-Allow-Credentials", "true")
			setInitParameter("Access-Control-Expose-Headers", "true")
		}
	}

}
