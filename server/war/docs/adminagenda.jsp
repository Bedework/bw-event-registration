<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <c:if test="${sessMan.adminUser}">
      <div class="fullpage">
        <div class="rightLinks">
          Welcome <c:out value="${sessMan.currentUser}"/> (admin)<br/>
          <a href="javascript:print();">print</a> |
          <a href="javascript:self.close();">close this window</a> |
          <c:set var="fileName" scope="page">EventReg-<c:out value="${fn:substring(fn:replace(sessMan.currEvent.summary, ' ',''),0,9)}"/>-<c:out value="${fn:substring(sessMan.currEvent.dateTime,0,7)}"/>.csv</c:set>
          <a href="download.do?href=${sessMan.href}&amp;atkn=${sessMan.adminToken}&amp;fn=${fileName}">download registrations</a>
        </div>
        <h1><c:out value="${sessMan.currEvent.summary}"/></h1>
        <div class="eventDateTime">
          <c:out value="${sessMan.currEvent.dateTime}"/>
          <%--<fmt:formatDate value="${sessMan.currEvent.date}" type="both" timeStyle="long" dateStyle="long" />--%>
        </div>
        <div id="adminAgendaButtons">
          <button onclick="window.location.reload()">refresh</button>
        </div>
        <div id="regInfo">
          <p class="left">
            Registrations: <c:out value="${sessMan.registrantCount}"/><br/>
            Fulfilled tickets:  <c:out value="${sessMan.regTicketCount}"/><br/>
            Waiting tickets: <c:out value="${sessMan.waitingTicketCount}"/>
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
                  Tickets<br/>Allocated
                </span>
              </th>
              <th>
                <span class="thText">
                  Tickets<br/>Requested
                </span>
              </th>
              <th>
                <span class="thText">
                  State
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
                    <td colspan="8">
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
                      <td class="regRequestedTickets">
                        <c:out value="${reg.ticketsRequested}"/>
                      </td>
                      <td class="regState">
                        <c:choose>
                          <c:when test="${reg.type == 'hold'}">
                            hold
                          </c:when>
                          <c:when test="${reg.numTickets < reg.ticketsRequested}">
                            waiting
                          </c:when>
                          <c:otherwise>
                            fulfilled
                          </c:otherwise>
                        </c:choose>
                        <%-- 
                        <select name='type<c:out value="${reg.registrationId}"/>' id='type<c:out value="${reg.registrationId}"/>'>
                          <option value="registered" ${reg.type == 'registered' ? 'selected="selected"' : ''}>registered</option>
                          <option value="hold" ${reg.type == 'hold' ? 'selected="selected"' : ''}>hold</option>
                          <option value="waiting" ${reg.type == 'waiting' ? 'selected="selected"' : ''}>waiting</option>
                        </select>
                        --%>
                      </td>
                      <td class="regComment">
                        <input name='comment<c:out value="${reg.registrationId}"/>' id='comment<c:out value="${reg.registrationId}"/>' type="text" value='<c:out value="${reg.comment}"/>'/>
                      </td>
                      <td class="regCreated">
                        <c:out value="${reg.created}"/>
                      </td>
                      <td class="regControls">
                        <a href="javascript:doUpdateAdminTicket('<c:out value="${reg.registrationId}"/>','<c:out value="${reg.event.href}"/>','${sessMan.adminToken}')" onclick="return confirmUpdateAdminTicket()">update</a> |
                        <a href='adminRemoveAgendaReg.do?regid=<c:out value="${reg.registrationId}"/>&amp;href=<c:out value="${reg.event.href}"/>&amp;atkn=<c:out value="${sessMan.adminToken}"/>' onclick="return confirmRemoveAdminTicket('<c:out value="${reg.authid}"/>')">remove</a>
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