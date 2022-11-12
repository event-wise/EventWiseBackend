package com.event.eventwiseap.model;


import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


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

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // non null relationship optional = false
    @JoinColumn(name = "group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // the event will be deleted on the action of related group deletion
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) // the event will be deleted on the action of organizer user deletion
    private User organizer;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(name = "event_accepted_members",
            joinColumns = {@JoinColumn(name = "event_id")},
            inverseJoinColumns = {@JoinColumn(name = "accepted_members_id")}
    )
    private Set<User> acceptedMembers = new HashSet<>();

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

    public boolean acceptedBy(User user){
        boolean eventAccepted = this.acceptedMembers.add(user);
        boolean userAccepted = user.acceptEvent(this);

        return userAccepted && eventAccepted;
    }


    public boolean isAccepted(User user){
        return this.acceptedMembers.contains(user);
    }

    public boolean rejectedBy(User user){
        boolean eventRejected = this.acceptedMembers.remove(user);
        boolean userRejected = user.rejectEvent(this);
        return eventRejected && userRejected;
    }

}
