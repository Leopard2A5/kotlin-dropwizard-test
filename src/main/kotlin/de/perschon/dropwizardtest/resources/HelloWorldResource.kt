package de.perschon.dropwizardtest.resources

import com.codahale.metrics.annotation.Timed
import de.perschon.dropwizardtest.api.Saying
import java.util.concurrent.atomic.AtomicLong
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
class HelloWorldResource(
	private val template: String,
	private val defaultName: String
) {
	private val counter: AtomicLong = AtomicLong()

	@GET
	@Timed
	fun sayHello(@QueryParam("name") name: String?): Saying {
		val value = String.format(template, name ?: defaultName)
		return Saying(counter.incrementAndGet(), value)
	}
}
