package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.QueryType;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class QueryReaderTest {

    @Test
    public void readQuery() {
        //prepare
        String testQuery = "{\n" +
                "  \"type\" : \"CREATE_TABLE\",\n" +
                "  \"dbName\" : \"test2db\",\n" +
                "  \"tableName\" : \"test2table\",\n" +
                "  \"columns\" :\n" +
                "    {\n" +
                "    \"id\":\"int\",\n" +
                "    \"firstName\":\"String\",\n" +
                "    \"lastName\":\"String\",\n" +
                "    \"salary\":\"double\"\n" +
                "    }\n" +
                "}";
        QueryReader queryReader = new QueryReader(new ByteArrayInputStream(testQuery.getBytes()));

        //when
        Query query = queryReader.readQuery();

        //then
        assertEquals(QueryType.CREATE_TABLE, query.getType());
        assertEquals("test2db", query.getDbName());
        assertEquals("test2table", query.getTableName());
    }
}