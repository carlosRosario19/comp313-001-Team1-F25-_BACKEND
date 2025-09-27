package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.RoleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorityTypeTest {

    @Test
    void constructor_setsLabelCorrectly() {
        AuthorityType authority = new AuthorityType(RoleType.ADMIN);

        assertNull(authority.getId()); // not set until persisted
        assertEquals(RoleType.ADMIN, authority.getLabel());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        AuthorityType authority = new AuthorityType();
        authority.setId(100);
        authority.setLabel(RoleType.MEMBER);

        assertEquals(100, authority.getId());
        assertEquals(RoleType.MEMBER, authority.getLabel());
    }

    @Test
    void toString_includesIdAndLabel() {
        AuthorityType authority = new AuthorityType(RoleType.CONTRIBUTOR);
        authority.setId(200);

        String str = authority.toString();
        assertTrue(str.contains("200"));
        assertTrue(str.contains(RoleType.CONTRIBUTOR.val()));
    }

}
