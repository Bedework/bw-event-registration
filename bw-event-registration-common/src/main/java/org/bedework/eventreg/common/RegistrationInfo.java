package org.bedework.eventreg.common;

public interface RegistrationInfo {
  /**
   * @return number of fulfilled tickets
   */
  long getTicketCount();

  /**
   * @return max number of tickets overall
   */
  long getMaxTicketCount();

  /**
   * @return max number of tickets per user
   */
  long getMaxTicketsPerUser();

  /**
   * @return number of tickets being waited for
   */
  long getWaitingTicketCount();

  /**
   * @return Wait list limit  as String - either an integer absolute limit or a percentage or null for none
   */
  String getWaitListLimit();

  /**
   * @return number registrants
   */
  long getRegistrantCount();

  String getRegistrationStart();

  String getRegistrationEnd();

  /**
   * @return number available tickets. Less than 0 for overallocated
   */
  default long getAvailableTickets() {
    return getMaxTicketCount() - getTicketCount();
  }
}
