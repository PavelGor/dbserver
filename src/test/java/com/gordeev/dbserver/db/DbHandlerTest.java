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
    public void createDatabaseITest() {
        //prepare
        Query query = new Query();
        query.setType(QueryType.CREATE_DATABASE);
        query.setDbName("testdb");

        DbHandler dbHandler = new DbHandler();

        //when
        Result result = dbHandler.executeQuery(query);

        //then
        assertEquals(ResultType.UPDATED, result.getResultType());
        //check file.exist? and clean all

    }

    @Test
    public void createTableTest() {
        //prepare
        Query query = new Query();
        query.setType(QueryType.CREATE_DATABASE);
        query.setDbName("testdb");

        DbHandler dbHandler = new DbHandler();
        dbHandler.executeQuery(query);

        Query createTableQuery = new Query();
        createTableQuery.setType(QueryType.CREATE_TABLE);
        createTableQuery.setDbName("testdb");
        createTableQuery.setTableName("testtable");

        Map<String, String> columns = new HashMap<>();
        columns.put("id","int");
        columns.put("name","String");
        columns.put("salary","double");
        createTableQuery.setColumns(columns);

        //when
        Result result = dbHandler.executeQuery(createTableQuery);

        //then
        assertEquals(ResultType.UPDATED, result.getResultType());
    }

    @Test
    public void selectTest(){
        //prepare
        DbHandler dbHandler = new DbHandler();
        Query selectQuery = new Query();
        selectQuery.setType(QueryType.SELECT);
        selectQuery.setDbName("testdbtemplates");
        selectQuery.setTableName("testtable");

        Map<String, String> columns = new HashMap<>();
        columns.put("id","int");
        columns.put("name","String");
        columns.put("salary","double");
        selectQuery.setColumns(columns);

        //when
        Result result = dbHandler.executeQuery(selectQuery);

        //then
        assertEquals(ResultType.RESULTSET, result.getResultType());
        assertEquals("1", result.getRows().get(0).get(0));
        System.out.println(result);

    }
}