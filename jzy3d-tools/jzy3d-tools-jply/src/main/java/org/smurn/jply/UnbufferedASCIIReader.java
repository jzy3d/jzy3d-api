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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * An unbuffered equivalent to {@link java.io.BufferedReader} and
 * {@link java.io.InputStreamReader}.
 * <p>This is quite slow in comparison, but it guarantees that no additional
 * bytes are read with is important when reading an ASCII header followed
 * by binary data. Since it is only used to read the PLY header the
 * performance is not relevant.</p>
 */
class UnbufferedASCIIReader extends Reader {

    private final PushbackInputStream stream;

    /**
     * Creates an instance.
     * @param stream Stream to read from.
     */
    public UnbufferedASCIIReader(final PushbackInputStream stream) {
        if (stream == null) {
            throw new NullPointerException();
        }
        this.stream = stream;
    }

    /**
     * Reads the next line.
     * @return The next line or {@code null} if the end of the stream is
     * reached.
     * @throws IOException if reading fails.
     */
    public String readLine() throws IOException {
        StringBuilder str = new StringBuilder();
        while (true) {
            int c = read();
            if (c < 0) {
                break;
            }
            if (c == '\n') {
                // if possible consume another '\r'
                int peek = stream.read();
                if (peek != '\r' && peek >= 0){
                    stream.unread(peek);
                }
                break;
            }
            if (c == '\r') {
                // if possible consume another '\n'
                int peek = stream.read();
                if (peek != '\n' && peek >= 0){
                    stream.unread(peek);
                }
                break;
            }
            str.append((char) c);
        }
        return str.toString();
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(final char[] cbuf, final int off,
            final int len) throws IOException {
        byte[] bbuf = new byte[len];
        int bytesRead = stream.read(bbuf);
        for (int i = 0; i < bytesRead; i++) {
            cbuf[off + i] = (char) bbuf[i];
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
