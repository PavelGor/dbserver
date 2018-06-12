package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Result;

import javax.xml.stream.XMLOutputFactory;
import java.io.OutputStream;

public class ResultWriter {
    private OutputStream outputStream;
    public ResultWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(Result result) {

    }
}
