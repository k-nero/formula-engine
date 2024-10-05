package com.force.formula.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DedicatedPostgresTester extends AbstractDbTester
{

    public DedicatedPostgresTester() throws IOException
    {
    }

    @Override
    public String getDbTypeName() {
        return "postgres";
    }

    @Override
    protected Connection getConnection() throws SQLException, IOException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "1234";
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    protected String getDecimalType() {
        return "numeric";
    }

    @Override
    protected String getTextType() {
        return "text";
    }

    @Override
    protected String getTimestampType() {
        return "timestamp";
    }

    @Override
    protected String stringToDateTime(String arg) {
        return arg + "::timestamp";
    }

    @Override
    protected String convertToDateTime(String arg) {
        return arg + "::timestamp";
    }

    @Override
    public void close() throws Exception {
        // It autocloses.
    }

}
