/* ********************************************************************
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 */
package org.bedework.eventreg.common;

import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * @author douglm
 *
 */
public class Mailer implements Logged {
  private final static BwLogger logger =
          new BwLogger().setLoggedClass(Mailer.class);

  /**
   * @param recipients
   * @param subject
   * @param message
   * @param from
   * @param id
   * @throws MessagingException
   */
  public static void postMail(final String[] recipients,
                              final String subject,
                              final String message ,
                              final String from,
                              final String id) {
    try {
      final boolean debug = false;

      //Set the host smtp address
      final Properties props = new Properties();
      props.put("mail.smtp.host", "mail.bedework.edu");

      // create some properties and get the default Session
      final Session session = Session.getDefaultInstance(props, null);
      session.setDebug(debug);

      // create a message
      final Message msg = new MimeMessage(session);

      // set the from and to address
      final InternetAddress addressFrom = new InternetAddress(from);
      msg.setFrom(addressFrom);

      final InternetAddress[] addressTo =
              new InternetAddress[recipients.length];
      for (int i = 0; i < recipients.length; i++) {
        addressTo[i] = new InternetAddress(recipients[i]);
      }
      msg.setRecipients(Message.RecipientType.TO, addressTo);

      // Optional : You can also set your custom headers in the Email if you Want
      msg.addHeader("X-BEDEWORK-REG", id);

      // Setting the Subject and Content Type
      msg.setSubject(subject);
      msg.setContent(message, "text/plain");
      Transport.send(msg);
    } catch (final MessagingException mex) {
      throw new EventregException(mex);
    }
  }

  public static String getEmail(final String user,
                                final String defaultEmailDomain) {
    if (user == null) {
      return null;
    }
      /*
      final Object[] pars = { currentUser };
      final String[] signature = { String.class.getName() };
      final AccountInfo ainfo =
              (AccountInfo)MBeanUtil.invoke(
                          "org.bedework.selfreg:service=config",
                          "getAccount", pars, signature);

      if (ainfo == null) {
        if (debug()) {
          debug("No account info for " + currentUser);
        }

        return currentUser + "@columbia.edu";
      }

      if (debug()) {
        debug("Retrieved account info " + ainfo);
      }

      return ainfo.getEmail();
      */
    final String email = getSelfregEmail(user);

    if (email != null) {
      return email;
    }

    if (defaultEmailDomain == null) {
      return null;
    }

    return user + "@" + defaultEmailDomain;
  }


  private static String getSelfregEmail(final String user) {
    try {
      final Context ctx = new InitialContext();
      final DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/selfregDS");

      try (final Connection con = ds.getConnection()) {
        con.setAutoCommit(false);
        //noinspection SqlResolve
        try (final PreparedStatement pstmt =
                     con.prepareStatement(
                             "select bw_email from bw_accounts " +
                                     "WHERE bw_account = ?")) {
          pstmt.setString(1, user);
          try (final ResultSet rs = pstmt.executeQuery()) {
            if (!rs.next()) {
              return null;
            }

            return rs.getString("bw_email");
          }
        }
      }
    } catch (final Throwable t) {
      logger.error(t);
      return null;
    }
  }

  public BwLogger getLogger() {
    return logger;
  }
}

