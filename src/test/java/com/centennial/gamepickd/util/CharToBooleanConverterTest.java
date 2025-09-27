package com.centennial.gamepickd.util;

import com.centennial.gamepickd.util.converters.CharToBooleanConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CharToBooleanConverterTest {

    private final CharToBooleanConverter converter = new CharToBooleanConverter();

    @Test
    void testConvertToDatabaseColumn_shouldReturnYForTrue() {
        assertThat(converter.convertToDatabaseColumn(true)).isEqualTo('Y');
    }

    @Test
    void testConvertToDatabaseColumn_shouldReturnNForFalse() {
        assertThat(converter.convertToDatabaseColumn(false)).isEqualTo('N');
    }

    @Test
    void testConvertToDatabaseColumn_shouldReturnNullForNull() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
    }

    @Test
    void testConvertToEntityAttribute_shouldReturnTrueForY() {
        assertThat(converter.convertToEntityAttribute('Y')).isTrue();
    }

    @Test
    void testConvertToEntityAttribute_shouldReturnFalseForN() {
        assertThat(converter.convertToEntityAttribute('N')).isFalse();
    }

    @Test
    void testConvertToEntityAttribute_shouldReturnFalseForOtherCharacters() {
        assertThat(converter.convertToEntityAttribute('X')).isFalse();
        assertThat(converter.convertToEntityAttribute('y')).isFalse();
        assertThat(converter.convertToEntityAttribute('1')).isFalse();
    }
}
