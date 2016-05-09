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

import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Socket factory for clients who want servers to be able to call them back even
 * if the client's address/port are not reachable from the server. This happens
 * with firewalls and dial-up networking, for example.
 * <p>
 *
 * The factory provides two main capabilities:
 * <p>
 * <ul>
 * <li>A gateway registry for going through firewalls</li>
 * <li>A signalling channel for establishing callback sockets</li>
 * </ul>
 * <p>
 * The gateway registry allows a mapping to be made between two endpoints. For
 * example, the endpoint cssassociates.com:3453 can be mapped to
 * somewhereElse.com:2356. Client connections made to the first endpoint from
 * RMI will actually be connected to the second endpoint. This situation occurs
 * when a port on a firewall is "opened" and tunnelled to a host within the
 * firewall.
 * <p>
 *
 * This could also be done by adding a route to the client hosts routing table.
 * Unfortunately, that requires administration on the client host.
 * <p>
 *
 * A signalling channel is a socket from the client to the server and carries
 * the tunnelling protocol. The client establishes a signalling channel to the
 * server, which must be running the ServerTwoWaySocketFactory. When the server
 * needs to make a callback to the client, it requests a socket via the channel
 * instead of trying to directly connect to the client. The client establishes a
 * socket to the server for the server to use in the callback.
 *
 * @author Tim Taylor -- tttaylor@cssassociates.com
 */
public class ClientTwoWaySocketFactory extends RMISocketFactory {

    /**
     * Registered gateways
     */
    private Map gatewayMap = Collections.synchronizedMap(new HashMap());

    /**
     * Signalling channel used to handshake with server. Currently supports one
     * server and one channel. I.e., each client can have only one server
     * (without modifying this code to support multiple channels). However, the
     * server can have as many clients as it wants.
     */
    private SignallingChannel channel;

    /**
     * Register a gateway. Client connections requested via
     * <code>createSocket</code> for <code>host:port</code> will instead be made
     * to <code>gateHost:gatePort</code>
     *
     * @param host The destination host.
     * @param port The destination port.
     * @param gateHost The gateway host (proxy, firewall, etc.).
     * @param gatePort The gateway port.
     */
    public void registerGateway(String host, int port, String gateHost, int gatePort) {
        gatewayMap.put(EndpointInfo.getEndpointString(host, port), new EndpointInfo(gateHost, gatePort));
    }

    /**
     * Establish a tunnelling protocol signalling channel to the server at <code>
     * @param address
     * @param port
     * @throws java.io.IOException
     */
    public void establishSignallingChannel(String address, int port) throws IOException {

        channel = new SignallingChannel(address, port);

        // The direct host/port and destination host/port may
        // or may not be the same.  The will be different
        // if we are initially connecting to a firewall tunnel.
        registerGateway(channel.getDestinationAddress(),
                channel.getDestinationPort(),
                channel.getDirectAddress(),
                channel.getDirectPort());

        channel.start();
    }

    /**
     * Returns the endpoint associated with the <code>host</code> and
     * <code>port</code>. The endpoint returned is the endpoint to be directly
     * contacted. If the <code>host</code> and <code>port</code> have an
     * associated gateway endpoint, that endpoint is returned. Otherwise an
     * endpoint for the provided <code>host</code> and <code>port</code> is
     * returned.
     *
     * @param host
     * @param port
     * @return The endpoint to be connected to.
     */
    public EndpointInfo getDirectEndpoint(String host, int port) {
        EndpointInfo endpointInfo = (EndpointInfo) gatewayMap.get(EndpointInfo.getEndpointString(host, port));

        if (endpointInfo != null) {
            return endpointInfo;
        } else {
            return new EndpointInfo(host, port);
        }
    }

    /**
     * Creates a socket to the specified <code>host</code> and <code>
     * port</code>. If a gateway has been registered using
     * <code>registerGateway</code> then the socket will be connected to that
     * gateway. Otherwise, the socket is connected directly to the specified
     * <code>host</code> and <code>port</code>.
     *
     * @return 
     * @throws java.io.IOException
     * @see #registerGateway()
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        String endpoint = EndpointInfo.getEndpointString(host, port);
        String directHost = host;
        int directPort = port;

        EndpointInfo ep = getDirectEndpoint(host, port);

        return new Socket(ep.getHost(), ep.getPort());
    }

    /**
     * @return a server socket for the specified port
     * @throws java.io.IOException
     */
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        ServerSocket sock = new ServerSocket(port);
        if (channel != null) {
            // Inform server we are exporting a callback
            channel.getOutputStream().writeInt(sock.getLocalPort());
        }
        return sock;
    }

    /**
     * @return a hashcode for this factory
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @param that
     * @return true if the two socket factories in question return
     * interchangeable sockets.
     */
    @Override
    public boolean equals(Object that) {
        return (that != null) && (that.getClass().equals(this.getClass()));
    }
}
