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

    @OneToOne
    private Group group;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
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
    private LocalDateTime creationTime;

    @Timestamp
    @NotNull
    @NotEmpty
    private LocalDateTime dateTime;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<User> acceptedMembers = new HashSet<>();

//    @OneToMany(mappedBy = "User", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//    private Set<User> rejectedMembers = new HashSet<>();


}
