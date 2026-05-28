package com.firstclub.membership.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/** autoApply so every Money field maps to a numeric column without per-field @Convert. */
@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
        return money == null ? null : money.amount();
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal value) {
        return value == null ? null : Money.of(value);
    }
}
