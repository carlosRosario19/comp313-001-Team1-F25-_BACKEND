package com.centennial.gamepickd.util;

import com.centennial.gamepickd.util.converters.RoleTypeConverter;
import com.centennial.gamepickd.util.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTypeConverterTest {
    private RoleTypeConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RoleTypeConverter();
    }

    @Test
    void convertToDatabaseColumn_returnsStringForValidRole() {
        assertEquals("ROLE_MEMBER", converter.convertToDatabaseColumn(RoleType.MEMBER));
        assertEquals("ROLE_CONTRIBUTOR", converter.convertToDatabaseColumn(RoleType.CONTRIBUTOR));
        assertEquals("ROLE_ADMIN", converter.convertToDatabaseColumn(RoleType.ADMIN));
    }

    @Test
    void convertToDatabaseColumn_returnsNullForNullRole() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_returnsRoleTypeForValidString() {
        assertEquals(RoleType.MEMBER, converter.convertToEntityAttribute("ROLE_MEMBER"));
        assertEquals(RoleType.CONTRIBUTOR, converter.convertToEntityAttribute("ROLE_CONTRIBUTOR"));
        assertEquals(RoleType.ADMIN, converter.convertToEntityAttribute("ROLE_ADMIN"));
    }

    @Test
    void convertToEntityAttribute_returnsNullForNullString() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToEntityAttribute_throwsExceptionForUnknownString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("UNKNOWN_ROLE")
        );

        assertEquals("Unknown role: UNKNOWN_ROLE", exception.getMessage());
    }
}
