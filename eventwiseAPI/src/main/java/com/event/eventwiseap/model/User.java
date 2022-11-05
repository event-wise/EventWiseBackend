package com.event.eventwiseap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Set<Role> roles = new HashSet<>();

    /* TO DO:
    * Relate to Group entity: many to many managed by Group
    * Accepted events: many to many mapped by User
    * Rejected events: many to many mapped by User
    * Pending events:  many to many mapped by User
    * */


}
