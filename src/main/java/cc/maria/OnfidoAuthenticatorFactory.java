package cc.maria;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OnfidoAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {
    private final OnfidoAuthenticator SINGLETON = new OnfidoAuthenticator();

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        ProviderConfigProperty apiRegion = new ProviderConfigProperty();
        apiRegion.setName("onfido.region");
        apiRegion.setLabel("Onfido API Base Domain");
        apiRegion.setType(ProviderConfigProperty.LIST_TYPE);
        apiRegion.setOptions(new ArrayList<>() {{
            add("Canada");
            add("European Union");
            add("United States");
        }});
        configProperties.add(apiRegion);

        ProviderConfigProperty tokenProperty = new ProviderConfigProperty();
        tokenProperty.setName("onfido.token");
        tokenProperty.setLabel("Onfido API Token");
        tokenProperty.setType(ProviderConfigProperty.STRING_TYPE);
        tokenProperty.setSecret(true);
        configProperties.add(tokenProperty);

        ProviderConfigProperty reportType = new ProviderConfigProperty();
        reportType.setName("onfido.reportType");
        reportType.setLabel("Onfido Report Type");
        reportType.setType(ProviderConfigProperty.LIST_TYPE);
        reportType.setOptions(new ArrayList<>() {{
            add("photo");
            add("photo_fully_auto");
            add("video");
            add("motion");
        }});
        configProperties.add(reportType);
    }

    @Override
    public String getDisplayType() {
        return "Onfido Facial Similarity";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] {AuthenticationExecutionModel.Requirement.REQUIRED};
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Onfido biometric facial similarity check";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
        return SINGLETON;
    }

    @Override
    public void init(Config.Scope scope) {}

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "onfido-facial-similarity";
    }
}
