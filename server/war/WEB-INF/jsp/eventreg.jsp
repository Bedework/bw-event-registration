<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ include file="head.jsp" %>

    <div class="box">
      <%@ include file="/WEB-INF/jsp/userNav.jsp" %>
      <c:choose>
        <c:when test="${sessMan.userInfo.type == 'superuser'}">
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
            tickets has been added. To review or change your order click on
            "<a href="javascript:launchWindow('agenda.do','1000')">view my list</a>".
            Please bring a print out of your list and your Rensselaer ID to the box
            office to pick up your tickets.
            <input type="button" value="ok" id="okbutton" onclick="window.location = 'init.do'"/>
          </p>
          <p>
            <a href="https://celebration.empac.rpi.edu/info/" target="_top">Ticket pickup, policies, and accessibility</a>
          </p>
        </c:otherwise>
      </c:choose>
    </div>

 <%@ include file="foot.jsp" %>