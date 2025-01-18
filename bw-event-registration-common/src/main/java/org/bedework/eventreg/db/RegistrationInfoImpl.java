package org.bedework.eventreg.db;

import org.bedework.eventreg.common.RegistrationInfo;
import org.bedework.base.ToString;

public class RegistrationInfoImpl implements RegistrationInfo {
  private long ticketCount;
  private long maxTicketCount;
  private long maxTicketsPerUser;
  private long waitingTicketCount;
  private String waitListLimit;
  private long registrantCount;
  private String registrationStart;
  private String registrationEnd;

  public void setTicketCount(final long val) {
    ticketCount = val;
  }

  @Override
  public long getTicketCount() {
    return 0;
  }

  public void setMaxTicketCount(final long val) {
    maxTicketCount = val;
  }

  @Override
  public long getMaxTicketCount() {
    return 0;
  }

  public void setMaxTicketsPerUser(final long val) {
    maxTicketsPerUser = val;
  }

  @Override
  public long getMaxTicketsPerUser() {
    return 0;
  }

  public void setWaitingTicketCount(final long val) {
    waitingTicketCount = val;
  }

  @Override
  public long getWaitingTicketCount() {
    return 0;
  }

  public void setWaitListLimit(final String val) {
    waitListLimit = val;
  }

  @Override
  public String getWaitListLimit() {
    return waitListLimit;
  }

  public void setRegistrantCount(final long val) {
    registrantCount = val;
  }

  @Override
  public long getRegistrantCount() {
    return registrantCount;
  }

  public void setRegistrationStart(final String val) {
    registrationStart = val;
  }

  @Override
  public String getRegistrationStart() {
    return registrationStart;
  }

  public void setRegistrationEnd(final String val) {
    registrationEnd = val;
  }

  @Override
  public String getRegistrationEnd() {
    return registrationEnd;
  }

  public String toString() {
    return new ToString(this)
            .append("ticketCount", getTicketCount())
            .append("maxTicketCount", getMaxTicketCount())
            .append("maxTicketsPerUser", getMaxTicketsPerUser())
            .append("waitingTicketCount", getWaitingTicketCount())
            .append("waitListLimit", getWaitListLimit())
            .append("registrantCount", getRegistrantCount())
            .append("registrationStart", getRegistrationStart())
            .append("registrationEnd", getRegistrationEnd())
            .toString();
  }
}
