/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress2.archivers.arj;

import org.apache.commons.compress2.archivers.arj.MainHeader;
import org.apache.commons.compress2.archivers.arj.LocalFileHeader;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.compress2.archivers.arj.ArjArchiveEntry.HostOs;
import org.junit.Test;

public class CoverageTest {

    @Test
    public void testHostOsInstance() {
        HostOs hostOs = new HostOs();
        assertNotNull(hostOs);
    }
    @Test
    public void testHeaderInstances() {
        assertNotNull(new LocalFileHeader.FileTypes());
        assertNotNull(new LocalFileHeader.Methods());
        assertNotNull(new LocalFileHeader.Flags());
        assertNotNull(new MainHeader.Flags());
    }
    @Test
    public void testCallLFHToString() {
        LocalFileHeader lfh = new LocalFileHeader();
        assertNotNull(lfh.toString());
    }

}
