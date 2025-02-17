package org.qrcode;

public enum ErrorCorrectionLevel {
    L(7), // 7% recovery capability
    M(15), // 15% recovery capability
    Q(25), // 25% recovery capability
    H(30); // 30% recovery capability

    private final int recoveryPercentage;

    ErrorCorrectionLevel(int recoveryPercentage) {
        this.recoveryPercentage = recoveryPercentage;
    }

    public int getRecoveryPercentage() {
        return recoveryPercentage;
    }
}
