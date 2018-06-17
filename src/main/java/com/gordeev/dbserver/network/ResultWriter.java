package com.gordeev.dbserver.network;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gordeev.dbserver.entity.Result;

import java.io.IOException;
import java.io.OutputStream;

public class ResultWriter implements AutoCloseable {
    private OutputStream outputStream;
    private final static XmlMapper xmlMapper = new XmlMapper();

    public ResultWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(Result result) {
        try {
            xmlMapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            xmlMapper.writeValue(outputStream,result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        outputStream.close();
    }
}
