<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <div class="fullpage">
    <div class="rightLinks">
      Welcome <c:out value="${sessMan.currentUser}"/> <br/>
      <a href="javascript:print();">print</a> |
      <a href="javascript:self.close();">close this window</a>
    </div>
    <h1>Reservation List</h1>

    <c:if test="${sessMan.message != null and sessMan.message != ''}">
      <div id="message">
        <c:out value="${sessMan.message}"/>
      </div>
    </c:if>
    <table id="userAgenda" cellspacing="2" class="tablesorter">
      <thead>
        <tr>
          <th>
            <%-- thText classes are needed to keep the table sorter 
                 background image from overlapping the text. --%>
            <span class="thText">ID</span>
          </th>
          <th>
            <span class="thText">Event</span>
          </th>
          <th>
            <span class="thText">Start Date/Time</span>
          </th>
          <th>
            <span class="thText">Venue</span>
          </th>
          <th>
            <span class="thText">Tickets</span>
          </th>
          <th>
            <span class="thText">State</span>
          </th>
          <th>
          </th>
        </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty regs}">
            <tr class="b">
              <td colspan="7">
                No tickets reserved
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="reg" items="${regs}" varStatus="loopStatus">
              <c:choose>
                <c:when test="${reg.numTickets < reg.ticketsRequested}">
                  <tr class="waitList">
                </c:when>
                <c:when test='${(loopStatus.index)%2 eq 0}'>
                  <tr class="b">
                </c:when>
                <c:otherwise>
                  <tr class="a">
                </c:otherwise>
              </c:choose>
                <td class="ticketId">
                  <c:out value="${reg.registrationId}"/>
                </td>
                <td class="ticketEventSummary">
                  <c:out value="${reg.event.summary}"/>
                </td>
                <td class="ticketDateTime">
                  <c:out value="${reg.event.dateTime}"/>
                </td>
                <td class="ticketLocation">
                  <c:out value="${reg.event.location}"/>
                </td>
                <td class="tickets">
                  <c:choose>
                    <c:when test="${(reg.numTickets < reg.ticketsRequested) and (reg.numTickets ne 0)}">
                      <c:out value="${reg.numTickets}"/> of <c:out value="${reg.ticketsRequested}"/> fulfilled
                    </c:when>
                    <c:otherwise>
                      <c:out value="${reg.ticketsRequested}"/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="state">
                  <c:choose>
                    <c:when test="${reg.numTickets < reg.ticketsRequested}">
                      waiting
                    </c:when>
                    <c:otherwise>
                      fulfilled
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="controls">
                  <c:if test="${(reg.event.maxTicketsPerUser > 1)}">
                    <select name='tickets<c:out value="${reg.registrationId}"/>' id='tickets<c:out value="${reg.registrationId}"/>'>
                      <c:forEach var="i" begin="1" end="${reg.event.maxTicketsPerUser}">
                         <c:choose>
                           <c:when test="${i == reg.numTickets}">
                             <option value="${i}" selected="selected">${i}</option>
                           </c:when>
                           <c:otherwise>
                             <option value="${i}">${i}</option>
                           </c:otherwise>
                         </c:choose>
                      </c:forEach>
                    </select>
                    <a href="javascript:doUpdateTicket('<c:out value="${reg.registrationId}"/>','<c:out value="${reg.event.href}"/>','<c:out value="${reg.type}"/>')" onclick="return confirmUpdateTicket('<c:out value="${reg.registrationId}"/>','<c:out value="${reg.event.summary}"/>')">update</a>
                     |
                   </c:if>
                  <a href='removeAgendaReg.do?regid=<c:out value="${reg.registrationId}"/>' onclick="return confirmRemoveTicket('<c:out value="${reg.event.summary}"/>')">remove</a>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
    <div id="eventInfo">
      <!-- This is a good place to put general information for users. -->
    </div>
  </div>

 <%@ include file="foot.jsp" %>