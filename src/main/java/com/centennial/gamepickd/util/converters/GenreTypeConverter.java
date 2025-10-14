package com.centennial.gamepickd.util.converters;

import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenreTypeConverter implements AttributeConverter<GenreType, String> {
    @Override
    public String convertToDatabaseColumn(GenreType genreType) {
        return genreType != null ? genreType.getVal() : null;
    }

    @Override
    public GenreType convertToEntityAttribute(String dbValue) {
        return dbValue != null ? GenreType.fromLabel(dbValue) : null;
    }
}
