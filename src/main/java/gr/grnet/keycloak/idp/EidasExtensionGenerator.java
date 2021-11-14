package gr.grnet.keycloak.idp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamWriter;

import org.jboss.logging.Logger;
import org.keycloak.saml.SamlProtocolExtensionsAwareBuilder;
import org.keycloak.saml.common.exceptions.ProcessingException;
import org.keycloak.saml.common.util.StaxUtil;
import org.keycloak.util.JsonSerialization;

public class EidasExtensionGenerator implements SamlProtocolExtensionsAwareBuilder.NodeGenerator {

	public static final String EIDAS_NS_URI = "http://eidas.europa.eu/saml-extensions";
	public static final String EIDAS_PREFIX = "eidas";

	protected static final Logger logger = Logger.getLogger(EidasExtensionGenerator.class);

	private EidasSAMLIdentityProviderConfig config;

	public EidasExtensionGenerator(EidasSAMLIdentityProviderConfig config) {
		this.config = config;
	}

	@Override
	public void write(XMLStreamWriter writer) throws ProcessingException {
		StaxUtil.writeNameSpace(writer, EIDAS_PREFIX, EIDAS_NS_URI);

		StaxUtil.writeStartElement(writer, EIDAS_PREFIX, "SPType", EIDAS_NS_URI);
		if (config.isPrivateServiceProvider()) {
			StaxUtil.writeCharacters(writer, "private");
		} else {
			StaxUtil.writeCharacters(writer, "public");
		}
		StaxUtil.writeEndElement(writer);

		List<RequestedAttribute> requestedAttributes = getRequestedAttributes();
		if (!requestedAttributes.isEmpty()) {
			StaxUtil.writeStartElement(writer, EIDAS_PREFIX, "RequestedAttributes", EIDAS_NS_URI);

			for (RequestedAttribute ra : requestedAttributes) {
				StaxUtil.writeStartElement(writer, EIDAS_PREFIX, "RequestedAttribute", EIDAS_NS_URI);
				StaxUtil.writeAttribute(writer, "Name", ra.getName());
				StaxUtil.writeAttribute(writer, "NameFormat", ra.getNameFormat());
				StaxUtil.writeAttribute(writer, "isRequired", String.valueOf(ra.isRequired()));
				StaxUtil.writeEndElement(writer);
			}

			StaxUtil.writeEndElement(writer);
		}

		StaxUtil.flush(writer);
	}

	private List<RequestedAttribute> getRequestedAttributes() {
		String requestedAttributes = config.getRequestedAttributes();
		if (requestedAttributes == null || requestedAttributes.isEmpty())
			return new ArrayList<>();
		try {
			return Arrays.asList(JsonSerialization.readValue(requestedAttributes, RequestedAttribute[].class));
		} catch (Exception e) {
			logger.warn("Could not json-deserialize RequestedAttribute config entry: " + requestedAttributes, e);
			return new ArrayList<>();
		}
	}
}