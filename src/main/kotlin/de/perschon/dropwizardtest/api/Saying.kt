package de.perschon.dropwizardtest.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

data class Saying(
	@field:JsonProperty
	private val id: Long,

	@field:JsonProperty
	private val content: String
) {
}
