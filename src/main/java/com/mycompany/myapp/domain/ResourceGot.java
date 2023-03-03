package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ResourceGot.
 */
@Table("resource_got")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ResourceGot implements Serializable {

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
    private User registerUser;

    @Column("register_user_id")
    private Long registerUserId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResourceGot id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getGold() {
        return this.gold;
    }

    public ResourceGot gold(Float gold) {
        this.setGold(gold);
        return this;
    }

    public void setGold(Float gold) {
        this.gold = gold;
    }

    public Float getWood() {
        return this.wood;
    }

    public ResourceGot wood(Float wood) {
        this.setWood(wood);
        return this;
    }

    public void setWood(Float wood) {
        this.wood = wood;
    }

    public Float getFer() {
        return this.fer;
    }

    public ResourceGot fer(Float fer) {
        this.setFer(fer);
        return this;
    }

    public void setFer(Float fer) {
        this.fer = fer;
    }

    public User getRegisterUser() {
        return this.registerUser;
    }

    public void setRegisterUser(User user) {
        this.registerUser = user;
        this.registerUserId = user != null ? user.getId() : null;
    }

    public ResourceGot registerUser(User user) {
        this.setRegisterUser(user);
        return this;
    }

    public Long getRegisterUserId() {
        return this.registerUserId;
    }

    public void setRegisterUserId(Long user) {
        this.registerUserId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceGot)) {
            return false;
        }
        return id != null && id.equals(((ResourceGot) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResourceGot{" +
            "id=" + getId() +
            ", gold=" + getGold() +
            ", wood=" + getWood() +
            ", fer=" + getFer() +
            "}";
    }
}
