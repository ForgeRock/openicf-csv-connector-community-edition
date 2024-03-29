/*
 *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * Portions Copyrighted 2011 Viliam Repan (lazyman)
 *
 * $Id$
 */
package org.forgerock.openicf.csvfile;

import org.forgerock.openicf.csvfile.sync.Change;
import org.forgerock.openicf.csvfile.sync.InMemoryDiff;
import org.forgerock.openicf.csvfile.util.TestUtils;
import org.forgerock.openicf.csvfile.util.Utils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import static org.testng.Assert.assertEquals;

/**
 *
 * @author Viliam Repan (lazyman)
 */
public class InMemoryDiffTest {

    @Test
    public void test1() throws Exception {
        InMemoryDiff diff = initializeDiff(new File("./src/test/resources/diff/test1.csv"),
                new File("./src/test/resources/diff/test1.csv.tmp"));
        List<Change> changes = diff.diff();
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).getType(), Change.Type.CREATE);
    }

    @Test
    public void test2() throws Exception {
        InMemoryDiff diff = initializeDiff(new File("./src/test/resources/diff/test2.csv"),
                new File("./src/test/resources/diff/test2.csv.tmp"));
        List<Change> changes = diff.diff();
        assertEquals(changes.size(), 0);
    }

    @Test
    public void test3() throws Exception {
        InMemoryDiff diff = initializeDiff(new File("./src/test/resources/diff/test3.csv"),
                new File("./src/test/resources/diff/test3.csv.tmp"));
        List<Change> changes = diff.diff();
        assertEquals(changes.size(), 1);
        assertEquals(changes.get(0).getType(), Change.Type.CREATE);
    }

    private InMemoryDiff initializeDiff(File oldFile, File newFile) throws Exception {
        CSVFileConfiguration config = new CSVFileConfiguration();
        config.setEncoding("utf-8");
        //just to make connector initialization happy
        config.setFilePath(TestUtils.getTestFile("../../../src/test/resources/files/sync.csv"));
        config.setUniqueAttribute("id");
        config.setPasswordAttribute("password");

        CSVFileConnector connector = new CSVFileConnector();
        connector.init(config);

        return new InMemoryDiff(oldFile, newFile, connector.getLinePattern(), config);
    }

    @Test
    public void simpleTest() throws Exception {
        Utils.copyAndReplace(new File("./src/test/resources/files/sync.csv.1300734815289.backup"),
                new File("./target/test-classes/files/sync.csv.1300734815289"));

        CSVFileConfiguration config = new CSVFileConfiguration();
        config.setEncoding("utf-8");
        config.setFilePath(TestUtils.getTestFile("sync.csv"));
        config.setUniqueAttribute("uid");
        config.setPasswordAttribute("password");

        StringBuilder builder = new StringBuilder();
        builder.append("(?:^|");
        builder.append(config.getFieldDelimiter());
        builder.append(")(");
        builder.append(config.getValueQualifier());
        builder.append("(?:[^");
        builder.append(config.getValueQualifier());
        builder.append("]+|");
        builder.append(config.getValueQualifier());
        builder.append(config.getValueQualifier());
        builder.append(")*");
        builder.append(config.getValueQualifier());
        builder.append("|[^");
        builder.append(config.getFieldDelimiter());
        builder.append("]*)");
        Pattern pattern = Pattern.compile(builder.toString());
        InMemoryDiff diff = new InMemoryDiff(TestUtils.getTestFile("sync.csv.1300734815289"),
                config.getFilePath(), pattern, config);

        List<Change> changes = diff.diff();
        for (Change change : changes) {
//            System.out.println(change.getType() + "\t" + change.getUid());
        }
    }
}
