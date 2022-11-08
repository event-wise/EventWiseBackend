package com.event.eventwiseap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
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
public class Group extends BaseEntity {

    @NotNull
    @NotEmpty
    @Size(max = 20, min = 1)
    private String groupName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private User owner;

//    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
//    private Set<User> members = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    private Set<Event> events = new HashSet<>();



    /* To DO
    * Relate to active events mapped by group
    * Relate to passive events mapped by group
    * */

}
