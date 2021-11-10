
# DNS 

Add the following line in `/etc/hosts`

```
127.0.0.1 keycloak.test ca.node.test cb.node.test
```

# Realm name

Create a `test` realm.

# Realm Settings
Increase the rsa key size to 4096 bits in the key providers. 

```
Keys->rsa-generated->Key size=4096
Keys->rsa-enc-generated->Key size=4096
```

# CA node setup 

Open `etc/config-ca/tomcat/specificConnector/metadata/MetadataFetcher_Provider.properties` and add 
`http://keycloak.test/auth/realms/test/broker/eidasSaml/endpoint/descriptor` to the whitelist. 

Find the `rsa-generated` certificate from keycloak and add it as a `.pem` file inside `etc/config-ca/tomcat/specificConnector/metadata-certs/`.
For example use file `keycloak_test_realm_metadata.pem`. You can use something like `https://www.samltool.com/format_x509cert.php` to convert 
the string to the appropriate pem format.

# Idp Settings

```
Redirect URI: http://keycloak.test/auth/realms/test/broker/eidasSaml/endpoint
Alias: eidasSaml
Service Provider Entity ID: http://keycloak.test/auth/realms/test/broker/eidasSaml/endpoint/descriptor
Single Sign-On Service URL: http://ca.node.test/SpecificConnector/ServiceProvider
HTTP-POST Binding Response: ON
HTTP-POST Binding for AuthnRequest: ON
Want AuthnRequests Signed: ON
Want Assertions Signed: OFF
Want Assertions Encrypted: ON
Signature Algorithm: RSA_SHA512_MGF1
SAML Signature Key NAme: KEY_ID
Force Authentication: ON

Validate Signature: ON 
Validating X509 Cerificates:  Obtain from `http://ca.node.test/SpecificConnector/ConnectorResponderMetadata`

Sign Service Provider Metadata: ON

Requested AuthnContext Constraints
Comparison: Minimum

eIDAS SAML Extensions Config
Level of Assurance: http://eidas.europa.eu/LoA/low
Private sector service provider: OFF
Requested Attributes: [{"Name":"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier", "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true}, {"Name":"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName", "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true}, 
{"Name":"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName", "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true}, 
{"Name":"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth", "NameFormat": "urn:oasis:names:tc:SAML:2.0:attrname-format:uri", "isRequired":true}]
```



# How to start 

```
pushd ..
mvn clean package
cp target/keycloak-eidas-idp-*.jar test-with-node/etc/keycloak/deployments/
popd
docker-compose up
```

