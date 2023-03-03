package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UserProfile.
 */
@Table("user_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    private User user;

    @Transient
    private ResourceData resource;

    @Column("user_id")
    private Long userId;

    @Column("resource_id")
    private Long resourceId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public UserProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public ResourceData getResource() {
        return this.resource;
    }

    public void setResource(ResourceData resourceData) {
        this.resource = resourceData;
        this.resourceId = resourceData != null ? resourceData.getId() : null;
    }

    public UserProfile resource(ResourceData resourceData) {
        this.setResource(resourceData);
        return this;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceData) {
        this.resourceId = resourceData;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        return id != null && id.equals(((UserProfile) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            "}";
    }
}
