package com.event.eventwiseap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Entity
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group extends BaseEntity {

    @NotNull
    @NotEmpty
    @Size(max = 20, min = 1)
    private String groupName;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String location;

    @NotNull
    @Size(max = 500)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH})
    @JoinTable(name = "group_members",
            joinColumns = {@JoinColumn(name = "group_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    Set<User> groupMembers = new HashSet<>();

    public boolean addMember(User member){

        boolean memberAdded = this.groupMembers.add(member);
        boolean groupAdded = member.addGroup(this);
        return memberAdded && groupAdded;
    }

    public boolean removeMember(User member){
        boolean memberRemoved = this.groupMembers.remove(member);
        boolean groupRemoved = member.removeGroup(this);
        if(this.isOwner(member)) assignOwner();
        return memberRemoved && groupRemoved;
    }

    public boolean isEmpty(){
        return this.groupMembers.isEmpty();
    }

    public boolean isOwner(User member){
        return this.owner.getId().equals(member.getId());
    }
    public void assignOwner(){
        if(!this.isEmpty()){
            Iterator<User> it = this.groupMembers.iterator();
            this.owner = it.next();
        }
    }


}
