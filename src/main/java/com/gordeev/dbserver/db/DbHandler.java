package com.gordeev.dbserver.db;

import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.QueryType;
import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DbHandler {
    private Query query;
    private Result result;

    public Result executeQuery(Query query) {
        this.query = query;
        result = new Result();

        QueryType queryType = query.getType();

        if (queryType == QueryType.CREATEDATABASE) {
            return createDatabase();
        }

        if (queryType == QueryType.CREATETABLE) {
            return createTable();
        }

        if (queryType == QueryType.SELECT) {
            return select();
        }

        return result;
    }

    //not private is only for tests
    Result createDatabase() {
        File database = new File(query.getDbName());
        if (!database.exists()) {
            if (database.mkdir()) {
                result.setResultType(ResultType.UPDATED);
                result.setMessage("Database was created successfully");
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("Database already exist");
        }
        return result;
    }

    //not private is only for tests
    Result createTable() {
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (!table.exists() && !tableMetadata.exists()) {
            try {
                if (table.createNewFile() && tableMetadata.createNewFile()) {

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder build = factory.newDocumentBuilder();
                    Document doc = build.newDocument();

                    Element rootElement = doc.createElement("root");
                    rootElement.setAttribute("atr_name", "bla-bla-bla");

                    HashMap<String,String> columns = (HashMap<String, String>) query.getColumns();
                    for (Map.Entry<String, String> mapElement : columns.entrySet()) {
                        Element column = doc.createElement("column");
                        column.setAttribute("type",mapElement.getValue());
                        Text textNode = doc.createTextNode(mapElement.getKey());
                        column.appendChild(textNode);
                        rootElement.appendChild(column);
                    }

                    doc.appendChild(rootElement);

                    Transformer t = TransformerFactory.newInstance().newTransformer();
                    t.setOutputProperty(OutputKeys.INDENT, "yes");
                    t.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(tableMetadata)));

                    result.setResultType(ResultType.UPDATED);
                    result.setMessage("Table was created successfully");
                }
            } catch (IOException e) {
                e.printStackTrace();
                result.setResultType(ResultType.ERROR);
                result.setMessage("Something wrong with table creation");
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("Table already exist");
        }
        return result;
    }

    //not private is only for tests
    Result select() {
        return null;
    }
}
