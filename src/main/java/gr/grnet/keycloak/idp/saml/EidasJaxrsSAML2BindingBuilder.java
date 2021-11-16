package gr.grnet.keycloak.idp.saml;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.common.constants.GeneralConstants;
import org.keycloak.saml.common.exceptions.ConfigurationException;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.w3c.dom.Document;

public class EidasJaxrsSAML2BindingBuilder extends EidasBaseSAML2BindingBuilder<EidasJaxrsSAML2BindingBuilder> {

	public static final String COUNTRY = "country";

	private final KeycloakSession session;
	private String country;

	public EidasJaxrsSAML2BindingBuilder(KeycloakSession session) {
		this.session = session;
	}

	public EidasJaxrsSAML2BindingBuilder country(String country) {
		this.country = country;
		return this;
	}

	public class PostBindingBuilder extends BasePostBindingBuilder {
		public PostBindingBuilder(EidasJaxrsSAML2BindingBuilder builder, Document document) throws ProcessingException {
			super(builder, document);
		}

		public Response request(String actionUrl) throws ConfigurationException, ProcessingException, IOException {
			return createResponse(actionUrl, GeneralConstants.SAML_REQUEST_KEY);
		}

		public Response response(String actionUrl) throws ConfigurationException, ProcessingException, IOException {
			return createResponse(actionUrl, GeneralConstants.SAML_RESPONSE_KEY);
		}

		private Response createResponse(String actionUrl, String key)
				throws ProcessingException, ConfigurationException, IOException {
			MultivaluedMap<String, String> formData = new MultivaluedHashMap<>();
			formData.add(GeneralConstants.URL, actionUrl);
			formData.add(key, EidasBaseSAML2BindingBuilder.getSAMLResponse(document));

			if (this.getRelayState() != null) {
				formData.add(GeneralConstants.RELAY_STATE, this.getRelayState());
			}

			// eIDAS specific - add country
			if (country != null) {
				formData.add(COUNTRY, country);
			}

			return session.getProvider(LoginFormsProvider.class).setFormData(formData).createSamlPostForm();
		}
	}

	public static class RedirectBindingBuilder extends BaseRedirectBindingBuilder {
		public RedirectBindingBuilder(EidasJaxrsSAML2BindingBuilder builder, Document document)
				throws ProcessingException {
			super(builder, document);
		}

		public Response response(String redirectUri) throws ProcessingException, ConfigurationException, IOException {
			return response(redirectUri, false);
		}

		public Response request(String redirect) throws ProcessingException, ConfigurationException, IOException {
			return response(redirect, true);
		}

		private Response response(String redirectUri, boolean asRequest)
				throws ProcessingException, ConfigurationException, IOException {
			URI uri = generateURI(redirectUri, asRequest);
			logger.tracef("redirect-binding uri: %s", uri);
			CacheControl cacheControl = new CacheControl();
			cacheControl.setNoCache(true);
			return Response.status(302).location(uri).header("Pragma", "no-cache")
					.header("Cache-Control", "no-cache, no-store").build();
		}

	}

	@Override
	public RedirectBindingBuilder redirectBinding(Document document) throws ProcessingException {
		return new RedirectBindingBuilder(this, document);
	}

	@Override
	public PostBindingBuilder postBinding(Document document) throws ProcessingException {
		return new PostBindingBuilder(this, document);
	}

}