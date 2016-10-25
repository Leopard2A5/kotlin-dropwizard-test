package de.perschon.dropwizardtest.health

import com.codahale.metrics.health.HealthCheck

class TemplateHealthCheck(
	private val template: String
) : HealthCheck() {

	override fun check(): Result {
		val saying = String.format(template, "TEST")
		if (!saying.contains("TEST")) {
			return Result.unhealthy("template doesn't include a name")
		}
		return Result.healthy()
	}

}