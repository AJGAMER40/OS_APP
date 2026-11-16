package com.zerovault.app.threat

data class ThreatLogEvent(
    val timestamp: Long,
    val type: ThreatEventType,
    val description: String,
    val severity: ThreatSeverity
)

enum class ThreatEventType {
    AUTH_SUCCESS,
    AUTH_FAILURE,
    BRUTE_FORCE_TRIGGER,
    RAM_SANITIZED,
    WORKMANAGER_ENGAGED,
    LOCKOUT_EXPIRED,
    MANUAL_LOCK,
    COERCION_DURESS
}

enum class ThreatSeverity {
    INFO,
    WARNING,
    CRITICAL
}
