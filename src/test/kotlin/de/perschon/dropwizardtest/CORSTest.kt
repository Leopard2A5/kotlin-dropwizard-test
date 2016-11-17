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
import javax.ws.rs.client.Invocation

class CORSTest {

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
	fun shouldIncludeVaryHeader() {
		val req = getRequestBuilder()
		val origin = RULE.configuration.corsAllowedOrigins.first()
		val response = req
			.header("Origin", origin)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.options()
		val headers = response.headers

		assertThat(response.status).isEqualTo(200)
		assertThat(headers).contains(entry("Vary", mutableListOf("Origin")))
	}

	@Test
	fun shouldIncludeAllowedOrigin() {
		val req = getRequestBuilder()
		val origin = RULE.configuration.corsAllowedOrigins.first()
		val response = req
			.header("Origin", origin)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.options()
		val headers = response.headers

		assertThat(response.status).isEqualTo(200)
		assertThat(headers).contains(entry("Access-Control-Allow-Origin", mutableListOf(origin)))
	}

	@Test
	fun shouldIncludeAllowedMethods() {
		val req = getRequestBuilder()
		val origin = RULE.configuration.corsAllowedOrigins.first()
		val response = req
			.header("Origin", origin)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.options()
		val headers = response.headers

		assertThat(response.status).isEqualTo(200)
		assertThat(headers).contains(entry("Access-Control-Allow-Methods", mutableListOf("GET,OPTIONS")))
	}

	@Test
	fun shouldDeclinePreflightForOriginsThatAreNotAllowed() {
		val req = getRequestBuilder()
		var origin = "ProhibitedOrigin"
		val response = req
			.header("Origin", origin)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.options()
		val headers = response.headers

		assertThat(response.status).isEqualTo(200)
		assertThat(headers).doesNotContain(entry("Access-Control-Allow-Origin", mutableListOf(origin)))
	}

	private fun getRequestBuilder(): Invocation.Builder {
		val client = checkNotNull(client)
		val ret = client
			.target("http://localhost:${RULE.localPort}/hello-world")
			.request();
		return checkNotNull(ret)
	}

}
