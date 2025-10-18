package com.centennial.gamepickd.util.converters;

import com.centennial.gamepickd.util.enums.PlatformType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PlatformTypeConverter implements AttributeConverter<PlatformType, String> {
    @Override
    public String convertToDatabaseColumn(PlatformType platformType) {
        return platformType != null ? platformType.getVal() : null;
    }

    @Override
    public PlatformType convertToEntityAttribute(String dbValue) {
        return dbValue != null ? PlatformType.fromValue(dbValue) : null;
    }
}
