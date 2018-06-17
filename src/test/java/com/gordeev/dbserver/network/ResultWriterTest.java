package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class ResultWriterTest {
    @Test
    public void write() throws FileNotFoundException {
        //prepare
        ResultWriter resultWriter = new ResultWriter(new FileOutputStream(new File("testdbtemplates/resulttoxmlSelect.xml")));
        Result result = new Result();
        result.setResultType(ResultType.RESULTSET);
        result.setMessage("all rows");
        ArrayList columns = new ArrayList();
        columns.add("id");
        columns.add("name");
        result.setColumns(columns);
        ArrayList row = new ArrayList();
        row.add("1");
        row.add("name1");
        ArrayList row2 = new ArrayList();
        row2.add("2");
        row2.add("name2");
        result.getRows().add(row);
        result.getRows().add(row2);
        //when
        resultWriter.write(result);

        //then - look in the file
    }
}