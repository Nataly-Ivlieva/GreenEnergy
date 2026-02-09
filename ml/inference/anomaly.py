def detect_anomaly(
    actual_power: float,
    predicted_power: float,
    thresholds: dict,
) -> dict:
    """
    Residual-based anomaly detection
    """

    residual = actual_power - predicted_power
    abs_residual = abs(residual)

    p95 = thresholds["p95"]
    p99 = thresholds["p99"]

    if abs_residual < p95:
        status = "NORMAL"
        severity = 0
    elif abs_residual < p99:
        status = "WARNING"
        severity = 1
    else:
        status = "ANOMALY"
        severity = 2

    return {
        "status": status,
        "severity": severity,
        "residual": round(residual, 2),
        "abs_residual": round(abs_residual, 2),
    }
