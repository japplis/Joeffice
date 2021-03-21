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
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

// Delete after commit as it was my fault
/**
 * Demo of problem with updating row with h2 jdbc row set
 * org.h2.jdbc.JdbcSQLException: Data conversion error converting "'Hello world 7' (TEST_TABLE: ID INT NOT NULL)"; SQL statement:
UPDATE "PUBLIC"."TEST_TABLE" SET "ID"=? ,"NAME"=?  WHERE "ID"=? [22018-170]
	at org.h2.message.DbException.getJdbcSQLException(DbException.java:329)
	at org.h2.message.DbException.get(DbException.java:169)
	at org.h2.message.DbException.get(DbException.java:146)
	at org.h2.table.Column.convert(Column.java:147)
	at org.h2.command.dml.Update.update(Update.java:119)
	at org.h2.command.CommandContainer.update(CommandContainer.java:75)
	at org.h2.command.Command.executeUpdate(Command.java:230)
	at org.h2.jdbc.JdbcPreparedStatement.executeUpdateInternal(JdbcPreparedStatement.java:156)
	at org.h2.jdbc.JdbcPreparedStatement.executeUpdate(JdbcPreparedStatement.java:142)
	at org.h2.result.UpdatableRow.updateRow(UpdatableRow.java:284)
	at org.h2.jdbc.JdbcResultSet.updateRow(JdbcResultSet.java:2771)
	at com.sun.rowset.JdbcRowSetImpl.updateRow(JdbcRowSetImpl.java:3029)
 * @author Anthony Goubard - Japplis
 */
public class UpdateRowSet {

    public static void main(String[] args) throws SQLException {
        Connection conn = CreateH2Database.createSimpleDatabase("~/test", true, true);

        RowSetFactory rowSetFactory = RowSetProvider.newFactory();
        JdbcRowSet dataModel = rowSetFactory.createJdbcRowSet();
        dataModel.setUrl(conn.getMetaData().getURL());
        dataModel.setUsername(conn.getMetaData().getUserName());
        dataModel.setPassword("");

        dataModel.setCommand("select * from test_table");
        dataModel.execute();

        // now update the database
        dataModel.absolute(1);
        dataModel.setString(2, "Hello world 2");

        // This throws an exception but according to
        // http://docs.oracle.com/javase/tutorial/jdbc/basics/jdbcrowset.html#updating-column-value
        // it shouldn't
        try {
            dataModel.updateRow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        conn.close();
    }
}