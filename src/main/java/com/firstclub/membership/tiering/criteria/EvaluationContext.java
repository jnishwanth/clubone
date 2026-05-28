package com.firstclub.membership.tiering.criteria;

import com.firstclub.membership.activity.domain.ActivitySnapshot;

/**
 * Everything a criterion needs to evaluate, assembled by the caller (cohort from
 * the user slice, metrics from the activity slice). Keeping it a plain input means
 * the tiering engine depends on neither the user nor the activity service.
 */
public record EvaluationContext(String cohort, ActivitySnapshot activity) {
}
