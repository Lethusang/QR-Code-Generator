package org.qrcode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CustomQRMatrix {
    private boolean[][] matrix;
    private int version;
    private ErrorCorrectionLevel ecl;

    public CustomQRMatrix(int size, ErrorCorrectionLevel ecl) {
        this.matrix = new boolean[size][size];
        this.version = calculateVersion(size);
        this.ecl = ecl;
        initializeFinderPatterns();
        initializeTimingPatterns();
        initializeAlignmentPatterns();
    }

    private int calculateVersion(int size) {
        // QR codes start at version 1 (21x21) and increase by 4 modules each version
        return (size - 21) / 4 + 1;
    }

    private void initializeFinderPatterns() {
        // Top-left finder pattern
        drawFinderPattern(0, 0);
        // Top-right finder pattern
        drawFinderPattern(matrix.length - 7, 0);
        // Bottom-left finder pattern
        drawFinderPattern(0, matrix.length - 7);
    }

    private void drawFinderPattern(int x, int y) {
        // Draw the 7x7 finder pattern with inner 5x5 dark square and timing patterns
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrix[x + i][y + j] =
                        (i == 0 || i == 6 || j == 0 || j == 6 ||
                                (i >= 2 && i <= 4 && j >= 2 && j <= 4));
            }
        }
    }
    private void initializeTimingPatterns() {
        // Horizontal timing pattern
        for (int i = 8; i < matrix.length - 8; i++) {
            matrix[i][6] = (i % 2 == 0);
        }
        // Vertical timing pattern
        for (int i = 8; i < matrix.length - 8; i++) {
            matrix[6][i] = (i % 2 == 0);
        }
    }
    private void initializeAlignmentPatterns() {
        int[] positions = calculateAlignmentPositions();

        for (int pos : positions) {
            if (pos < matrix.length - 4) { // Ensure enough space for alignment pattern
                drawAlignmentPattern(pos, pos);
            }
        }
    }

    private int[] calculateAlignmentPositions() {
        switch(version) {
            case 1:
                return new int[]{6, 18};
            case 2:
                return new int[]{6, 22};
            case 3:
                return new int[]{6, 26};
            case 4:
                return new int[]{6, 30};
            default: // Version 5 and above
                return new int[]{6, 18, 42};
        }
    }


    private void drawAlignmentPattern(int x, int y) {
        // Draw 5x5 pattern with dark center square
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (x + i < matrix.length && y + j < matrix.length) { // Boundary check
                    matrix[x + i][y + j] =
                            (i == 0 || i == 4 || j == 0 || j == 4 ||
                                    (i >= 1 && i <= 3 && j >= 1 && j <= 3));
                }
            }
        }
    }


    public void encodeData(String data) throws Exception {
        // Convert string to binary
        byte[] bytes = data.getBytes("UTF-8");
        StringBuilder binary = new StringBuilder();

        // Add mode indicator (0010 for byte mode)
        binary.append("0010");

        // Add data length (8 bits)
        binary.append(String.format("%8s", Integer.toBinaryString(bytes.length)));

        // Add actual data bits
        for (byte b : bytes) {
            String binByte = String.format("%8s", Integer.toBinaryString(b & 0xFF));
            binary.append(binByte);
        }

        // Add padding to reach capacity
        int capacity = getCapacity();
        while (binary.length() < capacity) {
            binary.append(getPaddingPattern(binary.length()));
        }

        // Place data in matrix using interleaved pattern
        placeData(binary.toString());
    }

    private void placeData(String binary) {
        int pos = matrix.length - 8;
        boolean goingUp = false;

        for (char bit : binary.toCharArray()) {
            if (pos < 6) pos = matrix.length - 8; // Skip timing patterns
            if (goingUp) {
                pos--;
                if (pos < 6) {
                    pos += 2;
                    goingUp = false;
                }
            } else {
                pos++;
                if (pos >= matrix.length - 8) {
                    pos -= 2;
                    goingUp = true;
                }
            }

            matrix[pos][6] = (bit == '1');
        }
    }

    public void saveToFile(String path) throws IOException {
        BufferedImage image = new BufferedImage(matrix.length, matrix.length,
                BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                image.setRGB(i, j, matrix[i][j] ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ImageIO.write(image, "png", new File(path));
    }

    private int getCapacity() {
        // Calculate capacity based on QR version and error correction level
        switch(version) {
            case 1:
                return ecl == ErrorCorrectionLevel.L ? 26 :
                        ecl == ErrorCorrectionLevel.M ? 20 :
                                ecl == ErrorCorrectionLevel.Q ? 16 :
                                        13;
            case 2:
                return ecl == ErrorCorrectionLevel.L ? 48 :
                        ecl == ErrorCorrectionLevel.M ? 36 :
                                ecl == ErrorCorrectionLevel.Q ? 28 :
                                        22;
            case 3:
                return ecl == ErrorCorrectionLevel.L ? 72 :
                        ecl == ErrorCorrectionLevel.M ? 54 :
                                ecl == ErrorCorrectionLevel.Q ? 42 :
                                        32;
            case 4:
                return ecl == ErrorCorrectionLevel.L ? 88 :
                        ecl == ErrorCorrectionLevel.M ? 68 :
                                ecl == ErrorCorrectionLevel.Q ? 52 :
                                        40;
            default: // Version 5
                return ecl == ErrorCorrectionLevel.L ? 108 :
                        ecl == ErrorCorrectionLevel.M ? 84 :
                                ecl == ErrorCorrectionLevel.Q ? 64 :
                                        49;
        }
    }

    private char getPaddingPattern(int length) {
        // Pattern 11101100 (0xEC in hex) repeated as needed
        String paddingPattern = "11101100";
        int remainder = (8 - (length % 8)) % 8;

        if (remainder == 0) {
            return '0'; // No padding needed
        }

        StringBuilder padding = new StringBuilder();
        while (padding.length() < remainder) {
            padding.append(paddingPattern);
        }

        return padding.charAt(remainder - 1);
    }
}
