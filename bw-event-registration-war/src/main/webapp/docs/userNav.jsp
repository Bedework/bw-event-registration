<%@ include file="/docs/include.jsp" %>

      <div class="rightLinks">
        Welcome <c:out value="${globals.currentUser}"/> | <a href="logout.do">logout</a><br/>
        <a href="javascript:launchWindow('showRegistrations.do','1000')">view my list</a>
      </div>
