package gr.grnet.keycloak.idp.parsers;

import org.jboss.logging.Logger;

import org.keycloak.common.util.StreamUtil;
import org.keycloak.saml.common.PicketLinkLogger;
import org.keycloak.saml.common.PicketLinkLoggerFactory;
import org.keycloak.saml.common.constants.GeneralConstants;
import org.keycloak.saml.processing.api.saml.v2.request.SAML2Request;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.saml.processing.web.util.PostBindingUtil;
import org.keycloak.saml.processing.web.util.RedirectBindingUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EidasSAMLRequestParser {
    private static final PicketLinkLogger logger = PicketLinkLoggerFactory.getLogger();
    protected static Logger log = Logger.getLogger(EidasSAMLRequestParser.class);

    public static SAMLDocumentHolder parseRequestRedirectBinding(String samlMessage) {
        InputStream is;
        is = RedirectBindingUtil.base64DeflateDecode(samlMessage);
        if (log.isDebugEnabled()) {
            String message = null;
            try {
                message = StreamUtil.readString(is, GeneralConstants.SAML_CHARSET);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.debug("SAML Redirect Binding");
            log.debug(message);
            is = new ByteArrayInputStream(message.getBytes(GeneralConstants.SAML_CHARSET));

        }
        try {
            return SAML2Request.getSAML2ObjectFromStream(is);
        } catch (Exception e) {
            logger.samlBase64DecodingError(e);
        }
        return null;

    }

    public static SAMLDocumentHolder parseRequestPostBinding(String samlMessage) {
        InputStream is;
        byte[] samlBytes = PostBindingUtil.base64Decode(samlMessage);
        if (log.isDebugEnabled()) {
            String str = new String(samlBytes, GeneralConstants.SAML_CHARSET);
            log.debug("SAML POST Binding");
            log.debug(str);
        }
        is = new ByteArrayInputStream(samlBytes);
        try {
            return SAML2Request.getSAML2ObjectFromStream(is);
        } catch (Exception e) {
            logger.samlBase64DecodingError(e);
        }
        return null;
    }

    public static SAMLDocumentHolder parseResponsePostBinding(String samlMessage) {
        byte[] samlBytes = PostBindingUtil.base64Decode(samlMessage);
        log.debug("SAML POST Binding");
        return parseResponseDocument(samlBytes);
    }

    public static SAMLDocumentHolder parseResponseDocument(byte[] samlBytes) {
        if (log.isDebugEnabled()) {
            String str = new String(samlBytes, GeneralConstants.SAML_CHARSET);
            log.debug(str);
        }
        InputStream is = new ByteArrayInputStream(samlBytes);
        EidasSAML2Response response = new EidasSAML2Response();
        try {
            response.getSAML2ObjectFromStream(is);
            return response.getSamlDocumentHolder();
        } catch (Exception e) {
            logger.samlBase64DecodingError(e);
        }
        return null;
    }

    public static SAMLDocumentHolder parseResponseRedirectBinding(String samlMessage) {
        InputStream is = RedirectBindingUtil.base64DeflateDecode(samlMessage);
        if (log.isDebugEnabled()) {
            String message = null;
            try {
                message = StreamUtil.readString(is, GeneralConstants.SAML_CHARSET);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.debug("SAML Redirect Binding");
            log.debug(message);
            is = new ByteArrayInputStream(message.getBytes(GeneralConstants.SAML_CHARSET));

        }
        EidasSAML2Response response = new EidasSAML2Response();
        try {
            response.getSAML2ObjectFromStream(is);
            return response.getSamlDocumentHolder();
        } catch (Exception e) {
            logger.samlBase64DecodingError(e);
        }
        return null;

    }


}

