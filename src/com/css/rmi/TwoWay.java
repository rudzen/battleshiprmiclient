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

/**
 * Constants and utilities for the tunneling protocol.
 *
 * @author Tim Taylor -- tttaylor@cssassociates.com
 */
public class TwoWay {

    /**
     * Opcodes for protocol.
     */
    /**
     * Header for all messages in the protocol
     */
    public static final int PROTOCOL_MAGIC = 23861;

    /**
     * Sent by client to register the signalling channel socket with the server.
     */
    public static final int REGISTER_CALLBACK_SOCKET_SOURCE = 0;

    /**
     * Sent by the client to tell the server that the socket is a callback
     * socket for the server's use in calling client callbacks.
     */
    public static final int RETURN_CALLBACK_SOCKET = 1;

    /**
     * Sent by the server to the client to request that the client make a
     * callback socket for the server's use.
     */
    public static final int REQUEST_CALLBACK_SOCKET = 2;

    /**
     * Sent by the server to the client to inform the client of the server
     * address. This is the actually address from the server's viewpoint and
     * will be different from the address used to connect to the server if
     * tunneling through a firewall.
     */
    public static final int RETURN_SERVER_ENDPOINT_INFO = 3;

    /**
     * Maximum length of messages in the protocol.
     */
    public static final int MAX_MESSAGE_LENGTH = 50;

    /**
     * String names of opcodes. Used for debugging.
     */
    private static final String[] opcodeNames = {
        "REGISTER_CALLBACK_SOCKET_SOURCE",
        "RETURN_CALLBACK_SOCKET",
        "REQUEST_CALLBACK_SOCKET",
        "RETURN_SERVER_ENDPOINT_INFO"
    };

    /**
     * Utility method to convert an IP address to a string.
     *
     * @param address The four bytes of an IP address.
     * @return The corresponding string (a.b.c.d format).
     */
    public static String getAddressString(byte[] address) {
        return ((int) address[0] & 0xff) + "."
                + ((int) address[1] & 0xff) + '.'
                + ((int) address[2] & 0xff) + '.'
                + ((int) address[3] & 0xff);
    }

    public static String getOpcodeName(int opcode) {
        return opcodeNames[opcode];
    }
}
