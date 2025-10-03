package dev.onat.onalps.utils;

import java.util.zip.CRC32;

public class Hasher {
    private Hasher() {}

    public static String hashIdCRC(String secretKey) {
        CRC32 crc = new CRC32();
        crc.update(secretKey.getBytes());
        long checksum = crc.getValue();
        return String.format("%08x", checksum);
    }
}
