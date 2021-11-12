
# How to use this setup

We use three hostname which are 

 - The greek node running as country CA at http://ca.node.test
 - The generic node running as country CB at http://cb.node.test
 - The keycloak server running at http://keycloak.test 

In order to use this setup you need to edit `/etc/hosts` and add 

```
127.0.0.1 keycloak.test ca.node.test cb.node.test
```

# How to setup keycloak 

 - Login using username `admin` and password `admin`
 - Create a `test` realm
 - Go to the `test` realm `Keys` settings and add a new provider. Use `java-keystore` with the following settings

   * Priority 100
   * Algorithm RS256
   * Keystore: /opt/keycloak.jks
   * Keystore password: local-demo 
   * Key alias: selfsigned
   * Key password: local-demo

   This certificate is pregenerated using the following command 

   ```
   keytool -genkey -keyalg RSA -alias selfsigned -keystore keycloak.jks -storepass local-demo -validity 3600 -keysize 4096
   ```

   It is very important that the country of the certificate is `C=CA` in order for the node to find the 
   country of the SP (keycloak in this case). The actual file is located at `etc/keycloak/certs/keycloak.jks` and it mounted inside the keycloak container at `/opt/keycloak.jsk`. The key alias is `selfsigned`. 

 - Disable all other key providers except for `hmac-generated`. 
 - Go at `Identity Providers` and add a new `eidas SAML`. 
 - Set `Service Provider Entity ID` as `http://keycloak.test/auth/realms/test/broker/eidasSaml/endpoint/descriptor`
 - Set `Single Sign-On Service URL` as `http://ca.node.test/SpecificConnector/ServiceProvider`
 - Set `Allow create` to `ON`
 - Set `HTTP-POST Binding Response` to `ON`
 - Set `HTTP-POST Binding for AuthnRequest` to `ON`
 - Set `Want AuthnRequests Signed` to `ON`
 - Set `Want Assertions Encrypted` to `ON`
 - Set `Signature Algorithm` to `RSA_SHA512_MGF1`
 - Set `Force Authentication` to `ON`
 - Set `Sign Service Provider Metadata` to `ON`
 - Open the `Requested AuthnContext Constraints` subsection
 - Set `Comparison` to `Minimum`
 - Open `eIDAS SAML Extensions Config` (its already open)
 - Set `Level of Assurance` to `http://eidas.europa.eu/LoA/low`
 - Set `Requested Attributes` to 
 
  ```
  [{"Name":"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier", 
    "NameFormat":  "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true
   }, 
   {"Name":"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName",
    "NameFormat":  "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true},
   {"Name":"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName",
    "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true},
   {"Name":"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth",
    "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true}
  ]
  ```

# CA node setup 

This setup is already performed and the files are preloaded, but we document it here for reference. 

 - Open `etc/config-ca/tomcat/specificConnector/metadata/MetadataFetcher_Provider.properties` and add 
   `http://keycloak.test/auth/realms/test/broker/eidasSaml/endpoint/descriptor` to the whitelist. 
 - Copy the `keystore` certificate from the previous section, convert it to a `.pem` file and add it 
   as a file inside `etc/config-ca/tomcat/specificConnector/metadata-certs/`. The file name does not 
   matter. Make sure you use something like `https://www.samltool.com/format_x509cert.php` to convert
   the string to the appropriate pem format.


# How to start 

```
pushd ..
mvn clean package
cp target/keycloak-eidas-idp-*.jar test-with-node/etc/keycloak/deployments/
popd
docker-compose up
```

