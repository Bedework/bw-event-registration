<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <c:choose>
    <c:when test="${sessMan.deadlinePassed}">
      <div class="box" id="deadlinePassed">
        <%@ include file="/docs/userNav.jsp" %>
        Registration is closed.
      </div>
    </c:when>
    <c:when test="${sessMan.registrationFull}">
      <div class="box" id="registrationFull">
        <%@ include file="/docs/userNav.jsp" %>
        Registration is full.  
      </div>
    </c:when>
    <c:when test="${sessMan.superUser}">
      <%-- Form for superuser --%>
      <form action="eventreg.do" method="POST">
        <div class="box">
          <%@ include file="/docs/userNav.jsp" %>
          <p class="superuserForm">
            Tickets: <input type="text" name="numtickets" id="numtickets" size="3" value=""/>
            <script type="text/javascript">
              formelement('numtickets');
            </script>
            <select name="regType">
              <option>hold</option>
              <option>normal</option>
            </select><br/>
            Comment: <input type="text" value="" size="10" name="comment" id="comment"/>
            <script type="text/javascript">
              formelement('comment');
            </script><br/>
            <input type="submit" value="Request Tickets"/>
          </p>
        </div>
      </form>
    </c:when>
    <c:otherwise>
      <%-- Normal authenticated user --%>
      <form action="eventreg.do" method="POST">
        <div class="box">
          <%@ include file="/docs/userNav.jsp" %>
          <form>
            Tickets:
            <select id="numtickets" name="numtickets">
              <c:forEach var="i" begin="1" end="${sessMan.currEvent.maxTicketsPerUser}">
                 <option value="${i}">${i}</option>
              </c:forEach>
            </select>
            <input type="hidden" name="href" value="${sessMan.href}"/>
            <input type="submit" value="Request Tickets"/>
          </form>
        </div>
      </form>
    </c:otherwise>
  </c:choose>

 <%@ include file="foot.jsp" %>