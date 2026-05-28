package com.firstclub.membership.catalog.domain;

import java.math.BigDecimal;

/** How a numeric criterion compares an actual metric against its threshold. */
public enum ComparisonOperator {
    GTE {
        @Override
        public boolean test(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) >= 0;
        }
    },
    GT {
        @Override
        public boolean test(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) > 0;
        }
    },
    EQ {
        @Override
        public boolean test(BigDecimal actual, BigDecimal threshold) {
            return actual.compareTo(threshold) == 0;
        }
    };

    public abstract boolean test(BigDecimal actual, BigDecimal threshold);
}
