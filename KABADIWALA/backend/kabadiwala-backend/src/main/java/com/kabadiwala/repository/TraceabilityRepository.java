package com.kabadiwala.repository;

// This interface is intentionally empty.
// TraceabilityRecord is accessed via TraceabilityRecordRepository.
// This file is retained for package completeness only and will not be registered as a Spring bean
// because it is NOT annotated with @Repository and does not extend JpaRepository.

/**
 * @deprecated Use TraceabilityRecordRepository instead.
 */
@Deprecated
public final class TraceabilityRepository {
    private TraceabilityRepository() {}
}
