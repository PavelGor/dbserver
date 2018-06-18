package com.gordeev.dbserver.db;

import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.QueryType;
import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DbHandler {
    private final static DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
    private final static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private final static Logger LOG = LoggerFactory.getLogger(DbHandler.class);

    public synchronized Result executeQuery(Query query) {

        QueryType queryType = query.getType();

        if (queryType == QueryType.CREATE_DATABASE) {
            return createDatabase(query);
        }

        if (queryType == QueryType.CREATE_TABLE) {
            return createTable(query);
        }

        if (queryType == QueryType.SELECT) {
            return select(query);
        }

        if (queryType == QueryType.DELETE) {
            return delete(query);
        }

        if (queryType == QueryType.UPDATE) {
            return update(query);
        }

        if (queryType == QueryType.INSERT) {
            return insert(query);
        }

        return new Result(ResultType.ERROR, "Type of Query is not allowed here (Allowed: SELECT, CREATE DATABASE, CREATE TABLE, UPDATE, DELETE)");
    }

    private Result createDatabase(Query query) {
        Result result = new Result();
        File database = new File(query.getDbName());
        if (!database.exists()) {
            if (database.mkdir()) {
                result.setResultType(ResultType.UPDATED);
                result.setMessage("Database " + query.getDbName() + " was created successfully");
                LOG.info("Database was created successfully: {}", query.getDbName());
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("Database already exist");
            LOG.info("Database already exist: {}", query.getDbName());
        }
        return result;
    }

    private Result createTable(Query query) {
        Result result = new Result();
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (!table.exists() && !tableMetadata.exists()) {
            try {
                if (table.createNewFile() && tableMetadata.createNewFile()) {

                    DocumentBuilder build = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                    Document doc = build.newDocument();

                    Element rootElement = doc.createElement(query.getTableName());
                    Element columnsElement = doc.createElement("columns");
                    rootElement.appendChild(columnsElement);

                    Map<String, String> columns = query.getColumns();
                    for (Map.Entry<String, String> mapElement : columns.entrySet()) {
                        Element column = doc.createElement("column");
                        column.setAttribute("type", mapElement.getValue());
                        Text textNode = doc.createTextNode(mapElement.getKey());
                        column.appendChild(textNode);
                        columnsElement.appendChild(column);
                    }
                    doc.appendChild(rootElement);

                    saveXmlToFile_Db(doc, tableMetadata);

                    result.setResultType(ResultType.UPDATED);
                    result.setMessage("Table" + query.getTableName() + " was created successfully");
                    LOG.info("Table was created successfully: {}", query.getTableName());
                }
            } catch (TransformerException | IOException | ParserConfigurationException e) {
                result.setResultType(ResultType.ERROR);
                result.setMessage("Cannot create table: " + query.getTableName());
                LOG.info("Cannot create table: {}", query.getTableName());
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("Table already exist");
            LOG.info("Table already exist: {}", query.getTableName());
        }
        return result;
    }

    private Result select(Query query) {
        Result result = new Result();
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (table.exists() && tableMetadata.exists()) {
            try {
                result = readAndValidateMetadata(query, tableMetadata);
                result = readData(result, table);
                LOG.info("Data was read by SELECT query to object: {}", result);
            } catch (ParserConfigurationException | IOException | SAXException e) {
                result.setResultType(ResultType.ERROR);
                result.setMessage("Something wrong with tables");
                LOG.info("Cannot read data by SELECT query: {}", e);
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("Bad query request");
            LOG.info("Table do not exist: {}", query.getTableName());
            return result;
        }
        result.setResultType(ResultType.RESULTSET);
        result.setMessage("look on data");
        return result;
    }

    private Result delete(Query query) {
        Result result = new Result();
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (table.exists() && tableMetadata.exists()) {
            try {
                //check columns between query and table
                DocumentBuilder buildMeta = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docMeta = buildMeta.parse(tableMetadata);
                NodeList listOfColumns = docMeta.getElementsByTagName("column");
                boolean exist = false;
                for (int i = 0; i < listOfColumns.getLength(); i++) {
                    Node column = listOfColumns.item(i);
                    if (column.getNodeType() == Node.ELEMENT_NODE) {
                        String columnName = column.getTextContent();
                        if (query.getConditions().containsKey(columnName))
                            exist = true;
                    }
                }
                if (!exist) {
                    result.setResultType(ResultType.ERROR);
                    result.setMessage("Table have no such columns");
                    LOG.info("DELETE: Table have no such columns");
                    return result;
                }

                //do action
                DocumentBuilder buildData = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docData = buildData.parse(table);

                NodeList listOfRows = docData.getElementsByTagName("value");

                for (int i = 0; i < listOfRows.getLength(); i++) {
                    NodeList listCellsInRows = listOfRows.item(i).getChildNodes();
                    for (int j = 0; j < listCellsInRows.getLength(); j++) {
                        Node cell = listCellsInRows.item(j);
                        if (cell.getNodeType() == Node.ELEMENT_NODE) {
                            if (query.getConditions().containsKey(cell.getNodeName())
                                    && query.getConditions().containsValue(cell.getTextContent())) {
                                listOfRows.item(i).getParentNode().removeChild(listOfRows.item(i));
                            }
                        }
                    }
                }

                saveXmlToFile_Db(docData, table);

                result.setResultType(ResultType.UPDATED);
                result.setMessage("Table was updated successfully");
                LOG.info("DELETE: Table was updated successfully");
            } catch (TransformerException | IOException | SAXException | ParserConfigurationException e) {
                result.setResultType(ResultType.ERROR);
                result.setMessage("Something wrong with deleting data");
                LOG.info("DELETE: Something wrong with deleting data");
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("server have no such table/database");
            LOG.info("DELETE: server have no such table/database");
        }
        return result;
    }

    private Result update(Query query) {
        Result result = new Result();
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (table.exists() && tableMetadata.exists()) {
            try {
                //check columns between query and table
                DocumentBuilder buildMeta = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docMeta = buildMeta.parse(tableMetadata);
                NodeList listOfColumns = docMeta.getElementsByTagName("column");
                boolean exist = false;
                for (int i = 0; i < listOfColumns.getLength(); i++) {
                    Node column = listOfColumns.item(i);
                    if (column.getNodeType() == Node.ELEMENT_NODE) {
                        String columnName = column.getTextContent();
                        if (query.getConditions().containsKey(columnName))
                            exist = true;
                    }
                }
                if (!exist) {
                    result.setResultType(ResultType.ERROR);
                    result.setMessage("Table have no such columns");
                    LOG.info("UPDATE: Table have no such columns");
                    return result;
                }

                //do action
                DocumentBuilder buildData = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docData = buildData.parse(table);

                NodeList listOfRows = docData.getElementsByTagName("value");

                for (int i = 0; i < listOfRows.getLength(); i++) {
                    NodeList listCellsInRows = listOfRows.item(i).getChildNodes();
                    for (int j = 0; j < listCellsInRows.getLength(); j++) {
                        Node cell = listCellsInRows.item(j);
                        if (cell.getNodeType() == Node.ELEMENT_NODE) {
                            if (query.getConditions().containsKey(cell.getNodeName())
                                    && query.getConditions().containsValue(cell.getTextContent())) {
                                Node row = cell.getParentNode();
                                NodeList updateCells = row.getChildNodes();
                                for (int k = 0; k < updateCells.getLength(); k++) {
                                    if (updateCells.item(k).getNodeType() == Node.ELEMENT_NODE) {
                                        String columnName = updateCells.item(k).getNodeName();
                                        String cellValue = query.getValues().get(columnName);
                                        updateCells.item(k).setTextContent(cellValue);
                                    }
                                }
                            }
                        }
                    }
                }

                saveXmlToFile_Db(docData, table);

                result.setResultType(ResultType.UPDATED);
                result.setMessage("Table was updated successfully");
                LOG.info("UPDATE: Table was updated successfully");
            } catch (TransformerException | IOException | SAXException | ParserConfigurationException e) {
                result.setResultType(ResultType.ERROR);
                result.setMessage("Something wrong with table updation");
                LOG.info("UPDATE: Something wrong with table updation");
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("server have no such table/database");
            LOG.info("UPDATE: server have no such table/database");
        }
        return result;
    }

    private Result insert(Query query) {
        Result result = new Result();
        File table = new File(query.getDbName(), query.getTableName() + ".xml");
        File tableMetadata = new File(query.getDbName(), query.getTableName() + "Metadata.xml");
        if (table.exists() && tableMetadata.exists()) {
            try {
                //check columns between query and table
                DocumentBuilder buildMeta = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docMeta = buildMeta.parse(tableMetadata);
                NodeList listOfColumns = docMeta.getElementsByTagName("column");
                boolean exist = false;
                for (int i = 0; i < listOfColumns.getLength(); i++) {
                    Node column = listOfColumns.item(i);
                    if (column.getNodeType() == Node.ELEMENT_NODE) {
                        String columnName = column.getTextContent();
                        if (query.getColumns().containsKey(columnName))
                            exist = true;
                    }
                }
                if (!exist) {
                    result.setResultType(ResultType.ERROR);
                    result.setMessage("Table have no such columns");
                    LOG.info("INSERT: Table have no such columns");
                    return result;
                }

                //do action
                DocumentBuilder buildData = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
                Document docData = buildData.parse(table);

                Node insertRowNode = docData.createElement("value");
                for (int i = 0; i < listOfColumns.getLength(); i++) {
                    Node column = listOfColumns.item(i);
                    if (column.getNodeType() == Node.ELEMENT_NODE) {
                        Element entry = docData.createElement(column.getTextContent());
                        Text textNode = docData.createTextNode(query.getValues().get(column.getTextContent()));
                        entry.appendChild(textNode);
                        insertRowNode.appendChild(entry);
                    }
                }

                docData.getFirstChild().appendChild(insertRowNode); //TODO: insert Node down 1 level

                saveXmlToFile_Db(docData, table);

                result.setResultType(ResultType.UPDATED);
                result.setMessage("Table was updated successfully");
                LOG.info("INSERT: Table was updated successfully");
            } catch (TransformerException | IOException | SAXException | ParserConfigurationException e) {
                result.setResultType(ResultType.ERROR);
                result.setMessage("Something wrong with table updation");
                LOG.info("INSERT: Something wrong with table updation");
            }
        } else {
            result.setResultType(ResultType.ERROR);
            result.setMessage("server have no such table/database");
            LOG.info("INSERT: server have no such table/database");
        }
        return result;
    }

    private Result readAndValidateMetadata(Query query, File tableMetadata) throws ParserConfigurationException, IOException, SAXException {
        Result result = new Result();
        DocumentBuilder build = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        Document doc = build.parse(tableMetadata);

        NodeList listOfColumns = doc.getElementsByTagName("column");
        for (int i = 0; i < listOfColumns.getLength(); i++) {
            Node column = listOfColumns.item(i);
            if (column.getNodeType() == Node.ELEMENT_NODE) {
                String columnName = column.getTextContent(); //TODO: берет из файла значения не по порядку - как обеспечить? а то названия столбцов не будут сооветствовать значениям!!!
                if (query.getColumns().containsKey(columnName))
                    result.getColumns().add(columnName);
            }
        }

        return result;
    }

    private Result readData(Result result, File table) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder build = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
        Document doc = build.parse(table);

        NodeList listOfRows = doc.getElementsByTagName("value");

        for (int i = 0; i < listOfRows.getLength(); i++) {
            NodeList listCellsInRows = listOfRows.item(i).getChildNodes();
            int index = 0;
            List row = new ArrayList<>();
            for (int j = 0; j < listCellsInRows.getLength(); j++) {
                Node cell = listCellsInRows.item(j);
                if (cell.getNodeType() == Node.ELEMENT_NODE) {
                    if (result.getColumns().contains(cell.getNodeName())) {
                        row.add(index, cell.getTextContent());
                        index++;
                    }
                }
            }
            result.getRows().add(row);
        }
        return result;
    }

    private void saveXmlToFile_Db(Document doc, File file) throws TransformerException, IOException {
        Transformer t = TRANSFORMER_FACTORY.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        t.transform(new DOMSource(doc), new StreamResult(fileOutputStream));
        fileOutputStream.close();
    }
}
