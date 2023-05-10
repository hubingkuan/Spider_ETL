package com.yeshen.appcenter.config.web.filter;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class RepeatReadRequest extends HttpServletRequestWrapper {
    private ByteArrayOutputStream out;

    public RepeatReadRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (out == null) {
            out = new ByteArrayOutputStream();
            IOUtils.copy(super.getInputStream(), out);
        }
        return new CustomerServletInputStream(out.toByteArray());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    private static class CustomerServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream in;

        public CustomerServletInputStream(byte[] data) {
            this.in = new ByteArrayInputStream(data);
        }

        @Override
        public boolean isFinished() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }
    }

    public ByteArrayOutputStream getOut() {
        return out;
    }
}