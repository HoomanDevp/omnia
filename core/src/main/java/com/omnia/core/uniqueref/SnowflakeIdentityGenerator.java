package com.omnia.core.uniqueref;

import com.omnia.core.converter.ByteConverter;
import com.omnia.core.converter.HexConverter;
import com.omnia.log.LogSpec;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class SnowflakeIdentityGenerator implements ISnowflakeIdentityGenerator {

    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 41; // Unused in algorithm
    private static final int NODE_ID_BITS = 31;
    private static final int SEQUENCE_BITS = 10;
    private static final int NODE_ID_SHIFT = SEQUENCE_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + NODE_ID_BITS;

    private static final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Custom Epoch (January 1, 2025, Midnight UTC = 2025-01-01T00:00:00Z) = 1_735_689_600_000L
    private static final long CUSTOM_EPOCH = 1_635_689_600_000L;

    private final long nodeId;

    private final AtomicReference<State> state = new AtomicReference<>();


    protected SnowflakeIdentityGenerator() {

        this.nodeId = this.createNodeId();
    }

    @Override
    public String generateId() {
        while (true) {
            long currentTimestamp = System.currentTimeMillis();

            if (currentTimestamp < CUSTOM_EPOCH) {
                throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
            }

            State currentState = state.get();

            long la = currentState == null ? -1L : currentState.lastTimestamp;
            long seq = currentState == null ? 0L : currentState.sequence;

            if (currentTimestamp < la) {
                throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
            }

            long newSequence;
            long newTimestamp = currentTimestamp;

            if (currentTimestamp == la) {
                newSequence = (seq + 1) & MAX_SEQUENCE;
                if (newSequence == 0) {
                    newTimestamp = this.waitNextMillis(currentTimestamp, currentState.lastTimestamp);
                }
            } else {
                newSequence = 0L;
            }

            State newState = new State();
            newState.lastTimestamp = newTimestamp;
            newState.sequence = newSequence;

            if (state.compareAndSet(currentState, newState)) {
                BigInteger uniqueId = BigInteger.valueOf(newTimestamp - CUSTOM_EPOCH).shiftLeft(TIMESTAMP_SHIFT)
                        .or(BigInteger.valueOf(nodeId).shiftLeft(NODE_ID_SHIFT))
                        .or(BigInteger.valueOf(newSequence));

                return ByteConverter.from(uniqueId.toByteArray()).toHex();
            }

            // CAS failed, retry
        }
    }

    public abstract String latestId();

    @Override
    public long[] parse(String hexId) {

        long maskNodeId = ((1L << NODE_ID_BITS) - 1) << SEQUENCE_BITS;
        long maskSequence = (1L << SEQUENCE_BITS) - 1;

        BigInteger id = HexConverter.from(hexId).toBigInteger();

        BigInteger timestampPart = id.shiftRight(TIMESTAMP_SHIFT);
        long timestamp = timestampPart.longValue() + CUSTOM_EPOCH;

        BigInteger nodeIdPart = id.and(BigInteger.valueOf(maskNodeId)).shiftRight(SEQUENCE_BITS);
        long nodeId = nodeIdPart.longValue();

        BigInteger sequencePart = id.and(BigInteger.valueOf(maskSequence));
        long sequence = sequencePart.longValue();

        return new long[]{timestamp, nodeId, sequence};
    }

    //######################################################################################################### //
    //region PRIVATE-METHOD-AREA => You Must Put All Private Methods Here And Call Them With 'this' phrase from Public Area

    private long waitNextMillis(long currentTimestamp, long lastTimestamp) {

        while (currentTimestamp <= lastTimestamp)
            currentTimestamp = System.currentTimeMillis();

        return currentTimestamp;
    }

    private long createNodeId() {

        Long nodeId = null;
        try {

            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {

                    byte[] mac = networkInterface.getHardwareAddress();
                    if (mac != null) {
                        for (byte macPort : mac)
                            sb.append(String.format("%02X", macPort));

                        nodeId = Math.abs(Long.valueOf(sb.toString().hashCode()));
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            log.error(LogSpec.ofException(ex.getMessage(), ex).toString());
        }
        if (nodeId == null)
            nodeId = new SecureRandom().nextLong(MAX_NODE_ID);

        nodeId = nodeId & MAX_NODE_ID;
        return nodeId;
    }

    //endregion
    //######################################################################################################### //

    private static class State {
        private long lastTimestamp = -1L;
        private long sequence = 0L;
    }

}