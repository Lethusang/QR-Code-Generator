package org.qrcode;

public class Main {
    public static void main(String[] args) throws Exception {
        String data = "Hello, World!";
        String outputPath = "custom_qr.png";

        // Calculate required version
        int requiredVersion = calculateRequiredVersion(data);

        // Create QR matrix with calculated version
        CustomQRMatrix qrMatrix = new CustomQRMatrix(
                21 + ((requiredVersion - 1) * 4), // Calculate size based on version
                ErrorCorrectionLevel.L // Using Level L for maximum capacity
        );

        qrMatrix.encodeData(data);
        qrMatrix.saveToFile(outputPath);
    }

    private static int calculateRequiredVersion(String data) throws Exception {
        byte[] bytes = data.getBytes("UTF-8");
        String binary = new StringBuilder()
                .append("0010") // Mode indicator for byte mode
                .append(String.format("%8s", Integer.toBinaryString(bytes.length)))
                .toString().replaceAll(" ", "0");

        System.out.println("Data analysis:");
        System.out.println("Original text: " + data);
        System.out.println("UTF-8 bytes: " + bytes.length);
        System.out.println("Binary length: " + binary.length());
        System.out.println("Binary data: " + binary);

        // Support versions 1-40 (full QR code range)
        for (int version = 1; version <= 40; version++) {
            ErrorCorrectionLevel ecl = ErrorCorrectionLevel.L; // Using Level L for maximum capacity
            int capacity = getCapacityForVersion(version, ecl);
            System.out.printf("Version %d capacity: %d bits (%d bytes)%n",
                    version, capacity, (capacity + 7) / 8);

            if (binary.length() <= capacity) {
                System.out.println("\nSelected version: " + version);
                return version;
            }
        }
        throw new Exception("Data too large for supported versions");
    }

    private static int getCapacityForVersion(int version, ErrorCorrectionLevel ecl) {
        // Extended capacity table for versions 1-40
        if (version <= 5) {
            switch(version) {
                case 1: return ecl == ErrorCorrectionLevel.L ? 26 :
                        ecl == ErrorCorrectionLevel.M ? 20 :
                                ecl == ErrorCorrectionLevel.Q ? 16 : 13;
                case 2: return ecl == ErrorCorrectionLevel.L ? 48 :
                        ecl == ErrorCorrectionLevel.M ? 36 :
                                ecl == ErrorCorrectionLevel.Q ? 28 : 22;
                case 3: return ecl == ErrorCorrectionLevel.L ? 72 :
                        ecl == ErrorCorrectionLevel.M ? 54 :
                                ecl == ErrorCorrectionLevel.Q ? 42 : 32;
                case 4: return ecl == ErrorCorrectionLevel.L ? 88 :
                        ecl == ErrorCorrectionLevel.M ? 68 :
                                ecl == ErrorCorrectionLevel.Q ? 52 : 40;
                case 5: return ecl == ErrorCorrectionLevel.L ? 108 :
                        ecl == ErrorCorrectionLevel.M ? 84 :
                                ecl == ErrorCorrectionLevel.Q ? 64 : 49;
                default: throw new IllegalArgumentException("Unsupported version");
            }
        } else {
            // Capacity increases by 32 characters per version after version 5
            int baseCapacity = getCapacityForVersion(5, ecl);
            return baseCapacity + ((version - 5) * 32);
        }
    }
}