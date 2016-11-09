import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.data.MapEntry;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.EnumSet;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

public class JettyCrossOriginDWIntegrationTest {

	private static final String GOOD_ORIGIN = "allowed_host";
	private static final String BAD_ORIGIN = "denied_host";

	public static class CORSApplication extends Application<Configuration> {

		@Override
		public void run(Configuration configuration, Environment environment) throws Exception {
			System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
			FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter.class);

			filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, environment.getApplicationContext().getContextPath() + "*");
			filter.setInitParameter(ALLOWED_METHODS_PARAM, "GET,PUT,POST,OPTIONS");
			filter.setInitParameter(ALLOWED_ORIGINS_PARAM, GOOD_ORIGIN);
			filter.setInitParameter(ALLOWED_HEADERS_PARAM, "Origin, Content-Type, Accept");
			filter.setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");
			filter.setInitParameter(EXPOSED_HEADERS_PARAM, "");
		}
	}

	@ClassRule
	public static final DropwizardAppRule<Configuration> RULE =
		new DropwizardAppRule<>(CORSApplication.class);
	private static Client client;

	@BeforeClass
	public static void setUp() {
		client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");
	}

	@Test
	public void allowedOriginPreflightOptions() {
		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.header(ACCESS_CONTROL_REQUEST_HEADERS_HEADER, "Content-Type")
			.options();

		// then
		MultivaluedMap<String, Object> headers = response.getHeaders();

		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, asList(GOOD_ORIGIN)));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, asList("true")));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_MAX_AGE_HEADER, asList("1800")));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_METHODS_HEADER, asList("GET,PUT,POST,OPTIONS")));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, asList("Origin,Content-Type,Accept")));
	}

	@Test
	public void deniedOriginPreflightOptions() {
		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", BAD_ORIGIN)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "GET")
			.options();

		// then
		assertThat(response.getHeaders()).containsOnlyKeys(DATE, CONTENT_LENGTH);
	}


	@Test
	public void deniedMethodPreflightOptions() {

		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.header(ACCESS_CONTROL_REQUEST_METHOD_HEADER, "DELETE")
			.options();

		// then
		assertThat(response.getHeaders()).containsOnlyKeys(DATE, CONTENT_LENGTH, CONTENT_TYPE);
	}

	@Test
	public void deniedHeaderPreflightOptions() {

		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.header(ACCESS_CONTROL_REQUEST_HEADERS_HEADER, AUTHORIZATION)
			.options();

		// then
		MultivaluedMap<String, Object> headers = response.getHeaders();

		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, asList(GOOD_ORIGIN)));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, asList("true")));
		assertThat(response.getHeaders()).doesNotContainKeys(
			ACCESS_CONTROL_MAX_AGE_HEADER,
			ACCESS_CONTROL_ALLOW_METHODS_HEADER,
			ACCESS_CONTROL_ALLOW_HEADERS_HEADER);
	}

	@Test
	public void allowedOriginAllowedMethodAllowedHeaderRequest() {
		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.header("Content-Type", APPLICATION_JSON)
			.get();

		// then
		MultivaluedMap<String, Object> headers = response.getHeaders();

		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, asList(GOOD_ORIGIN)));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, asList("true")));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, asList("")));
		assertThat(headers).doesNotContainKeys(
			ACCESS_CONTROL_MAX_AGE_HEADER,
			ACCESS_CONTROL_ALLOW_METHODS_HEADER,
			ACCESS_CONTROL_ALLOW_HEADERS_HEADER);
	}

	@Test
	public void deniedOriginRequest() {
		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", BAD_ORIGIN)
			.get();

		// then
		assertThat(response.getHeaders()).doesNotContainKeys(
			ACCESS_CONTROL_ALLOW_ORIGIN_HEADER,
			ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER,
			ACCESS_CONTROL_EXPOSE_HEADERS_HEADER,
			ACCESS_CONTROL_MAX_AGE_HEADER,
			ACCESS_CONTROL_ALLOW_METHODS_HEADER,
			ACCESS_CONTROL_ALLOW_HEADERS_HEADER);
	}

	@Test
	public void deniedMethodRequest() {
		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.delete();

		// then
		MultivaluedMap<String, Object> headers = response.getHeaders();

		assertThat(response.getHeaders()).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, asList(GOOD_ORIGIN)));
		assertThat(response.getHeaders()).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, asList("true")));
		assertThat(response.getHeaders()).contains(MapEntry.entry(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, asList("")));
		assertThat(headers).doesNotContainKeys(
			ACCESS_CONTROL_MAX_AGE_HEADER,
			ACCESS_CONTROL_ALLOW_METHODS_HEADER,
			ACCESS_CONTROL_ALLOW_HEADERS_HEADER);
	}

	@Test
	public void deniedHeaderRequest() {

		// when
		Response response = client.target(String.format("http://localhost:%d/", RULE.getLocalPort())).request()
			.header("Origin", GOOD_ORIGIN)
			.header("Content-Type", AUTHORIZATION)
			.delete();

		// then
		MultivaluedMap<String, Object> headers = response.getHeaders();

		assertThat(response.getHeaders()).doesNotContainKeys(ACCESS_CONTROL_ALLOW_HEADERS_HEADER, ACCESS_CONTROL_MAX_AGE_HEADER, ACCESS_CONTROL_ALLOW_METHODS_HEADER);
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, asList(GOOD_ORIGIN)));
		assertThat(headers).contains(MapEntry.entry(ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER, asList("true")));
	}
}
