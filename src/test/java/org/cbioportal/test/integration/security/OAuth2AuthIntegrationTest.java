package org.cbioportal.test.integration.security;

import org.cbioportal.PortalApplication;
import org.cbioportal.test.integration.security.util.Util;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.cbioportal.test.integration.security.AbstractContainerTest.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    classes = {PortalApplication.class}
)
@TestPropertySource(
    properties = {
        "authenticate=oauth2",
        "dat.method=oauth2",
        // DB settings (also see MysqlInitializer)
        "spring.datasource.driverClassName=com.mysql.jdbc.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect",
        // OAuth2 settings
        "spring.security.oauth2.client.registration.keycloak.redirect-uri=http://host.testcontainers.internal:8080/login/oauth2/code/keycloak",
        "spring.security.oauth2.client.provider.keycloak.user-name-attribute=email",
        "spring.security.oauth2.client.registration.keycloak.client-name=cbioportal_oauth2",
        "spring.security.oauth2.client.registration.keycloak.client-id=cbioportal_oauth2",
        "spring.security.oauth2.client.registration.keycloak.client-secret=client_secret",
        "spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.keycloak.client-authentication-method=client_secret_post",
        "spring.security.oauth2.client.registration.keycloak.scope=openid,email,roles",
        "spring.security.oauth2.client.user-info-roles-path=resource_access::cbioportal::roles",
        // Keycloak host settings (also see KeycloakInitializer)
        "dat.oauth2.clientId=cbioportal_oauth2",
        "dat.oauth2.clientSecret=client_secret",
        // Redirect URL to cBiopPortal application from perspective of browser
        "dat.oauth2.redirectUri=http://host.testcontainers.internal:8080/api/data-access-token/oauth2",
        "dat.oauth2.jwtRolesPath=resource_access::cbioportal::roles",
        "session.service.url=http://localhost:5000/api/sessions/my_portal/"
    }
)
@ContextConfiguration(initializers = {
    MyMysqlInitializer.class,
    MyOAuth2KeycloakInitializer.class,
    PortInitializer.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext // needed to reuse port 8080 for multiple tests
public class OAuth2AuthIntegrationTest extends AbstractContainerTest {

    public final static String CBIO_URL_FROM_BROWSER =
        String.format("http://host.testcontainers.internal:%d", CBIO_PORT);
    
    @Test
    public void a_loginSuccess() {
        Util.testLogin(CBIO_URL_FROM_BROWSER, chromedriverContainer);
    }
    
    @Test
    public void b_downloadOfflineToken() throws Exception {
        Util.testDownloadOfflineToken(CBIO_URL_FROM_BROWSER, chromedriverContainer);
    }

    @Test
    public void c_logoutSuccess() {
        Util.testLogout(CBIO_URL_FROM_BROWSER, chromedriverContainer);
    }

    @Test
    public void d_loginAgainSuccess() {
        Util.testLoginAgain(CBIO_URL_FROM_BROWSER, chromedriverContainer);
    }
    
}