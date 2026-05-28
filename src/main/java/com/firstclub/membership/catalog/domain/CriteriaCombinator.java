package com.firstclub.membership.catalog.domain;

/**
 * How a tier's qualifying criteria combine. ALL = every criterion must pass (AND),
 * ANY = at least one (OR). The tiering engine builds an And/Or Composite from this,
 * so the config stays simple while the engine remains composable/extensible.
 */
public enum CriteriaCombinator {
    ALL,
    ANY
}
