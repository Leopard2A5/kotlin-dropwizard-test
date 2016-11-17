package de.perschon.dropwizardtest

import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.*
import javax.servlet.DispatcherType

object CORSFilter {

	fun enableCORSHeaders(
		environment: Environment,
		configuration: DropwizardTestConfiguration
	) {
		val filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter::class.java)
		with(filter) {
			addMappingForUrlPatterns(
				EnumSet.allOf(DispatcherType::class.java),
				false,
				environment.applicationContext.contextPath + "*"
			)
			setInitParameter("allowedOrigins", configuration.corsAllowedOrigins.joinToString(","))
			setInitParameter("allowedMethods", configuration.corsAllowedMethods.joinToString(","))
			setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
		}
	}

}
