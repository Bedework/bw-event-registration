<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ include file="head.jsp" %>

  <div class="box wide2">
    <div class="rightLinks">
      Welcome <c:out value="${sessMan.userInfo.fname}"/> (<c:out value="${sessMan.userInfo.email}"/>)<br/>
      <a href="javascript:print();">print</a> |
      <a href="javascript:self.close();">close this window</a>
    </div>
    <h4>EMPAC Reservation List</h4>

    <div id="userInfo">
      <c:out value="${sessMan.userInfo.fname}"/> <c:out value="${sessMan.userInfo.lname}"/><br/>
      <c:out value="${sessMan.userInfo.email}"/><c:if test="${sessMan.userInfo.phone != ''}">, <c:out value="${sessMan.userInfo.phone}"/></c:if><br/>
      <c:if test="${sessMan.userInfo.street1 != ''}"><c:out value="${sessMan.userInfo.street1}"/>, </c:if>
      <c:if test="${sessMan.userInfo.street2 != ''}"><c:out value="${sessMan.userInfo.street2}"/>, </c:if>
      <c:if test="${sessMan.userInfo.city != ''}"><c:out value="${sessMan.userInfo.city}"/>, </c:if>
      <c:out value="${sessMan.userInfo.state}"/>
      <c:out value="${sessMan.userInfo.country}"/>
    </div>

    <form name="agenda" action="">
      <c:if test="${sessMan.message != null and sessMan.message != ''}">
        <div id="message">
          <c:out value="${sessMan.message}"/>
        </div>
      </c:if>
      <table id="agenda" cellspacing="2">
        <tr>
          <th>
            Event
          </th>
          <th>
            Start Date/Time
          </th>
          <th>
            Venue
          </th>
          <th>
            Ticket ID
          </th>
          <th>
            Tickets
          </th>
          <th>
          </th>
        </tr>
        <c:choose>
          <c:when test="${empty userAgenda}">
            <tr class="b">
              <td colspan="6">
                No tickets reserved
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="event" items="${userAgenda}" varStatus="loopStatus">
              <c:choose>
                <c:when test='${(loopStatus.index)%2 eq 0}'>
                  <tr class="b">
                </c:when>
                <c:otherwise>
                  <tr class="a">
                </c:otherwise>
              </c:choose>
                <td class="ticketEventSummary">
                  <c:out value="${event['eventSummary']}"/>
                </td>
                <td class="ticketDateTime">
                  <c:out value="${event['eventDateStr']}"/> - <c:out value="${event['eventTimeStr']}"/>
                </td>
                <td class="ticketLocation">
                  <c:out value="${event['eventLocation']}"/>
                </td>
                <td class="ticketId">
                  <c:out value="${event['ticketId']}"/>
                </td>
                <td>
                  <c:out value="${event['ticketsRequested']}"/>
                </td>
                <td>
                  <select name='tickets<c:out value="${event['ticketId']}"/>' id='tickets<c:out value="${event['ticketId']}"/>'>
                    <c:forEach var="i" begin="1" end="${event['ticketsAllowed']}">
                       <c:choose>
                         <c:when test="${i == event['ticketsRequested']}">
                           <option value="<c:out value='${i}'/>"  selected="selected"><c:out value="${i}" /></option>
                         </c:when>
                         <c:otherwise>
                           <option value="<c:out value='${i}' />"><c:out value="${i}" /></option>
                         </c:otherwise>
                       </c:choose>
                    </c:forEach>
                  </select>
                  <a href="javascript:doUpdateTicket('<c:out value="${event['ticketId']}"/>')" onclick="return confirmUpdateTicket('<c:out value="${event['ticketId']}"/>','<c:out value="${event['eventSummary']}"/>')">update</a>
                   |
                  <a href='removeTicket.do?id=<c:out value="${event['ticketId']}"/>' onclick="return confirmRemoveTicket('<c:out value="${event['eventSummary']}"/>')">remove</a>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </table>
    </form>
    <div id="empacInfo">
      <c:choose>
        <c:when test="${empty userAgenda}">
          <h3>You have no tickets reserved for a Celebration Weekend event.</h3>
          <!-- p id="agendaToReserveMsg">To reserve tickets, select "request tickets" from an <a href="http://empac.rpi.edu">EMPAC</a> event.</p-->
        </c:when>
        <c:otherwise>
          <h3>Thank you for reserving tickets to a Celebration Weekend event!</h3>
        </c:otherwise>
      </c:choose>

      <p class="ticketInfo">
        Tickets reserved online will be available for pickup at the EMPAC Box Office on the following dates:
      </p>

      <p class="indent strong">
        Friday, December 4th from 2:00 - 7:00 PM, EMPAC lobby.<br/>
        Saturday, December 5th from 2:00 - 7:00 PM, EMPAC NIRVE center.
      </p>

      <p>
        Please bring your <strong>Rensselaer ID card to pick up tickets</strong>.
        Tickets not be picked up by 7:00 PM the night of the concert will be released.
        If you have tickets for both concerts you may pick up both sets of tickets on Friday, December 4th.
      </p>

      <p>
        For more information please see <a href="https://celebration.empac.rpi.edu/info/">https://celebration.empac.rpi.edu/info/</a>
      </p>

      <p>
        Questions about your reservation?<br/>
        Contact the EMPAC Ticket Office at
        <a href="mailto:empacboxoffice@rpi.edu">empacboxoffice@rpi.edu</a> or 518-276-3921.
      </p>

      <p>
        <strong>Ticket Policies:</strong><br/>
        No refunds/exchanges<br/>
        Use of cameras or recording devices in venues is not permitted.<br/>
        Late arrivals will be seated at the discretion of venue management.<br/>
        Not responsible for items lost, stolen or left behind.
      </p>

      <p>
        <strong>Accessibility</strong><br/>
        All venues are wheelchair accessible. Please contact the Box Office by December 2nd at
        <a href="mailto:empacboxoffice@rpi.edu">empacboxoffice@rpi.edu</a> or 518-276-3921 to
        reserve accessible seating or to request additional disability-related accommodation.
      </p>

      <p>
        <strong>Box Office</strong><br/>
        <a href="http://empac.rpi.edu">Experimental Media and Performing Arts Center</a><br/>
        <a href="http://www.rpi.edu">Rensselaer Polytechnic Institute</a><br/>
        110 8th street<br/>
        Troy, NY 12180<br/>
        518.276.3921<br/>
        <a href="mailto:empacboxoffice@rpi.edu">empacboxoffice@rpi.edu</a><br/>
        <a href="http://empac.rpi.edu">http://empac.rpi.edu</a>
      </p>
    </div>
  </div>

 <%@ include file="foot.jsp" %>