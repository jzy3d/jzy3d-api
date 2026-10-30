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
import java.util.Locale;

/**
 * Encoding format of a PLY file.
 */
enum Format {

    /** Data is stored in ASCII format. */
    ASCII,
    /** Data is stored in little endian binary format. */
    BINARY_LITTLE_ENDIAN,
    /** Data is stored in big endian binary format. */
    BINARY_BIG_ENDIAN;

    /**
     * Parses the header line defining the format.
     * @param formatLine Header line.
     * @return Format defined by it.
     * @throws IOException invalid format header line.
     */
    static Format parse(final String formatLine) throws IOException {
        if (!formatLine.startsWith("format ")) {
            throw new IOException("not a format definition: '"
                    + formatLine + "'");
        }
        String definition = formatLine.substring("format ".length());

        String[] parts = definition.split(" +", 2);
        if (parts.length != 2) {
            throw new IOException("Format definition must have two values.");
        }
        String format = parts[0];
        String version = parts[1];

        if (!version.equals("1.0")) {
            throw new IOException("Unsupported version of PLY: '"
                    + version + "'. Supported version is 1.0");
        }

        if (format.equals("ascii")) {
            return Format.ASCII;
        } else if (format.equals("binary_little_endian")) {
            return Format.BINARY_LITTLE_ENDIAN;
        } else if (format.equals("binary_big_endian")) {
            return Format.BINARY_BIG_ENDIAN;
        } else {
            throw new IOException("Unsupported format:"
                    + format + ".");
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase(Locale.US);
    }
}
