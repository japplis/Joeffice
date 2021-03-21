/*
 * Copyright 2013 Japplis.
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
package org.joeffice.tools;

import java.io.File;
import java.sql.*;

/**
 * Creates an empty h2 database.
 *
 * C:\Java\projects\Joeffice\tools\src>javac -classpath C:\Java\projects\Joeffice\admin\libs\h2\bin\h2-1.3.170.jar org\joeffice\tools\CreateH2Database.java
 * java -cp .;C:\Java\projects\Joeffice\admin\libs\h2\bin\h2-1.3.170.jar org.joeffice.tools.CreateH2Database
 *
 * @author Anthony Goubard - Japplis
 */
public class CreateH2Database {

    public CreateH2Database() throws SQLException, ClassNotFoundException {
    }

    public void createTest1() throws SQLException {
        String test1Path = "C:/Java/projects/Joeffice/tools/tests/test1";
        createSimpleDatabase(test1Path, false, false);
    }

    public void createTest2() throws SQLException {
        String test2Path = "C:/Java/projects/Joeffice/tools/tests/test2";
        createSimpleDatabase(test2Path, true, true);
    }

    public void createTest3() throws SQLException {
        String test3Path = "C:/Java/projects/Joeffice/tools/tests/Database - 500 items";
        Connection conn = createSimpleDatabase(test3Path, true, true);
        for (int i = 0; i < 500; i++) {
            PreparedStatement insertValue = conn.prepareStatement("INSERT INTO TEST_TABLE VALUES(" + i + ", 'Hello World " + i + "');");
            insertValue.execute();
            insertValue.close();
        }
        PreparedStatement createTable = conn.prepareStatement("CREATE TABLE TEST_TABLE2 (ID INT PRIMARY KEY, SOME_NUMBER DECIMAL);");
        createTable.execute();
        createTable.close();
    }

    public static Connection createSimpleDatabase(String path, boolean withTable, boolean withData) throws SQLException {
        if (!new File(path + ".h2.db").exists()) {
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException ex) {
                throw new SQLException("Driver not found");
            }
            Connection conn = DriverManager.getConnection("jdbc:h2:" + path, "sa", "");
            PreparedStatement createTable = conn.prepareStatement("CREATE TABLE TEST_TABLE(ID INT PRIMARY KEY, NAME VARCHAR);");
            createTable.execute();
            createTable.close();
            if (withData) {
                PreparedStatement insertValue = conn.prepareStatement("INSERT INTO TEST_TABLE VALUES(1, 'Hello World');");
                insertValue.execute();
                insertValue.close();
            }
            return conn;
        } else {
            Connection conn = DriverManager.getConnection("jdbc:h2:" + path, "sa", "");
            return conn;
        }
    }

    public final static void main(String[] args) throws SQLException, ClassNotFoundException {
        new CreateH2Database();
    }
}