package org.keycloak.custom.storage.user;

import javax.naming.InitialContext;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

/**
 * @author <a href="mailto:bbalasub@redhat.com">Bala B</a>
 * @version $Revision: 1 $
 */
public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {
    private static final Logger logger = Logger.getLogger(CustomUserStorageProviderFactory.class);


    @Override
    public CustomUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        try {
            InitialContext ctx = new InitialContext();
            CustomUserStorageProvider provider = (CustomUserStorageProvider)ctx.lookup("java:global/custom-user-storage-jpa/" + CustomUserStorageProvider.class.getSimpleName());
            provider.setModel(model);
            provider.setSession(session);
            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return "custom-user-storage-jpa";
    }

    @Override
    public String getHelpText() {
        return "JPA Example UserEntity Storage Provider";
    }

    @Override
    public void close() {
        logger.info("<<<<<< Closing factory");

    }
}