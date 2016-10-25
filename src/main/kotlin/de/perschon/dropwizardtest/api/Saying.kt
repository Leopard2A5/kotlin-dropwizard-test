package de.perschon.dropwizardtest.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

data class Saying(
	private val id: Long,
	private val content: String
) {
	@JsonProperty
	fun getId(): Long {
		return id
	}

	@JsonProperty
	fun getContent(): String {
		return content
	}
}