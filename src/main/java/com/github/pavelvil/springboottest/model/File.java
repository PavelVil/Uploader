package com.github.pavelvil.springboottest.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"owned", "sharedBy"})
@ToString(exclude = {"owned", "sharedBy"})
public class File extends BaseModel {

    @Lob
    private byte[] data;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owned;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "sharedFiles")
    private Set<User> sharedBy = new HashSet<>();

}
