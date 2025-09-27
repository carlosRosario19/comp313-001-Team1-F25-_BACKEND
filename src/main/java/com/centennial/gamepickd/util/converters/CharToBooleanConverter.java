package com.centennial.gamepickd.util.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class CharToBooleanConverter implements AttributeConverter<Boolean, Character> {
    @Override
    public Character convertToDatabaseColumn(Boolean aBoolean) {
        if (aBoolean == null) return null;
        return aBoolean ? 'Y' : 'N';
    }

    @Override
    public Boolean convertToEntityAttribute(Character character) {
        return character == 'Y';
    }
}
