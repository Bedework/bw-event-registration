<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <div class="box">
      <%@ include file="/docs/userNav.jsp" %>
      <c:choose>
        <c:when test="${sessMan.superUser}">
          <p>
            Your request for
            <strong><c:out value="${sessMan.ticketsRequested}"/></strong>
            tickets has been added to the
            <a href="javascript:launchWindow('suagenda.do','1000')">event's agenda</a>.
          </p>
        </c:when>
        <c:otherwise>
          <p>
            Thank you! Your request for <strong><c:out value="${sessMan.ticketsRequested}"/></strong>
            tickets has been added. To review or change your registration click on
            "<a href="javascript:launchWindow('agenda.do','1000')">view my list</a>".
            <input type="button" value="ok" id="okbutton" onclick="window.location = 'init.do'"/>
          </p>
        </c:otherwise>
      </c:choose>
    </div>

 <%@ include file="foot.jsp" %>