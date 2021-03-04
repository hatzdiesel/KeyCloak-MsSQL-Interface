package org.keycloak.custom.storage.user;
//imports
import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UserAdapter extends AbstractUserAdapterFederatedStorage {
    private static final Logger logger = Logger.getLogger(UserAdapter.class);
    protected Tuser entity;
    protected String keycloakId;
    protected RealmModel realmModel;

    public UserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, Tuser entity) {
        
        super(session, realm, model);
    //    this.realmModel = realm;
        this.entity = entity;
        keycloakId = StorageId.keycloakId(model, entity.getId());
       // keycloakId = entity.getId();
    }

    public String getPassword() {
        return entity.getPassword();
    }

    public void setPassword(String password) {
        entity.setPassword(password);
    }

    @Override
    public boolean isEmailVerified() {
        return true;
    }

    @Override
    public void setEmailVerified(boolean verified) {
        super.setEmailVerified(verified);
    }

    
    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        entity.setUsername(username);
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(entity.getEmail());
        entity.setEmail(email);
        
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public void setFirstName(String fullname) {
        String[] parts = entity.getFullname().split(" ");

        String rest = "";
        for (Integer i = 0; i < parts.length - 1; i++) {
            rest += parts[i] + " ";
        }
        entity.setFullname(fullname + getLastName());
        super.setFirstName(rest);
    }

    @Override
    public String getLastName() {
        String[] parts = entity.getFullname().split(" ");

        String rest = "";
        for (Integer i = 0; i < parts.length - 1; i++) {
            rest += parts[i] + " ";
        }
        return rest;
    }

    @Override
    public String getFirstName() {
        String[] parts = entity.getFullname().split(" ");

        return parts[parts.length - 1];
    }

    @Override
    public void setLastName(String name) {
        entity.setFullname(getFirstName() + name);
        super.setLastName(entity.getFullname());
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }


    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        if (name.equals("phone")) {
            entity.setPhone(value);
        } /*else if (name.equals("FK_company")) {
            entity.setFK_company(value);
        } */else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name.equals("phone")) {
            entity.setPhone(null);
        } else if (name.equals("FK_company")) {
            entity.setFK_company(null);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        if (name.equals("phone")) {
            entity.setPhone(values.get(0));
        } /*else if (name.equals("FK_company")) {
            entity.setFK_company(values.get(0));
        } */else {
            super.setAttribute(name, values);
        }
    }

    @Override
    public String getFirstAttribute(String name) {
        if (name.equals("phone")) {
            return entity.getPhone();
        } /*else if (name.equals("FK_company")) {
            return entity.getFK_company();
        } */else {
            return super.getFirstAttribute(name);
        }
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Field[] fields = entity.getClass().getDeclaredFields();
        Method[] methods = entity.getClass().getMethods();
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        // all.add("phone", entity.getPhone());
        // all.add("fk-comp", entity.getFK_company());|| field.getName().contains("fullname") || field.getName().contains("email")
        for (Field field : fields) {
            if (field.getName().contains("username") || field.getName().contains("password") || field.getName().contains("id")) {

            } else {
                field.setAccessible(true);
                try {
                    if(field.getName().contains("FK_company")){
                        company comp = entity.getFK_company();
                      //  String company = field.get(entity).getClass().getDeclaredField("company").get(comp).toString();
                        all.add("company", comp.getCompany());
                    }else if(field.getName().contains("fullname")){
                       // String fullname = field.get(entity).toString());

                        String entid = getId();
                        entid = entid.replaceFirst(":", "-");
                        entid = entid.replace(":", "_");
                        all.add("id", entid);
                        all.add("databaseID", entity.getId());
                        all.add("UserUUID",getId().split("\\:")[1]);

                        all.add("firstname", getFirstName());
                        all.add("lastname", getLastName());
                        all.add(field.getName(), field.get(entity).toString());
                        all.add("useremail", getEmail());
                    }else if(field.getName().contains("email")){
                      //  all.add(field.getName(), getEmail());
                    }
                    else{
                        if (field.get(entity) == null) {
                            all.add(field.getName(), "");
                        }else{
                            all.add(field.getName(), field.get(entity).toString());
                            if(field.getName()=="language"){
                                all.add("userlocale", field.get(entity).toString().substring(0,2));
                            }
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
      //  FieldsAndGetters.fields();
        return all;
    }

    @Override
    public List<String> getAttribute(String name) {
        if (name.equals("phone")) {
            List<String> phone = new LinkedList<>();
            phone.add(entity.getPhone());
            return phone;
        } 
       /* else if (name.equals("FK_company")){
            List<String> fkcomp = new LinkedList<>();
            fkcomp.add(entity.getFK_company());
            return fkcomp;
        }*/
         else {
            return super.getAttribute(name);
        }
    }

}