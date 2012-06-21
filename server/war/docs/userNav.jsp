<%@ include file="/docs/include.jsp" %>

      <div class="rightLinks">
        <c:choose>
          <c:when test="${sessMan.authenticated}">
            <c:choose>
              <c:when test="${sessMan.userInfo.type == 'superuser'}">
                Welcome <c:out value="${sessMan.userInfo.fname}"/><br/>
                <c:out value="${sessMan.userInfo.email}"/> (superuser)<br/>
                <a href="javascript:launchWindow('suagenda.do', '1000')">view event's agenda</a><br/>
                <a href="download.do">download registrations</a>
              </c:when>
              <c:otherwise>
                Welcome <c:out value="${sessMan.userInfo.fname}"/> (<c:out value="${sessMan.userInfo.email}"/>)<br/>
                <a href="javascript:launchWindow('agenda.do','1000')">view my list</a>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <a href="signin.do">sign in</a><%--  | <a href="javascript:launchWindow('userreg.do', '600')">register</a>  --%>
          </c:otherwise>
        </c:choose>
      </div>
