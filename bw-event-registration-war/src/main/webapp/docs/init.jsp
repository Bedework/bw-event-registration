<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <h3>Register for this event</h3>
  <c:choose>
    <c:when test="${sessMan.isWaiting}">
      <div class="box" id="registered">
        <%@ include file="/docs/userNav.jsp" %>
        <p class="waiting">
          <span class="checkmark">&#x2713;</span> You are on the waiting list.
        </p>
        <p class="details">
          Your tickets will be allocated if space becomes available.
        </p>
        <p class="unregister">
          <a href="removeReg.do?regid=<c:out value="${sessMan.registration.registrationId}"/>&amp;href=${req.href}&calsuite=${sessMan.currentCalsuite}&amp;name=${sessMann.formDef.formName}&amp;email=${sessMan.currEmail}" onclick="return confirmRemoveTicket('<c:out value="${sessMan.currEvent.summary}"/>')">remove me</a>
        </p>
      </div>
    </c:when>
    <c:when test="${sessMan.isRegistered}">
      <div class="box" id="registered">
        <%@ include file="/docs/userNav.jsp" %>
        <p class="registered">
          <span class="checkmark">&#x2713;</span> You are registered for this event.
        </p>
        <p class="unregister">
          <a href="removeReg.do?regid=<c:out value="${sessMan.registration.registrationId}"/>&amp;href=${req.href}&calsuite=${sessMan.currentCalsuite}&amp;name=${sessMann.formDef.formName}&amp;email=${sessMan.currEmail}" onclick="return confirmRemoveTicket('<c:out value="${sessMan.currEvent.summary}')"/>">unregister</a>
        </p>
      </div>
    </c:when>
    <c:when test="${sessMan.deadlinePassed}">
      <div class="box" id="deadlinePassed">
        <%@ include file="/docs/userNav.jsp" %>
        Registration is closed.
      </div>
    </c:when>
    <c:when test="${sessMan.registrationFull}">
      <div class="box" id="registrationFull">
        <%@ include file="/docs/userNav.jsp" %>
        Registration is full.<br/>
        <form action="eventreg.do" class="commonForm" method="POST">
          <input type="hidden" name="href" value="${req.href}"/>
          <input type="hidden" name="calsuite" value="${sessMann.currentCalsuite}"/>
          <input type="hidden" name="name" value="${sessMann.formDef.formName}"/>
          <input type="hidden" name="email" value="${sessMan.currEmail}"/>
          <c:choose>
            <c:when test="${sessMan.currEvent.maxTicketsPerUser > 1}">
              <div id="bwNumtickets">
                Tickets:
                <select id="numtickets" name="numtickets">
                  <c:forEach var="i" begin="1" end="${sessMan.currEvent.maxTicketsPerUser}">
                     <option value="${i}">${i}</option>
                  </c:forEach>
                </select>
              </div>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="numtickets" value="1"/>
            </c:otherwise>
          </c:choose>

          <%@ include file="/docs/forms/customFieldsForReg.jspf" %>

          <input type="submit" id="joinWaiting" class="button" value="Join waiting list"/>
        </form>
      </div>
    </c:when>
    <c:otherwise>
      <div class="box">
        <div id="regheadBox">
          <%@ include file="/docs/userNav.jsp" %>
          <div class="ticketVals">
            <!-- em>Tickets</em><br/-->
            Total: ${sessMan.currEvent.maxTickets}<br/>
            Available: ${sessMan.currEvent.maxTickets - sessMan.regTicketCount}
          </div>
        </div>
        <form action="eventreg.do" class="commonForm" method="POST">
          <input type="hidden" name="href" value="${req.href}"/>
          <input type="hidden" name="calsuite" value="${sessMann.currentCalsuite}"/>
          <input type="hidden" name="name" value="${sessMann.formDef.formName}"/>
          <input type="hidden" name="email" value="${sessMan.currEmail}"/>
          <c:choose>
            <c:when test="${sessMan.currEvent.maxTicketsPerUser > 1}">
              <div id="bwNumtickets">
                Tickets:
                <select id="numtickets" name="numtickets">
                  <c:forEach var="i" begin="1" end="${sessMan.currEvent.maxTicketsPerUser}">
                     <option value="${i}">${i}</option>
                  </c:forEach>
                </select>
              </div>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="numtickets" value="1"/>
            </c:otherwise>
          </c:choose>

          <%@ include file="/docs/forms/customFieldsForReg.jspf" %>

          <input type="submit" id="register" class="button" value="Register"/>
        </form>
      </div>
    </c:otherwise>
  </c:choose>

 <%@ include file="foot.jsp" %>