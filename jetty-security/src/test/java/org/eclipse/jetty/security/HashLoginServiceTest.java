//
//  ========================================================================
//  Copyright (c) 1995-2018 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.security;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashLoginServiceTest
{
    private final String testFileDir = "target" + File.separator + "property-user-store-test";
    private final String testFile = "users.txt";
    private Path testFilePath;

    @Before
    public void before() throws Exception
    {
        final Path testFileDirPath = Paths.get(testFileDir);
        Files.createDirectories(testFileDirPath);
        testFilePath = testFileDirPath.resolve(testFile).toAbsolutePath();
        
        writeInitialUsers(testFilePath);
    }

    @After
    public void after() throws Exception
    {
        Files.delete(testFilePath);
    }

    private void writeInitialUsers(Path testFilePath) throws Exception
    {
        try (Writer writer = new BufferedWriter(new FileWriter(testFilePath.toFile())))
        {
            writer.append("tom: tom, roleA\n");
            writer.append("dick: dick, roleB\n");
            writer.append("harry: harry, roleA, roleB\n");
        }
    }

    @Test
    public void testHashLoginServiceLoadFromSimplePath() throws Exception
    {
        testHashLoginService(testFilePath.normalize().toString());
    }

    @Test
    public void testHashLoginServiceLoadFromUri() throws Exception
    {
        testHashLoginService(testFilePath.toUri().toString());
    }

    @Test
    public void testHashLoginServiceLoadFromUrl() throws Exception
    {
        testHashLoginService(testFilePath.toUri().toURL().toString());
    }

    protected void testHashLoginService(String configPath) throws Exception
    {
        final HashLoginService hashLoginService = new HashLoginService("hashLoginService");
        System.err.println("config path string: " + configPath);
        hashLoginService.setConfig(configPath);

        try
        {
            hashLoginService.start();
            assertEquals("Unexpected user count", 3, hashLoginService.getUsers().size());
            assertNotNull("Can't find user dick", hashLoginService.getUsers().get("dick"));
        }
        finally
        {
            hashLoginService.stop();
        }
    }
}
