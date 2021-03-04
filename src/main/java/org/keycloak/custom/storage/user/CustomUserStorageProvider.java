package org.keycloak.custom.storage.user;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.services.resources.admin.RoleMapperResource;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

/**
 * @author <a href="mailto:bbalasub@redhat.com">Bala B</a>
 * @version $Revision: 1 $
 */
@Stateful
@Local(CustomUserStorageProvider.class)
public class CustomUserStorageProvider
        implements ILocalCustomUserStorageProvider /*
                                                    * implements UserStorageProvider, UserLookupProvider,
                                                    * UserRegistrationProvider, UserQueryProvider,
                                                    * CredentialInputUpdater, CredentialInputValidator, OnUserCache
                                                    */
{
    private static final Logger logger = Logger.getLogger(CustomUserStorageProvider.class);
    public static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";

    @PersistenceContext
    protected EntityManager em;

    protected ComponentModel model;
    protected KeycloakSession session;

    protected RealmModel realmmodel;

    // private GroupModel group = new GroupModel();

    public void setModel(ComponentModel model) {
        this.model = model;
    }

    public void setSession(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Remove
    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        logger.info("getUserById: " + id);
        String persistenceId = StorageId.externalId(id);
        Tuser entity = em.find(Tuser.class,  persistenceId);
        if (entity == null) {
            logger.info("could not find UserEntity by id: " + id);
            return null;
        }
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        logger.info("getUserByUsername: " + username);
        TypedQuery<Tuser> query = em.createNamedQuery("getUserByUsername", Tuser.class);
        query.setParameter("username", username);
        List<Tuser> result = query.getResultList();
        if (result.isEmpty()) {
            logger.info("could not find username: " + username);
            return null;
        }
        return new UserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        TypedQuery<Tuser> query = em.createNamedQuery("getUserByEmail", Tuser.class);
        query.setParameter("email", email);
        List<Tuser> result = query.getResultList();
        if (result.isEmpty())
            return null;
        return new UserAdapter(session, realm, model, result.get(0));
    }
/*
    @Override
    public UserModel addUser(RealmModel realm, String username) {
        Tuser entity = new Tuser();
        entity.setId(UUID.randomUUID().toString());
        entity.setUsername(username);
        em.persist(entity);
        logger.info("added user: " + username);
        return new UserAdapter(session, realm, model, entity);
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        String persistenceId = StorageId.externalId(user.getId());
        Tuser entity = em.find(Tuser.class, persistenceId);
        if (entity == null)
            return false;
        em.remove(entity);
        return true;
    }*/
/*
    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
        String password = ((UserAdapter) delegate).getPassword();
        if (password != null) {
            user.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
        
    }
*/
    @Override
    public boolean supportsCredentialType(String credentialType) {
        return CredentialModel.PASSWORD.equals(credentialType);
    }
/*
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        UserAdapter adapter = getUserAdapter(user);
        adapter.setPassword(cred.getValue());

        return true;
    }
*/
    public UserAdapter getUserAdapter(UserModel user) {
        UserAdapter adapter = null;

        if (user instanceof CachedUserModel) {
            /*
             * if(user.getId().endsWith(":7")){
             * 
             * RoleModel role = realmmodel.getRole("tesingRole"); user.grantRole(role);
             * 
             * }
             */
            adapter = (UserAdapter) ((CachedUserModel) user).getDelegateForUpdate();
        } else {
            adapter = (UserAdapter) user;
        }
        return adapter;
    }
/*
    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType))
            return;

        getUserAdapter(user).setPassword(null);

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        if (getUserAdapter(user).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(CredentialModel.PASSWORD);
            return set;
        } else {
            return Collections.emptySet();
        }
    }
*/
    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        String password = getPassword(user);

        MessageDigest md;
        byte[] passw;


        String result = "";

        try {
            md = MessageDigest.getInstance("MD5");
            passw = cred.getValue().getBytes();
            md.reset();
            md.update(passw);

            byte[] digestedpass = md.digest();

            BigInteger no = new BigInteger(1, digestedpass);

            result = no.toString(16);
            while (result.length() < 32) { 
                result = "0" + result; 
            } 
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

        



return password != null && password.equals(result);
}

public String getPassword(UserModel user) {
String password = null;
if (user instanceof CachedUserModel) {
    password = (String)((CachedUserModel)user).getCachedWith().get(PASSWORD_CACHE_KEY);
} else if (user instanceof UserAdapter) {
    password = ((UserAdapter)user).getPassword();
}
return password;
}

@Override
public int getUsersCount(RealmModel realm) {
Object count = em.createNamedQuery("getUserCount")
        .getSingleResult();
return ((Number)count).intValue();
}

@Override
public List<UserModel> getUsers(RealmModel realm) {
return getUsers(realm, -1, -1);
}

@Override
public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {

TypedQuery<Tuser> query = em.createNamedQuery("getAllUsers", Tuser.class);
if (firstResult != -1) {
    query.setFirstResult(firstResult);
}
if (maxResults != -1) {
    query.setMaxResults(maxResults);
}
List<Tuser> results = query.getResultList();
List<UserModel> users = new LinkedList<>();
for (Tuser entity : results) users.add(new UserAdapter(session, realm, model, entity));
return users;
}

@Override
public List<UserModel> searchForUser(String search, RealmModel realm) {
return searchForUser(search, realm, -1, -1);
}

@Override
public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {
TypedQuery<Tuser> query = em.createNamedQuery("searchForUser", Tuser.class);
query.setParameter("search", "%" + search.toLowerCase() + "%");
if (firstResult != -1) {
    query.setFirstResult(firstResult);
}
if (maxResults != -1) {
    query.setMaxResults(maxResults);
}
List<Tuser> results = query.getResultList();
List<UserModel> users = new LinkedList<>();
for (Tuser entity : results) users.add(new UserAdapter(session, realm, model, entity));
return users;
}

@Override
public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
return Collections.EMPTY_LIST;
}

@Override
public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {
return Collections.EMPTY_LIST;
}

@Override
public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
return Collections.EMPTY_LIST;
}

@Override
public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {
return Collections.EMPTY_LIST;
}

@Override
public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {
return Collections.EMPTY_LIST;
}
}