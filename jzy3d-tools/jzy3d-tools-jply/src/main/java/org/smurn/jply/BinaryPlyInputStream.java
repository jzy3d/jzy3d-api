/*
 * Copyright 2011 Stefan C. Mueller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smurn.jply;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

/**
 * Stream to read binary PLY values.
 */
class BinaryPlyInputStream {

    /** Channel to read from. */
    private final ReadableByteChannel channel;
    /** Buffer used for reading. */
    private final ByteBuffer buffer;
    /** Size of the buffer in bytes. */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Creates an instance.
     * @param channel Channel to read from.
     * @param byteOrder Byte-order to use for reading.
     * @throws NullPointerException if {@code channel} or {@code byteOrder}
     * is {@code null}.
     */
    BinaryPlyInputStream(final ReadableByteChannel channel,
            final ByteOrder byteOrder) {
        if (channel == null) {
            throw new NullPointerException("channel must not be null.");
        }
        if (byteOrder == null) {
            throw new NullPointerException("byteOrder must not be null.");
        }
        this.channel = channel;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        this.buffer.order(byteOrder);
        this.buffer.clear();
        this.buffer.position(this.buffer.capacity());
    }

    /**
     * Reads a value from the stream.
     * @param type Encoding of the value in the binary stream.
     * @return Value casted to {@code double}.
     * @throws IOException if reading the value fails.
     * @throws NullPointerException if {@code type} is {@code null}.
     */
    public double read(final DataType type) throws IOException {
        if (type == null) {
            throw new NullPointerException("type must not be null.");
        }
        switch (type) {
            case CHAR:
                ensureAvailable(1);
                return buffer.get();
            case UCHAR:
                ensureAvailable(1);
                return ( (int) buffer.get() ) & 0x000000FF;
            case SHORT:
                ensureAvailable(2);
                return buffer.getShort();
            case USHORT:
                ensureAvailable(2);
                return ( (int) buffer.getShort() ) & 0x0000FFFF;
            case INT:
                ensureAvailable(4);
                return buffer.getInt();
            case UINT:
                ensureAvailable(4);
                return ( (long) buffer.getShort() ) & 0x00000000FFFFFFFF;
            case FLOAT:
                ensureAvailable(4);
                return buffer.getFloat();
            case DOUBLE:
                ensureAvailable(8);
                return buffer.getDouble();
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    /**
     * Ensures that a certain amount of bytes are in the buffer, ready
     * to be read.
     * @param bytes Minimal number of unread bytes required in the buffer. 
     * @see ByteBuffer#remaining()
     * @throws IOException if reading sufficient more data into the buffer
     * fails.
     */
    private void ensureAvailable(final int bytes) throws IOException {
        while (buffer.remaining() < bytes) {
            buffer.compact();
            if (channel.read(buffer) < 0) {
                throw new EOFException();
            }
            buffer.flip();
        }
    }
}
