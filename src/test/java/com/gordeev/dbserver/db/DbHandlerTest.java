package com.gordeev.dbserver.db;

import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.QueryType;
import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DbHandlerTest {

    @Test
    public void createDatabaseTest() {
        Query query = new Query();
        DbHandler dbHandler = new DbHandler();
        query.setType(QueryType.CREATEDATABASE);
        query.setDbName("testdb");
        Result result = dbHandler.executeQuery(query);

        assertEquals(ResultType.UPDATED, result.getResultType());

    }

    @Test
    public void createTableTest() {
        Query query = new Query();
        DbHandler dbHandler = new DbHandler();
        query.setType(QueryType.CREATEDATABASE);
        query.setDbName("testdb");
        dbHandler.executeQuery(query);

        Query queryTable = new Query();
        DbHandler dbHandlerTable = new DbHandler();
        queryTable.setType(QueryType.CREATETABLE);
        queryTable.setDbName("testdb");
        queryTable.setTableName("testtable");

        Map<String, String> columns = new HashMap<>();
        columns.put("id","int");
        columns.put("name","String");
        columns.put("hz","Object");
        queryTable.setColumns(columns);
        Result result = dbHandlerTable.executeQuery(queryTable);

        assertEquals(ResultType.UPDATED, result.getResultType());
    }

    @Test
    public void selectTest() {


    }
}