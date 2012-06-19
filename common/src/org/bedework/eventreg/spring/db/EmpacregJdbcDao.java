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

package org.bedework.eventreg.spring.db;

import org.bedework.eventreg.spring.bus.Event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class EmpacregJdbcDao implements EmpacregDao {

  /** Logger for this class and subclasses */
  protected final Log logger = LogFactory.getLog(getClass());

  private JdbcTemplate jdbcTemplate;

  public void setDataSource(final DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public List<Map> getUserRegistrations(final String email) {
    // List userInfo = jdbcTemplate.queryForList("select * from registrations where email = ", new Object[] {new String(email)} );
    List userInfo = jdbcTemplate.queryForList("select * from registrations where email = '" + email + "'" );
    return userInfo;
  }

  @Override
  public List getRegistrations(final String eventGUID) {
    List registrations = jdbcTemplate.queryForList("select r.*,u.fname,u.lname,u.rpi from registrations r, users u where r.email = u.email and r.eventguid = '" + eventGUID + "'" );
    return registrations;
  }

  @Override
  public boolean validateEmail(final String activationCode) {
    int retval = 0;
    //  try {
    //      count = jdbcTemplate.queryForInt("select count(*) from users where activation_code = '"  + activationCode + "'");
    // } catch (Exception e) {
    //     logger.info("Activation code not found");
    // }
    //if (count > 0) {
    retval = this.jdbcTemplate.update("update users set type = 'confirmed' where activation_code = '" + activationCode + "'");
    if( retval == 1) {
      return true;
    }
    else {
      return false;
    //  }
    //  return false;
    }
  }

  @Override
  public int registerUserInEvent(final String email, final Event eif, final int numTickets, final String comment, final String regType, final String userType) {
    Timestamp  sqlDate = new Timestamp(new java.util.Date().getTime());
    logger.debug("Event details: " + email +" "+ eif.getEventGUID() +" "+ eif.getEmpacKey() +" "+ eif.getEventType() + " " + regType);
    int count = jdbcTemplate.queryForInt("select count(*) from registrations where email = '" + email + "' and eventguid = '" + eif.getEventGUID() + "'");
    logger.debug("query: select count(*) from registrations where email = '" + email + "' and eventguid = '" + eif.getEventGUID() + "'");
    logger.debug("query count: " + count);
    /* we  let superusers register over and over, but not regular users */
    if ( (count > 0)  && !userType.equals("superuser")) {
      this.jdbcTemplate.update("update registrations set numtickets = " + numTickets + ", comment = '" + comment + "', type = '" + regType + "' where email = '" + email + "' and eventguid = '" + eif.getEventGUID() + "'");
    } else {
      this.jdbcTemplate.update("insert into registrations (email, " +
      		"eventguid, " +
      		"queryStr, " +
      		"eventid, " +
      		"numtickets, " +
      		"type, " +
      		"created_at, " +
      		"comment) values (?,?,?,?,?,?,?,?)",
      		new Object[] {email,
      		              eif.getEventGUID(),
      		              eif.getQueryStr(),
      		              eif.getEmpacKey(),
      		              new Integer(numTickets),
      		              regType,
      		              sqlDate,
      		              comment});
    }
    return 0;
  }

  @Override
  public int getTicketCount(final String eventGUID) {
    int count = 0;
    try {
      count = jdbcTemplate.queryForInt("select sum(numtickets) from registrations where eventguid = '"  + eventGUID + "'");
    } catch (Exception e) {
      logger.info(e);
    }
    return count;
  }

  @Override
  public int getUserTicketCount(final String eventGUID, final String email) {
    int count = 0;
    try {
      count = jdbcTemplate.queryForInt("select numtickets from registrations where eventguid = '"  + eventGUID + "' and email = '" + email + "'");
    } catch (Exception e) {
      logger.info(e);
    }
    return count;
  }

  @Override
  public int getRegistrantCount(final String eventGUID) {
    int count = 0;
    try {
      count = jdbcTemplate.queryForInt("select count(*) from registrations where eventguid = '"  + eventGUID + "'");
    } catch (Exception e) {
      logger.info(e);
    }
    return count;
  }

  @Override
  public void removeTicketById(final int id, final String email, final String userType) throws Throwable {
    logger.info("About to remove ticket " + id + " for " + userType + " user " + email);
    if (userType.equals("superuser")) {
      this.jdbcTemplate.update("delete from registrations where id = " + id );
    } else {
      // limit deletions to the user's tickets
      this.jdbcTemplate.update("delete from registrations where id = " + id + " and email = '" + email + "'");
    }
  }
  @Override
  public void updateTicketById(final int id, final String email, final int numTickets, final String comment, final String type, final String userType) throws Throwable {
    logger.info("About to update ticket " + id + " for user " + email);
    if (userType.equals("superuser")) {
      this.jdbcTemplate.update("update registrations set numtickets = " + numTickets + ", comment = '" + comment + "', type = '" + type + "' where id = " + id );
    } else {
      // no comments for normal users
      this.jdbcTemplate.update("update registrations set numtickets = " + numTickets + ", type = '" + type + "' where id = " + id + " and email = '" + email + "'");
    }
  }
}

