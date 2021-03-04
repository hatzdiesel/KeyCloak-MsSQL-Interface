package org.keycloak.custom.storage.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils.Null;

/**
 * @author <a href="mailto:bbalasub@redhat.com">Bala B</a>
 * @version $Revision: 1 $
 */

@NamedQueries({ @NamedQuery(name = "getUserByUsername", query = "select u from Tuser u where u.username = :username"),
        @NamedQuery(name = "getUserByEmail", query = "select u from Tuser u where u.email = :email"),
        @NamedQuery(name = "getUserCount", query = "select count(u) from Tuser u"),
        @NamedQuery(name = "getAllUsers", query = "select u from Tuser u"),
        @NamedQuery(name = "searchForUser", query = "select u from Tuser u where "
                + "( lower(u.username) like :search or u.email like :search ) order by u.username"), })
@Entity
@Table(name = "Tuser")
@SecondaryTables({
        @SecondaryTable(name = "detail_ticketsystem", pkJoinColumns = @PrimaryKeyJoinColumn(name = "fk_user")),
        @SecondaryTable(name = "Tauthorization", pkJoinColumns = @PrimaryKeyJoinColumn(name = "fk_user")) })
public class Tuser {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "job_title")
    private String job_title;

    @Column(name = "isAdmin")
    private String isAdmin;

    @Column(name = "isUser")
    private String isUser;
    // @Column(name = "FK_company")
    // private String FK_company;

    // authorization table
    @Column(name = "hds2", table = "Tauthorization")
    private String hds2 = null;

    @Column(name = "documentportal", table = "Tauthorization")
    private String documentportal = null;

    @Column(name = "applications", table = "Tauthorization")
    private String applications = null;

    @Column(name = "ticketsystem", table = "Tauthorization")
    private String ticketsystem = null;

    @Column(name = "moodle", table = "Tauthorization")
    private String moodle = null;

    @Column(name = "ecommerce", table = "Tauthorization")
    private String ecommerce = null;

    @Column(name = "motorpanel", table = "Tauthorization")
    private String motorpanel = null;

    @Column(name = "iotdashboard", table = "Tauthorization")
    private String iotdashboard = null;

    @Column(name = "motorregistry", table = "Tauthorization")
    private String motorregistry = null;

    // detail_ticketsystem table
    // @Column(name = "id", table = "detail_ticketsystem")
    // private String tkId;

    @Column(name = "role", table = "detail_ticketsystem")
    private String role;

    @Column(name = "timezone", table = "detail_ticketsystem")
    private String timezone;

    @Column(name = "language", table = "detail_ticketsystem")
    private String language;

    @Column(name = "sales_org", table = "detail_ticketsystem")
    private String sales_org;

    @Column(name = "distr_chan", table = "detail_ticketsystem")
    private String distr_chan;

    @Column(name = "division", table = "detail_ticketsystem")
    private String division;

    @Column(name = "sales_office", table = "detail_ticketsystem")
    private String sales_office;

    @Column(name = "maintplant", table = "detail_ticketsystem")
    private String maintplant;

    @Column(name = "sap_name", table = "detail_ticketsystem")
    private String sap_name;

    // company table
    @ManyToOne
    @JoinColumn(name = "FK_company")
    private company FK_company;

    //private boolean emailVerified = true;

    /*
     * public user() { this.id = username; this.username = username; this.email=
     * email; this.phone = phone; this.fullname = fullname; this.password =
     * password; this.FK_company = fk_company; }
     */

    public String getId() {
        return id;
    }
/*
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }*/

    public company getFK_company() {
        return FK_company;
    }

    public void setFK_company(company fK_company) {
        this.FK_company = fK_company;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

	@Override
	public String toString() {
		return "user [id=" + id + ", username=" + username + ", email=" + email
		 + ", password=" + password  + ", phone=" + phone + ", fullname=" + fullname + "]";
	}

}

