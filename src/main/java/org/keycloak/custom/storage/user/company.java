package org.keycloak.custom.storage.user;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class company {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "company")
    private String company;
    
    @OneToMany(mappedBy = "FK_company")
    private List<Tuser> users;

    public String getCompany(){
        return this.company;
    }
}
