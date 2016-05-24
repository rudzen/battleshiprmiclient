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
import java.util.Arrays;
import java.util.Enumeration;
import ui.UIHelpers;

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
        
        //TESTING
        //out.write(getFirstNonLoopbackAddress(true, false).getAddress());
        //UIHelpers.messageDialog("Original signal : " + Arrays.toString(InetAddress.getLocalHost().getAddress()), "IP");
        //UIHelpers.messageDialog("Detected signal : " + getFirstNonLoopbackAddress(true, false).getHostAddress(), "First link-local IP");
        
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

    private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }
}
