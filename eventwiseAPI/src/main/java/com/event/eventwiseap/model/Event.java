package com.event.eventwiseap.model;


import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity{

    @NotNull
    @NotEmpty
    @Size(max = 50)
    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    private Group group;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private User organizer;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String location;

    @NotNull
    @Size(max = 500)
    private String description;

    @NotNull
    @NotEmpty
    @Size(max = 20)
    private String type;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationTime;

    @Timestamp
    @NotNull
    private LocalDateTime dateTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Set<User> acceptedMembers = new HashSet<>();

    public void addToGroup(Group group, User user){
        this.organizer = user;
        this.group = group;
        this.acceptedMembers.add(user);
        group.addEvent(this);
    }

    public void removeFromGroup(){
        this.acceptedMembers.clear();
        group.removeEvent(this);
    }

    public void accept(User user){
        this.acceptedMembers.add(user);
    }

    public boolean isAccepted(User user){
        return this.acceptedMembers.contains(user);
    }

    public void reject(User user){
        if(isAccepted(user)){
            this.acceptedMembers.remove(user);
        }
    }




//    @OneToMany(mappedBy = "User", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//    private Set<User> rejectedMembers = new HashSet<>();


}
