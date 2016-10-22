package de.perschon.dropwizardtest

import io.dropwizard.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.*
import javax.validation.constraints.*

class DropwizardTestConfiguration() : Configuration() {
    @field:NotEmpty
    @get:JsonProperty
    var foo: String? = null
}
