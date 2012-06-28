<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <div class="box wide2">
    <div class="rightLinks">
      Welcome <c:out value="${sessMan.currentUser}"/> <br/>
      <a href="javascript:print();">print</a> |
      <a href="javascript:self.close();">close this window</a>
    </div>
    <h4>Reservation List</h4>

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
            <c:forEach var="reg" items="${userAgenda}" varStatus="loopStatus">
              <c:choose>
                <c:when test='${(loopStatus.index)%2 eq 0}'>
                  <tr class="b">
                </c:when>
                <c:otherwise>
                  <tr class="a">
                </c:otherwise>
              </c:choose>
                <td class="ticketEventSummary">
                  <c:out value="${reg.event.summary}"/>
                </td>
                <td class="ticketDateTime">
                  <c:out value="${reg.event.date}"/> - <c:out value="${reg.event.time}"/>
                </td>
                <td class="ticketLocation">
                  <c:out value="${reg.event.location}"/>
                </td>
                <td class="ticketId">
                  <c:out value="${reg.ticketId}"/>
                </td>
                <td>
                  <c:out value="${reg.ticketsRequested}"/>
                </td>
                <td>
                  <select name='tickets<c:out value="${reg.ticketId}"/>' id='tickets<c:out value="${reg.ticketId}"/>'>
                    <c:forEach var="i" begin="1" end="${reg.event.ticketsAllowed}">
                       <c:choose>
                         <c:when test="${i == reg.ticketsRequested}">
                           <option value="<c:out value='${i}'/>"  selected="selected"><c:out value="${i}" /></option>
                         </c:when>
                         <c:otherwise>
                           <option value="<c:out value='${i}' />"><c:out value="${i}" /></option>
                         </c:otherwise>
                       </c:choose>
                    </c:forEach>
                  </select>
                  <a href="javascript:doUpdateTicket('<c:out value="${reg.ticketId}"/>')" onclick="return confirmUpdateTicket('<c:out value="${reg.ticketId}"/>','<c:out value="${reg.event.summary}"/>')">update</a>
                   |
                  <a href='removeTicket.do?id=<c:out value="${reg.ticketId}"/>' onclick="return confirmRemoveTicket('<c:out value="${reg.event.summary}"/>')">remove</a>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </table>
    </form>
    <div id="eventInfo">
      <!-- This is a good place to put general information for users. -->
    </div>
  </div>

 <%@ include file="foot.jsp" %>