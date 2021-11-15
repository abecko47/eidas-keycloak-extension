package gr.grnet.keycloak.idp.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class CitizenCountrySelectorAuthenticatorForm implements Authenticator {

	private static final Logger LOG = Logger.getLogger(CitizenCountrySelectorAuthenticatorForm.class);

	public CitizenCountrySelectorAuthenticatorForm() {
	}

	@Override
	public void authenticate(AuthenticationFlowContext context) {

		// get config
		AuthenticatorConfigModel config = context.getAuthenticatorConfig();
		String countriesList = config.getConfig()
				.get(CitizenCountrySelectorAuthenticatorFormFactory.CITIZEN_COUNTRY_LIST);
		List<String> countries;

		if (countriesList == null) {
			countries = new ArrayList<>();
		} else {
			countries = Arrays.asList(countriesList.split("##"));
		}

		LOG.info("Countries from config = " + countries);

		Response response = context.form().setAttribute("availablecountries", countries)
				.createForm("citizen-country-select-form.ftl");

		context.challenge(response);
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return true;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	}

	@Override
	public void action(AuthenticationFlowContext context) {

		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		String country = formData.getFirst("country");

		LOG.infof("Retrieved country=%s", country);

		if (country != null && !country.trim().isEmpty()) {
			// Add selected information to authentication session
			context.getAuthenticationSession().setUserSessionNote("country", country);
		}

		context.success();
	}

	@Override
	public void close() {
		// NOOP
	}
}