package com.centennial.gamepickd.util.converters;

import com.centennial.gamepickd.util.enums.PublisherType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PublisherTypeConverter implements AttributeConverter<PublisherType, String> {
    @Override
    public String convertToDatabaseColumn(PublisherType publisherType) {
        return publisherType != null ? publisherType.getVal() : null;
    }

    @Override
    public PublisherType convertToEntityAttribute(String dbValue) {
        return dbValue != null ? PublisherType.fromValue(dbValue) : null;
    }
}
