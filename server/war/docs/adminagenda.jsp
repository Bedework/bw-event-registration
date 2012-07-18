<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <c:if test="${sessMan.adminUser}">
      <div class="fullpage">
        <div class="rightLinks">
          Welcome <c:out value="${sessMan.currentUser}"/> (admin)<br/>
          <a href="javascript:print();">print</a> |
          <a href="javascript:self.close();">close this window</a> |
          <a href="download.do?href=${sessMan.href}&amp;atkn=${sessMan.adminToken}">download registrations</a>
        </div>
        <h1><c:out value="${sessMan.currEvent.summary}"/></h1>
        <div class="eventDateTime">
          <c:out value="${sessMan.currEvent.dateTime}"/>
          <%--<fmt:formatDate value="${sessMan.currEvent.date}" type="both" timeStyle="long" dateStyle="long" />--%>
        </div>
        <div id="regInfo">
          <p class="left">
            Registrations: <c:out value="${sessMan.registrantCount}"/><br/>
            Total active tickets:  <c:out value="${sessMan.regTicketCount}"/><br/>
            Total waiting list tickets: <c:out value="${sessMan.waitingTicketCount}"/>
          </p>
          <p class="left">
            Max tickets allowed for event: <c:out value="${sessMan.currEvent.maxTickets}"/><br/>
            Max tickets allowed per user: <c:out value="${sessMan.currEvent.maxTicketsPerUser}"/>
          </p>
          <p>
            Registration starts: <c:out value="${sessMan.currEvent.registrationStart}"/><br/>
            Registration ends: <c:out value="${sessMan.currEvent.registrationEnd}"/>
          </p>
        </div>
        <form name="adminagenda" action="">
          <c:if test="${sessMan.message != null and sessMan.message != ''}">
            <div id="message">
              <c:out value="${sessMan.message}"/>
            </div>
          </c:if>
          <table id="adminAgenda" cellspacing="2" class="tablesorter">
          <thead>
            <tr>
              <th>
              <%-- thText classes are needed to keep the table sorter 
                   background image from overlapping the text. --%>
                <span class="thText">
                  Ticket ID
                </span>
              </th>
              <th>
                <span class="thText">
                  UserID
                </span>
              </th>
              <th>
                <span class="thText">
                  Tickets
                </span>
              </th>
              <th>
                <span class="thText">
                  Type
                </span>
              </th>
              <th>
                <span class="thText">
                  Comment
                </span>
              </th>
              <th>
                <span class="thText">
                  Created
                </span>
              </th>
              <th>
                <span class="thText">
                  Options
                </span>
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
                      <c:when test="${reg.type eq 'waiting'}">
                        <tr class="waitList">
                      </c:when>
                      <c:otherwise>
                        <tr>
                      </c:otherwise>
                    </c:choose>
                      <td class="regTicketId">
                        <c:out value="${reg.registrationId}"/>
                      </td>
                      <td class="regEmail">
                        <c:out value="${reg.authid}"/>
                      </td>
                      <td class="regNumTickets">
                        <input name='numtickets<c:out value="${reg.registrationId}"/>' id='numtickets<c:out value="${reg.registrationId}"/>' type="text" value='<c:out value="${reg.numTickets}"/>' size="3"/>
                      </td>
                      <td class="regType">
                        <select name='type<c:out value="${reg.registrationId}"/>' id='type<c:out value="${reg.registrationId}"/>'>
                          <option value="registered" ${reg.type == 'registered' ? 'selected="selected"' : ''}>registered</option>
                          <option value="hold" ${reg.type == 'hold' ? 'selected="selected"' : ''}>hold</option>
                          <option value="waiting" ${reg.type == 'waiting' ? 'selected="selected"' : ''}>waiting</option>
                        </select>
                      </td>
                      <td class="regComment">
                        <input name='comment<c:out value="${reg.registrationId}"/>' id='comment<c:out value="${reg.registrationId}"/>' type="text" value='<c:out value="${reg.comment}"/>'/>
                      </td>
                      <td class="regCreated">
                        <c:out value="${reg.created}"/>
                      </td>
                      <td class="regControls">
                        <a href="javascript:doUpdateAdminTicket('<c:out value="${reg.registrationId}"/>&amp;atkn=${sessMan.adminToken}','<c:out value="${reg.event.href}"/>')" onclick="return confirmUpdateAdminTicket()">update</a> |
                        <a href='removeAgendaReg.do?regid=<c:out value="${reg.registrationId}"/>&amp;href=<c:out value="${reg.event.href}&amp;atkn=${sessMan.adminToken}"/>' onclick="return confirmRemoveAdminTicket('<c:out value="${reg.authid}"/>')">remove</a>
                      </td>
                    </tr>
                  </c:forEach>
                </c:otherwise>
              </c:choose>
            </tbody>
          </table>
        </form>
      </div>
    </c:if>

 <%@ include file="foot.jsp" %>