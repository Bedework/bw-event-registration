<%@ include file="/docs/include.jsp" %>

      <div class="rightLinks">
        <c:choose>
          <c:when test="${sessMan.adminUser}">
            Welcome <c:out value="${sessMan.currentUser}"/><br/>
            <a href="javascript:launchWindow('adminagenda.do', '1000')">view event's agenda</a><br/>
            <a href="download.do">download registrations</a>
          </c:when>
          <c:otherwise>
            Welcome <c:out value="${sessMan.currentUser}"/> <br/>
            <a href="javascript:launchWindow('agenda.do','1000')">view my list</a>
          </c:otherwise>
        </c:choose>
      </div>
