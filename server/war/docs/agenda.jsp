<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <div class="fullpage">
    <div class="rightLinks">
      Welcome <c:out value="${sessMan.currentUser}"/> <br/>
      <a href="javascript:print();">print</a> |
      <a href="javascript:self.close();">close this window</a>
    </div>
    <h1>Reservation List</h1>

    <form name="agenda" action="">
      <c:if test="${sessMan.message != null and sessMan.message != ''}">
        <div id="message">
          <c:out value="${sessMan.message}"/>
        </div>
      </c:if>
      <table id="agenda" cellspacing="2">
        <tr>
          <th>
            ID
          </th>
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
            Tickets
          </th>
          <th>
          </th>
        </tr>
        <c:choose>
          <c:when test="${empty regs}">
            <tr class="b">
              <td colspan="6">
                No tickets reserved
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="reg" items="${regs}" varStatus="loopStatus">
              <c:choose>
                <c:when test='${(loopStatus.index)%2 eq 0}'>
                  <tr class="b">
                </c:when>
                <c:otherwise>
                  <tr class="a">
                </c:otherwise>
              </c:choose>
                <td class="ticketId">
                  <c:out value="${reg.ticketid}"/>
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
                  <c:out value="${reg.numTickets}"/>
                </td>
                <td class="controls">
                  <c:if test="${reg.event.maxTicketsPerUser > 1}">
                    <select name='tickets<c:out value="${reg.ticketid}"/>' id='tickets<c:out value="${reg.ticketid}"/>'>
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
                    <a href="javascript:doUpdateTicket('<c:out value="${reg.ticketid}"/>','<c:out value="${reg.event.href}"/>')" onclick="return confirmUpdateTicket('<c:out value="${reg.ticketid}"/>','<c:out value="${reg.event.summary}"/>')">update</a>
                     |
                   </c:if>
                  <a href='removeTicket.do?ticketid=<c:out value="${reg.ticketid}"/>' onclick="return confirmRemoveTicket('<c:out value="${reg.event.summary}"/>')">remove</a>
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