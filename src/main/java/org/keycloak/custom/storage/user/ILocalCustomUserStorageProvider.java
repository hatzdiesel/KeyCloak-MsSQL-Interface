package org.keycloak.custom.storage.user;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

/**
 * @author <a href="mailto:bbalasub@redhat.com">Bala B</a>
 * @version $Revision: 1 $x	
 */

public interface ILocalCustomUserStorageProvider extends UserStorageProvider,
UserLookupProvider,
//UserRegistrationProvider,
UserQueryProvider,
CredentialInputValidator{

	void setModel(ComponentModel model);

	void setSession(KeycloakSession session);

}

