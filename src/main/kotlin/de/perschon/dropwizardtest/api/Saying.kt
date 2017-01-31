package de.perschon.dropwizardtest.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

data class Saying(
	@field:JsonProperty
	val id: Long,

	@field:JsonProperty
	val content: String
) {
}
