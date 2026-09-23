package com.kabadiwala;

import com.kabadiwala.repository.RecyclingRecordRepository;
import com.kabadiwala.repository.TraceabilityRecordRepository;
import com.kabadiwala.repository.TransactionRepository;
import com.kabadiwala.service.QRService;
import com.kabadiwala.service.RecyclingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class QRServiceTest {

    private QRService qrService;

    @BeforeEach
    void setUp() {
        TraceabilityRecordRepository traceabilityRecordRepository = Mockito.mock(TraceabilityRecordRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        RecyclingRecordRepository recyclingRecordRepository = Mockito.mock(RecyclingRecordRepository.class);
        RecyclingService recyclingService = Mockito.mock(RecyclingService.class);

        qrService = new QRService(
                traceabilityRecordRepository,
                transactionRepository,
                recyclingRecordRepository,
                recyclingService
        );
    }

    @Test
    @DisplayName("Should generate valid non-empty PNG barcode byte array using ZXing")
    void shouldGenerateValidQRCodeImageBytes() {
        byte[] bytes = qrService.generateQRCodeImage("https://kabadiwala.com/trace/QR-TEST-1234", 200, 200);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        // PNG magic header bytes: 0x89, 0x50, 0x4E, 0x47
        assertEquals((byte) 0x89, bytes[0]);
        assertEquals((byte) 0x50, bytes[1]);
        assertEquals((byte) 0x4E, bytes[2]);
        assertEquals((byte) 0x47, bytes[3]);
    }

    @Test
    @DisplayName("Should generate valid Base64 data URL for direct embedding")
    void shouldGenerateValidBase64DataUrl() {
        String base64Url = qrService.generateBase64QR("QR-WASTE-TEST", 200, 200);

        assertNotNull(base64Url);
        assertTrue(base64Url.startsWith("data:image/png;base64,"));
        assertTrue(base64Url.length() > 50);
    }
}
