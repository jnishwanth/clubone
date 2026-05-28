package com.firstclub.membership.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Maps {@link Money} to a single numeric DB column (autoApply: every Money field
 * is converted without per-field annotations). Keeps entity tables clean and
 * demonstrates the value-object ↔ column boundary.
 */
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
