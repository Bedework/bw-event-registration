<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <c:if test="${sessMan.superUser}">
      <div class="fullpage">
        <div class="rightLinks">
          Welcome <c:out value="${sessMan.currentUser}"/> (superuser)<br/>
          <a href="javascript:print();">print</a> |
          <a href="javascript:self.close();">close this window</a> |
          <a href="download.do">download registrations</a>
        </div>
        <h1><c:out value="${sessMan.currEvent.summary}"/></h1>
        <p>
          <c:out value="${sessMan.currEvent.date}"/> - <c:out value="${sessMan.currEvent.time}"/>
        </p>
        <p>
          Event Agenda<br/>
          <strong>
            Registration deadline: <c:out value="${sessMan.currEvent.registrationEnd}"/><br/>
            Max allowed: <c:out value="${sessMan.currEvent.maxTickets}"/><br/>
            Registrations: <c:out value="${sessMan.registrantCount}"/><br/>
            Tickets requested:  <c:out value="${sessMan.ticketCount}"/>
          </strong>
        </p>
        <form name="suagenda" action="">
          <c:if test="${sessMan.message != null and sessMan.message != ''}">
            <div id="message">
              <c:out value="${sessMan.message}"/>
            </div>
          </c:if>
          <table id="agenda" cellspacing="2">
            <tr>
              <th>
                Ticket ID
              </th><!-- 
              <th>
                Name
              </th> -->
              <th>
                UserID
              </th>
              <th>
                Tickets
              </th>
              <th>
                Type
              </th>
              <th>
                Comment
              </th>
              <th>
                Created
              </th>
              <th>
              </th>
            </tr>
            <c:forEach var="reg" items="${suserAgenda}" varStatus="loopStatus">
              <c:choose>
                <c:when test='${(loopStatus.index)%2 eq 0}'>
                  <tr class="b">
                </c:when>
                <c:otherwise>
                  <tr class="a">
                </c:otherwise>
              </c:choose>
                <td class="regTicketId">
                  <c:out value="${reg.ticketId}"/>
                </td>
                <!-- <td>
                  <c:out value="${reg['fname']}"/>
                  <c:out value="${reg['lname']}"/>
                </td> -->
                <td class="regEmail">
                  <c:out value="${reg.email}"/>
                </td>
                <td class="regNumTickets">
                  <input name='numtickets<c:out value="${reg.ticketId}"/>' id='numtickets<c:out value="${reg.ticketId}"/>' type="text" value='<c:out value="${reg.numtickets}"/>' size="3"/>
                </td>
                <td class="regType">
                  <select name='type<c:out value="${reg.ticketId}"/>' id='type<c:out value="${reg.ticketId}"/>'>
                    <c:choose>
                      <c:when test="${reg.type == 'hold'}">
                        <option value="normal">normal</option>
                        <option value="hold" selected="selected">hold</option>
                      </c:when>
                      <c:otherwise>
                        <option value="normal" selected="selected">normal</option>
                        <option value="hold">hold</option>
                      </c:otherwise>
                    </c:choose>
                  </select>
                </td>
                <td class="regComment">
                  <input name='comment<c:out value="${reg.ticketId}"/>' id='comment<c:out value="${reg.ticketId}"/>' type="text" value='<c:out value="${reg.comment}"/>'/>
                </td>
                <td class="regCreated">
                  <c:out value="${reg.created}"/>
                </td>
                <td class="regControls">
                  <a href="javascript:doUpdateSuTicket('<c:out value="${reg.ticketId}"/>')" onclick="return confirmUpdateSuTicket()">update</a> |
                  <a href='removeAgendaTicket.do?id=<c:out value="${reg.ticketId}"/>' onclick="return confirmRemoveSuTicket('<c:out value="${reg.email}"/>')">remove</a>
                </td>
              </tr>
            </c:forEach>
          </table>
        </form>
      </div>
    </c:if>

 <%@ include file="foot.jsp" %>