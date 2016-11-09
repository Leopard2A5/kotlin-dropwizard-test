package de.perschon.dropwizardtest

import io.dropwizard.Configuration
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.testing.junit.DropwizardAppRule
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.eclipse.jetty.servlets.CrossOriginFilter.ACCESS_CONTROL_REQUEST_HEADERS_HEADER
import org.eclipse.jetty.servlets.CrossOriginFilter.ACCESS_CONTROL_REQUEST_METHOD_HEADER
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import javax.ws.rs.client.Client
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.MapEntry

class DropwizardTestApplicationHeadersTest {

	companion object {
		@ClassRule @JvmField
		val RULE = DropwizardAppRule<DropwizardTestConfiguration>(DropwizardTestApplication1::class.java)
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

		println(headers)
//		assertThat(headers).contains(MapEntry.entry())
	}

}
