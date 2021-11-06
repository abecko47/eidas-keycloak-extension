FROM tomcat:8.5-jdk8

RUN mkdir /tmp/eidas \
 && curl -o /tmp/eidas/eIDAS-node-2.5.0.zip -L https://secure-web.cisco.com/1sEvHhehb7aEYORu--vBI725B5MlADfG-yUXnsBQLZDoy_dBQM8xsfqrqovwc8zZrM1Q74FAs1oo9Ev_1O3YF38t0PRCd652DPRBnlcF6dl1rL-D-cYu7mATvQguuk-nUYGrDNURoSqLg6QjY1hXdd2coOkNn3FL1G4wpm4bJW4fpgKUM1rHA8eOJx2R9Gkbj3_kxhN41-ldY_6NQVU2WjOoS8qkbwY3MapSC6oXMhwWIIC3m-aZPG_00zIIPKaU6KoVJJlap-A2llXwtApjGW5OS9AMxocO9RukiWn4eFlMK4wiOQLKLjgOwz09ne8h_RdV5SoTMbhxNAJym8bVmf7jt4P4TvSz0eqbZUxUYapz-JRr-_qkZYFC-y0et2MJqlx1BE3pkzfipPa-AfMVVK-me7p3z9C-XQD-kH8DtBhVmNHezJULsMN0ocu2kAAkvXVkH0qFe8e1Ji4RnvUKbK2p7KZVXoDokCUueC8fXIdAAkJIs9MSDt8tLYkTvnzdq/https%3A%2F%2Fec.europa.eu%2Fcefdigital%2Fartifact%2Frepository%2Feid%2Feu%2FeIDAS-node%2F2.5.0%2FeIDAS-node-2.5.0.zip \
 && cd /tmp/eidas \
 && unzip eIDAS-node-2.5.0.zip \
 && unzip EIDAS-Binaries-Tomcat-2.5.0.zip \
 && cp TOMCAT/*.war /usr/local/tomcat/webapps/ \
 && rm -rf /tmp/eidas

RUN ls /usr/local/tomcat/webapps/

ENV JPDA_ADDRESS="0.0.0.0:8000"
ENV JPDA_TRANSPORT="dt_socket"

CMD ["catalina.sh", "jpda", "run"]
