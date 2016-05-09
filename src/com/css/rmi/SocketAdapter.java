/*
 * Copyright 2000 Computer System Services, Inc.
 *
 * Permission to use this software for any purpose is granted provided that
 * this copyright notice is preserved.
 *
 * This software is provided as-is and without warranty as to its
 * fitness for any purpose.  In other words, Computer System Services,
 * Inc. does not guarantee that this software works.  It is provided
 * only in the hope that it may be found useful by someone.
 *
 * Please e-mail tttaylor@cssassociates.com if you find any errors
 * or want to request changes/enhancements.
 */
package com.css.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Adapts two sockets by sending the data from the output stream of one to the
 * input stream of the other and vice versa.
 *
 * @author Tim Taylor -- tttaylor@cssassociates.com
 */
public class SocketAdapter {

    Socket socket1;
    Socket socket2;

    private class StreamThread extends Thread {

        InputStream in;
        OutputStream out;

        StreamThread(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    int val = in.read();
                    out.write(val);
                    out.flush();
                    if (val == -1) {
                        out.close();
                        in.close();
                        return;
                    }
                }
            } catch (IOException e) {
            }
        }
    }

    public SocketAdapter(Socket socket1, Socket socket2) throws IOException {
        new StreamThread(socket1.getInputStream(), socket2.getOutputStream()).start();
        new StreamThread(socket2.getInputStream(), socket1.getOutputStream()).start();
    }

    public void close() {
    }
}
