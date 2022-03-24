package de.seinab.user.models;

import de.seinab.EventGroup;
import de.seinab.backend.security.entities.FormPermission;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.util.StreamUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String email;

    @NotNull
    private String password, firstName, lastName;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.EAGER)
    private List<EventGroup> eventGroupList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "userId")
    private List<Role> roleList;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = FormPermission.class)
    @JoinColumn(name = "user_id")
    private List<FormPermission> formPermissionList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<EventGroup> getEventGroupList() {
        return eventGroupList;
    }

    public void setEventGroupList(List<EventGroup> eventGroupList) {
        this.eventGroupList = eventGroupList;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public MultiValueMap<String, FormPermission> getFormPermissionsByEventGroupName() {
        return formPermissionList.stream().collect(StreamUtils.toMultiMap(fp -> fp.getForm().getEventGroup().getName(), fp -> fp));
    }

    public List<FormPermission> getFormPermissionList() {
        return formPermissionList;
    }

    public void setFormPermissionList(List<FormPermission> formPermissionList) {
        this.formPermissionList = formPermissionList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
