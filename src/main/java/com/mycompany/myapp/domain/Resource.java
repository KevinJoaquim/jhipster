package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Resource.
 */
@Table("resource")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("gold")
    private Float gold;

    @Column("wood")
    private Float wood;

    @Column("fer")
    private Float fer;

    @Transient
    private Client client;

    @Column("client_id")
    private Long clientId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Resource id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getGold() {
        return this.gold;
    }

    public Resource gold(Float gold) {
        this.setGold(gold);
        return this;
    }

    public void setGold(Float gold) {
        this.gold = gold;
    }

    public Float getWood() {
        return this.wood;
    }

    public Resource wood(Float wood) {
        this.setWood(wood);
        return this;
    }

    public void setWood(Float wood) {
        this.wood = wood;
    }

    public Float getFer() {
        return this.fer;
    }

    public Resource fer(Float fer) {
        this.setFer(fer);
        return this;
    }

    public void setFer(Float fer) {
        this.fer = fer;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.clientId = client != null ? client.getId() : null;
    }

    public Resource client(Client client) {
        this.setClient(client);
        return this;
    }

    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long client) {
        this.clientId = client;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        return id != null && id.equals(((Resource) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Resource{" +
            "id=" + getId() +
            ", gold=" + getGold() +
            ", wood=" + getWood() +
            ", fer=" + getFer() +
            "}";
    }
}
