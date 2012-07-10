<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <h3>Register for this event</h3>
  <c:choose>
    <c:when test="${sessMan.isRegistered}">
      <div class="box" id="registered">
        <%@ include file="/docs/userNav.jsp" %>
        <p class="registered">
          <span class="checkmark">&#x2713;</span> You are registered for this event.
        </p>
        <p class="unregister">
          <a href="removeTicket.do?ticketid=<c:out value="${sessMan.registration.ticketid}"/>&amp;href=${sessMan.href}" onclick="return confirmRemoveTicket('<c:out value="${sessMan.currEvent.summary}')"/>">unregister</a>
        </p>
      </div>
    </c:when>
    <c:when test="${sessMan.isWaiting}">
      <div class="box" id="registered">
        <%@ include file="/docs/userNav.jsp" %>
        <p class="waiting">
          <span class="checkmark">&#x2713;</span> You are on the waiting list.
        </p>
        <p class="details">
          You will be automatically registered if space becomes available.
        </p>
        <p class="unregister">
          <a href="removeTicket.do?ticketid=<c:out value="${sessMan.registration.ticketid}"/>&amp;href=${sessMan.href}" onclick="return confirmRemoveTicket('<c:out value="${sessMan.currEvent.summary}"/>')">remove me</a>
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
        <form action="waitlist.do" class="commonForm" method="POST">
          <c:choose>
            <c:when test="${sessMan.currEvent.maxTicketsPerUser > 1}">
              Tickets:
              <select id="numtickets" name="numtickets">
                <c:forEach var="i" begin="1" end="${sessMan.currEvent.maxTicketsPerUser}">
                   <option value="${i}">${i}</option>
                </c:forEach>
              </select>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="numtickets" value="1"/>
            </c:otherwise>
          </c:choose>
          <input type="hidden" name="href" value="${sessMan.href}"/>
          <input type="submit" value="Register for waiting list"/>
        </form>
      </div>
    </c:when>
    <c:when test="${sessMan.superUser}">
      <%-- Form for superuser - DEPRECATE? --%>
      <form action="eventreg.do" class="commonForm" method="POST">
        <div class="box">
          <%@ include file="/docs/userNav.jsp" %>
          <p class="superuserForm">
            Tickets: <input type="text" name="numtickets" id="numtickets" size="3" value=""/>
            <script type="text/javascript">
              formelement('numtickets');
            </script>
            <select name="type">
              <option>registered</option>
              <option>hold</option>
              <option>waiting</option>
            </select><br/>
            Comment: <input type="text" value="" size="10" name="comment" id="comment"/>
            <script type="text/javascript">
              formelement('comment');
            </script><br/>
            <input type="submit" value="Register"/>
          </p>
        </div>
      </form>
    </c:when>
    <c:otherwise>
      <%-- Normal authenticated user --%>
      <div class="box">
        <%@ include file="/docs/userNav.jsp" %>
        <div class="ticketVals">
          <!-- em>Tickets</em><br/-->
          Total: ${sessMan.currEvent.maxTickets}<br/>
          Available: ${sessMan.currEvent.maxTickets - sessMan.ticketCount}
        </div>
        <form action="eventreg.do" class="commonForm" method="POST">
          <c:choose>
            <c:when test="${sessMan.currEvent.maxTicketsPerUser > 1}">
              Tickets:
              <select id="numtickets" name="numtickets">
                <c:forEach var="i" begin="1" end="${sessMan.currEvent.maxTicketsPerUser}">
                   <option value="${i}">${i}</option>
                </c:forEach>
              </select>
            </c:when>
            <c:otherwise>
              <input type="hidden" name="numtickets" value="1"/>
            </c:otherwise>
          </c:choose>
          <input type="hidden" name="href" value="${sessMan.href}"/>
          <!-- input type="hidden" name="type" value="registered"/-->
          <input type="submit" value="Register"/>
        </form>
      </div>
    </c:otherwise>
  </c:choose>

 <%@ include file="foot.jsp" %>