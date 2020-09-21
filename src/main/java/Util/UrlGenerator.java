package Util;

import db.H2Milestone;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

// Class to generate Unique URLS
public class UrlGenerator {

    //  Encoder
    private final Encoder enc;

    //  H2Milestone
    private final H2Milestone h2Milestone;

    //  Constructor
    public UrlGenerator(H2Milestone h2Milestone) {
        this.enc = Base64.getUrlEncoder();
        this.h2Milestone = h2Milestone;
    }

    // Returns a random base64 string, length specified
    // Recursion in case duplicates generated, however very unlikely
    public String rndUrl(int length) {
        // Random UUID
        UUID uuid = UUID.randomUUID();
        byte[] byte_src = ByteBuffer.wrap(new byte[16]).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).array();
        String enc_uuid = enc.encodeToString(byte_src).substring(0, length);

        // if the url isn't unique, recall function until a unique one is found
        return h2Milestone.checkUrl(enc_uuid) ? rndUrl(length) :  enc_uuid;
    }

}
