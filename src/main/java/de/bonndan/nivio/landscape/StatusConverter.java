package de.bonndan.nivio.landscape;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.toString();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Status.from(dbData);
    }
}