package de.perschon.dropwizardtest

import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlets.CrossOriginFilter
import java.util.*
import javax.servlet.DispatcherType

object CORSFilter {

	fun enableCORSHeaders(environment: Environment) {
		val filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter::class.java)
		with(filter) {
			addMappingForUrlPatterns(
				EnumSet.allOf(DispatcherType::class.java),
				false,
				environment.applicationContext.contextPath + "*"
			)
			setInitParameter("allowedOrigins", "http://localhost:3000")
			setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
			setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS")
			setInitParameter("Access-Control-Allow-Origin", "/*")
			setInitParameter("Access-Control-Allow-Credentials", "true")
			setInitParameter("Access-Control-Expose-Headers", "true")
		}
	}

}
