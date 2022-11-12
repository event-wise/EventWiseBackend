package com.event.eventwiseap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{
    @NotNull
    @Size(max = 20, min = 5)
    @Column(unique = true)
    private String username;

    @NotNull
    @NotEmpty
    @Size(max = 50)
    @Email
    private String email;

    @NotEmpty
    @NotNull
    @Size(min = 5, max = 10)
    private String displayedName;

    @NotNull
    @Size(min = 5)
    private String password;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String location;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "acceptedMembers")
    private Set<Event> acceptedEvents = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, mappedBy = "groupMembers")
    private Set<Group> groups = new HashSet<>();



    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User)){
            return false;
        }
        User user = (User) obj;
        return this.id.equals(user.getId());
    }

    public boolean addGroup(Group group){
        return this.groups.add(group);
    }

    public boolean removeGroup(Group group){
        return this.groups.remove(group);
    }

    public boolean acceptEvent(Event event){
        return this.acceptedEvents.add(event);
    }

    public boolean rejectEvent(Event event){
        return this.acceptedEvents.remove(event);
    }

}
