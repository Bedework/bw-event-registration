<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <div class="fullpage">
      <div class="rightLinks">
        Welcome <c:out value="${globals.currentUser}"/> | <a href="logout.do">logout</a><br/>
        <c:set var="fileName" scope="page">EventReg-<c:out value="${fn:substring(fn:replace(globals.currentEvent.summary, ' ',''),0,9)}"/>-<c:out value="${fn:substring(globals.currentEvent.dateTime,0,7)}"/>.csv</c:set>
        <a href="download.do?href=${req.href}&amp;fn=${fileName}&amp;calsuite=${globals.calsuite}&amp;formName=${globals.formName}">download registrations</a> |
        <a href="listForms.do?calsuite=${globals.calsuite}">manage custom fields</a> |
        <a href="javascript:print();">print</a> |
        <a href="showRegistrations.do?href=${globals.href}">refresh</a>
      </div>
      <h1><c:out value="${globals.currentEvent.summary}"/></h1>
      <div class="eventDateTime">
        <c:set var="eventDate" scope="page" value="${globals.currentEvent.dateTime}"/>
        Start: ${fn:substring(eventDate,-1,4)}-${fn:substring(eventDate,4,6)}-${fn:substring(eventDate,6,8)}
        ${fn:substring(eventDate,11,13)}:${fn:substring(eventDate,13,15)}
        (${fn:substring(eventDate,18,-1)})
      </div>
      <form id="holdTickets" action="hold.do">
        Hold tickets: <input name="numtickets" value="" size="2"/>
        <input type="submit" value="hold"/><br/>
        Comment: <input name="comment" placeholder='e.g. "reserved for VIP"' size="22"/>
        <input type="hidden" name="href" value="${req.href}"/>
      </form>
      <div id="regInfo">
        <p class="left">
          Fulfilled tickets:  <c:out value="${globals.regInfo.ticketCount}"/><br/>
          Waiting tickets: <c:out value="${globals.regInfo.waitingTicketCount}"/><br/>
          Wait list limit:
          <c:choose>
            <c:when test="${empty globals.currentEvent.waitListLimit}"><br/>
              <c:out value="unlimited"/>
            </c:when>
            <c:otherwise>
              <c:out value="${globals.currentEvent.waitListLimit}"/><br/>
            </c:otherwise>
          </c:choose>
          Registrations: <c:out value="${globals.regInfo.registrantCount}"/>
        </p>
        <p class="left">
          Max tickets allowed for event: <c:out value="${globals.currentEvent.maxTickets}"/><br/>
          Max tickets allowed per user: <c:out value="${globals.currentEvent.maxTicketsPerUser}"/><br/>
          Available tickets:
          <c:set var="availableTickets" scope="page" value="${globals.regInfo.availableTickets}"/>
          <c:choose>
            <c:when test="${availableTickets < 0}">
              0
            </c:when>
            <c:otherwise>
              <c:out value="${availableTickets}"/>
            </c:otherwise>
          </c:choose>
        </p>
        <p>
          Registration starts:
          <c:set var="regStarts" scope="page" value="${globals.currentEvent.registrationStart}"/>
          ${fn:substring(regStarts,-1,4)}-${fn:substring(regStarts,4,6)}-${fn:substring(regStarts,6,8)}
          ${fn:substring(regStarts,9,11)}:${fn:substring(regStarts,12,14)}
          <br/>
          Registration ends:
          <c:set var="regEnds" scope="page" value="${globals.currentEvent.registrationEnd}"/>
          ${fn:substring(regEnds,-1,4)}-${fn:substring(regEnds,4,6)}-${fn:substring(regEnds,6,8)}
          ${fn:substring(regEnds,9,11)}:${fn:substring(regEnds,12,14)}
          <c:if test="${availableTickets < 0}">
            <c:set var="overallocation" scope="page" value="${globals.regInfo.maxTicketCount - globals.currentEvent.maxTickets}"/>
            <br/>
            <span class="overallocationNotice">
              You are overallocated by <c:out value="${overallocation}"/>
              ticket<c:if test="${overallocation > 1}">s</c:if>
            </span>
          </c:if>
          <c:if test="${not empty globals.formName}">
            <br/>Custom fields:
            <a href="editForm.do?calsuite=${globals.calsuite}&formName=${globals.formName}">${globals.formName}</a>
          </c:if>
        </p>
      </div>
      <form name="registrationList" action="">
        <c:if test="${not empty globals.message}">
          <div id="message">
            <c:out value="${globals.message}"/>
          </div>
        </c:if>
        <table id="registrationList" cellspacing="2" class="tablesorter">
        <thead>
          <tr>
            <%-- thText classes are needed to keep the table sorter
                 background image from overlapping the text. --%>
            <th>
              <span class="thText">
                ID
              </span>
            </th>
            <th>
              <span class="thText">
                State
              </span>
            </th>
            <th>
              <span class="thText">
                UserID
              </span>
            </th>
            <th>
              <span class="thText">
                Tickets<br/>Requested
              </span>
            </th>
            <th>
              <span class="thText">
                Tickets<br/>Allocated
              </span>
            </th>
            <th>
              <span class="thText">
                Created
              </span>
            </th>
            <%-- XXX expose the fields here
            <th>
              <span class="thText">
                Custom Fields
              </span>
            </th>
            --%>
            <th>
              <span class="thText">
                Comment
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
                  <td colspan="9">
                    No tickets reserved
                  </td>
                </tr>
              </c:when>

              <c:otherwise>
                <c:forEach var="reg" items="${regs}" varStatus="loopStatus">
                  <c:choose>
                    <c:when test="${reg.type == 'hold'}">
                      <tr class="hold">
                    </c:when>
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
                    </td>
                    <td class="regAuthId">
                      <c:choose>
                        <c:when test="${reg.type == 'hold'}">
                          <em>(administrator)</em>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${reg.authid}"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="regRequestedTickets">
                      <input name="numtickets<c:out value="${reg.registrationId}"/>" id="numtickets<c:out value="${reg.registrationId}"/>" type="text" value="<c:out value="${reg.ticketsRequested}"/>" size="3"/>
                    </td>
                    <td class="regNumTickets">
                      <c:out value="${reg.numTickets}"/>
                    </td>
                    <td class="regCreated">
                      <c:out value="${fn:substring(reg.created,-1,16)}"/>
                    </td>
                    <%--
                    <td class="regCustomFields">
                      *TBD*
                    </td>
                    --%>
                    <td class="regComment">
                      <input name="comment<c:out value="${reg.registrationId}"/>" id="comment<c:out value="${reg.registrationId}"/>" type="text" value="<c:out value="${reg.comment}"/>" size="45"/>
                    </td>
                    <td class="regControls">
                      <a href="javascript:doUpdateAdminTicket('<c:out value="${reg.registrationId}"/>','<c:out value="${reg.event.href}"/>','${globals.calsuite}','${globals.formName}')" onclick="return confirmUpdateAdminTicket()">update</a> |
                      <a href='removeReg.do?regid=<c:out value="${reg.registrationId}"/>&amp;href=<c:out value="${reg.event.href}"/>"/>' onclick="return confirmRemoveAdminTicket('<c:out value="${reg.authid}"/>','<c:out value="${reg.type}"/>')">remove</a>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </form>
    </div>

 <%@ include file="foot.jsp" %>