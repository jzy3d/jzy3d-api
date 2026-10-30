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

/**
 * jPLY - Java PLY File Format Library.
 * <p>jPLY is a library to read PLY files in a simple way. It has <b>complete
 * support for all features</b> offered by the PLY format. jPLY is using the
 * <b>Apache 2 License</b> so feel free to use it for open or closed source
 * projects.</p>
 * <p>This JavaDoc documentation is containing the detailed description of the
 * API. A quick-start manual for this library is provided though the maven site
 * of this library.</p>
 * <p>Data is read using two interfaces. The first one is {@link PlyReader}
 * which defines {@link PlyReader#nextElementReader()} that returns
 * one {@link ElementReader} after the other. The element readers then allow
 * the user to read the actual elements using
 * {@link ElementReader#readElement()}.</p>
 * <p>The data is extracted from the {@link Element}s using the getter methods
 * provided by it.</p>
 * <p>It is also possible to inspect the element types and element counts
 * using both the {@link PlyReader} and {@link ElementReader} interfaces.
 * This can be done before actually reading any elements.</p>
 * <p>The remaining classes serve this purpose by modeling the element
 * types.</p>
 * <p>The normalizer is separated in the {@code org.smurn.jply.util} package
 * since the normalizer is only a helper and not necessarily required to
 * read a PLY file.</p>
 */
package org.smurn.jply;