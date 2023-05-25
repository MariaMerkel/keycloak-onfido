package cc.maria;

import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Applicant;
import com.onfido.models.Check;
import com.onfido.models.Document;
import com.onfido.models.SdkToken;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.Map;

public class OnfidoAuthenticator implements Authenticator {
    private static Onfido getOnfidoClient (AuthenticationFlowContext authenticationFlowContext) {
        Map<String, String> config = authenticationFlowContext.getAuthenticatorConfig().getConfig();

        Onfido.Builder builder = Onfido.builder().apiToken(config.get("onfido.token"));

        switch (config.get("onfido.region")) {
            case "Canada":
                builder.regionCA();
                break;

            case "European Union":
                builder.regionEU();
                break;

            case "United States":
                builder.regionUS();
                break;
        }

        return builder.build();
    }

    @Override
    public void authenticate(AuthenticationFlowContext authenticationFlowContext) {
        Onfido onfido = getOnfidoClient(authenticationFlowContext);
        Map<String, String> config = authenticationFlowContext.getAuthenticatorConfig().getConfig();

        try {
            Applicant applicant = onfido.applicant.find(authenticationFlowContext.getUser().getFirstAttribute("onfido_applicant_id"));
            if (onfido.document.list(applicant.getId()).size() == 0) {
                authenticationFlowContext.failure(AuthenticationFlowError.INVALID_USER);
                return;
            }

            String sdkToken = onfido.sdkToken.generate(SdkToken.request().applicantId(applicant.getId()));

            String variant = null;
            switch (config.get("onfido.reportType")) {
                case "photo":
                case "photo_fully_auto":
                    variant = "standard";
                    break;

                case "video":
                    variant = "video";
                    break;

                case "motion":
                    variant = "motion";
                    break;
            }

            authenticationFlowContext.challenge(authenticationFlowContext.form().setAttribute("sdk_token", sdkToken).setAttribute("variant", variant).createForm("onfido.ftl"));
        } catch (OnfidoException e) {
            authenticationFlowContext.failure(AuthenticationFlowError.CREDENTIAL_SETUP_REQUIRED);
        }
    }

    @Override
    public void action(AuthenticationFlowContext authenticationFlowContext) {
        Onfido onfido = getOnfidoClient(authenticationFlowContext);
        Map<String, String> config = authenticationFlowContext.getAuthenticatorConfig().getConfig();

        try {
            Applicant applicant = onfido.applicant.find(authenticationFlowContext.getUser().getFirstAttribute("onfido_applicant_id"));

            String reportType = null;

            switch (config.get("onfido.reportType")) {
                case "photo":
                    reportType = "facial_similarity_photo";
                    break;

                case "photo_fully_auto":
                    reportType = "facial_similarity_photo_fully_auto";
                    break;

                case "video":
                    reportType = "facial_similarity_video";
                    break;

                case "motion":
                    reportType = "facial_similarity_motion";
                    break;
            }

            Check check = onfido.check.create(
                    Check.request()
                            .applicantId(applicant.getId())
                            .reportNames(reportType)
                            .asynchronous(false)
            );

            if (onfido.report.list(check.getId()).get(0).getResult().equalsIgnoreCase("clear"))
                authenticationFlowContext.success();
            else
                authenticationFlowContext.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        } catch (OnfidoException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return userModel.getFirstAttribute("onfido_applicant_id") != null;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {}

    @Override
    public void close() {}
}
