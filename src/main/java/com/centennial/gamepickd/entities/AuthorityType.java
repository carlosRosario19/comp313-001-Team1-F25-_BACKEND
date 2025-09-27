package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.converters.RoleTypeConverter;
import com.centennial.gamepickd.util.enums.RoleType;
import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Entity
@Table(name = "AUTHORITY_TYPE")
public class AuthorityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHORITY_TYPE_ID")
    private @Nullable Integer id;

    @Column(name = "LABEL", length = 128, nullable = false, unique = true)
    @Convert(converter = RoleTypeConverter.class)
    private RoleType label;

    public AuthorityType() {}

    public AuthorityType(RoleType label) {
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleType getLabel() {
        return label;
    }

    public void setLabel(RoleType label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "AuthorityType{" +
                "id=" + id +
                ", label='" + label.val() + '\'' +
                '}';
    }
}
