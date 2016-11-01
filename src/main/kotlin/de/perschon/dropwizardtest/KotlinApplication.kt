package de.perschon.dropwizardtest

import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment

abstract class KotlinApplication <T : Configuration> : Application<T>() {

	abstract fun nullSafeRun(
		configuration: T,
		environment: Environment
	)

	override fun run(
		configuration: T?,
		environment: Environment?
	) {
		nullSafeRun(checkNotNull(configuration), checkNotNull(environment))
	}
}
