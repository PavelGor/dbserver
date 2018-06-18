package com.gordeev.dbserver.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gordeev.dbserver.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class ResultWriter implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(ResultWriter.class);
    private final static XmlMapper xmlMapper = new XmlMapper();
    private OutputStream outputStream;

    public ResultWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(Result result) {
        try {
            xmlMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            xmlMapper.writeValue(outputStream,result);
            LOG.info("Result of query was send to user: {}", result);
        } catch (IOException e) {
            LOG.info("XmlMapper cannot write data to outputStream");
        }
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
