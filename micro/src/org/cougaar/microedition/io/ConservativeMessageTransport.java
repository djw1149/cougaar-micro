/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */
package org.cougaar.microedition.io;

import java.io.*;
import java.util.*;

import org.cougaar.microedition.shared.*;
import org.cougaar.microedition.asset.*;

/**
 * This message transport strategy uses the same socket that the client originally connected
 * on to talk to it.
 */
public class ConservativeMessageTransport implements MessageTransport {

/**
 * This variable declares the reader class whose thread listens on a serversocket port.
 */
  private PacketReader reader = null;

  private InputStream in;
  private OutputStream out;

  private String nodeName = null;
/**
 * This constructor starts a listening/reader thread as well as instantiates a sender object.
 *
 * @param   myListenPort    the port number the reader thread should listen on.
 */
  public ConservativeMessageTransport (InputStream in, OutputStream out, String name) {
    this.in = in;
    this.out = out;
    reader = new PacketReader(in);
    reader.setMessageTransport(this);
    reader.start();
    nodeName = name;
  }

/**
   * This method sends a message to the desired server on a particular port.
   *
   * @param   message   a string representing the message to be sent.
   * @return  none
   */
  protected void sendMessage(String message) {

    try {
      byte [] data = message.getBytes();
      out.write(data);
      out.flush();
    } catch (Exception e) {
      System.err.println("Unable to sendMessage " + e);
    }
  }

  Vector listeners = new Vector();

  public void takePacket(String data, String source) {
    Enumeration en = listeners.elements();
    while (en.hasMoreElements()) {
      MessageListener ml = (MessageListener)en.nextElement();
      ml.deliverMessage(data, source);
    }

  }

  public void addMessageListener(MessageListener ml) {
    if (!listeners.contains(ml))  {
      listeners.addElement(ml);
    }
  }

  public void removeMessageListener(MessageListener ml) {
    if (listeners.contains(ml))  {
      listeners.removeElement(ml);
    }
  }

  public void sendMessage(Encodable msg, MicroCluster dest, String op) {
    StringBuffer buf = new StringBuffer();
    buf.append(nodeName + ":");
    buf.append(msg.xmlPreamble);
    buf.append("<message op=\""+op+"\">");
    msg.encode(buf);
    buf.append("</message>");
    buf.append('\0');

//    System.out.println("Sending: "+buf.toString()+" to "+ipAddress);

    sendMessage(buf.toString());
  }



}