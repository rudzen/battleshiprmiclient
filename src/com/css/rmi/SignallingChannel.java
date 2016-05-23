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

import java.io.*;
import java.net.*;

/**
 * Provides a signalling channel used to set up callback sockets between the
 * client and server.
 *
 * @author Tim Taylor -- tttaylor@cssassociates.com
 */
public class SignallingChannel extends Thread {

    private final String directAddress;
    private final int directPort;

    private final String destinationAddress;
    private final int destinationPort;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public SignallingChannel(final String directAddress,
            final int directPort) throws IOException {
        super();
        this.directAddress = directAddress;
        this.directPort = directPort;
        socket = new Socket(directAddress, directPort);

        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        // Register with server
        out.writeInt(TwoWay.PROTOCOL_MAGIC);
        out.writeInt(TwoWay.REGISTER_CALLBACK_SOCKET_SOURCE);
        out.write(InetAddress.getLocalHost().getAddress());
        out.flush();

        // Get back server endpoint info
        int opcode = in.readInt();
        byte[] address = new byte[4];
        in.read(address, 0, 4);
        destinationAddress = TwoWay.getAddressString(address);
        destinationPort = in.readInt();
    }

    public String getDirectAddress() {
        return directAddress;
    }

    public int getDirectPort() {
        return directPort;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    @Override
    public void run() {
        try {
            for (;;) {
                int magic = in.readInt();
                int opcode = in.readInt();
                int port = in.readInt();

                byte[] localAddress = InetAddress.getLocalHost().getAddress();

                Socket localSocket = null;

                try {
                    localSocket
                            = new Socket(TwoWay.getAddressString(localAddress),
                                    port);
                } catch (IOException e) {
                    // Usually means client terminating while server
                    // requesting callback socket.
                }

                Socket remoteSocket = new Socket(directAddress, directPort);
                new SocketAdapter(localSocket, remoteSocket);

                DataOutputStream remoteOut = new DataOutputStream(remoteSocket.getOutputStream());

                remoteOut.writeInt(TwoWay.PROTOCOL_MAGIC);
                remoteOut.writeInt(TwoWay.RETURN_CALLBACK_SOCKET);
                remoteOut.write(localAddress);
                remoteOut.writeInt(port);
                remoteOut.flush();
            }
        } catch (IOException e) {
            
        }
    }
}
