package de.perschon.dropwizardtest

import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.ResourceHelpers.resourceFilePath
import io.dropwizard.testing.junit.DropwizardAppRule
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.eclipse.jetty.servlets.CrossOriginFilter.ACCESS_CONTROL_REQUEST_HEADERS_HEADER
import org.eclipse.jetty.servlets.CrossOriginFilter.ACCESS_CONTROL_REQUEST_METHOD_HEADER
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import javax.ws.rs.client.Client

class DropwizardTestApplicationHeadersTest {

	companion object {
		@ClassRule @JvmField
		val RULE = DropwizardAppRule(
			DropwizardTestApplication1::class.java,
			resourceFilePath("test.yml")
		)
		private var client: Client? = null

		@BeforeClass
		@JvmStatic
		fun setup() {
			client = JerseyClientBuilder(RULE.environment).build("test client")
		}
	}

	@Test
	fun testPreflightOptions() {
		val response = checkNotNull(client).target("http://localhost:${RULE.localPort}")
			.request()
			.header("Origin", "http://localhost:3000")
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.header(ACCESS_CONTROL_REQUEST_HEADERS_HEADER, "Content-Type")
			.options()
		val headers = response.headers

		assertThat(headers).contains(entry("Access-Control-Allow-Credentials", mutableListOf("true")))
		assertThat(headers).contains(entry("Access-Control-Allow-Headers", mutableListOf("X-Requested-With,Content-Type,Accept,Origin")))
		assertThat(headers).contains(entry("Access-Control-Allow-Methods", mutableListOf("GET,PUT,POST,DELETE,OPTIONS")))
	}

}
