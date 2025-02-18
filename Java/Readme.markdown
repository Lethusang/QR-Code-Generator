# QR Code Generator
A Java implementation of a QR code generator without external dependencies.

## Features
- Custom QR matrix generation
- Automatic version selection
- Error correction support
- Detailed logging and debugging
- Pure Java implementation (no external dependencies)

## Installation
Clone the repository:
```bash
```

## Usage
```java
String data = "Hello, World!";
String outputPath = "qr-code.png";

// Calculate required version
int requiredVersion = calculateRequiredVersion(data);

// Create QR matrix with calculated version
CustomQRMatrix qrMatrix = new CustomQRMatrix(
    21 + ((requiredVersion - 1) * 4), // Calculate size based on version
    ErrorCorrectionLevel.L // Using Level L for maximum capacity
);

qrMatrix.encodeData(data);
qrMatrix.saveToFile(outputPath);
```

## Technical Details
- Supports QR versions 1-40
- Implements error correction levels L, M, Q, H
- Uses interleaved data encoding
- Includes detailed logging for debugging

## License
MIT License

Copyright (c) 2025 Lethusang

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.