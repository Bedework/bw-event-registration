<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <div class="box">
      <%@ include file="/docs/userNav.jsp" %>
      <p>
        Thank you! Your request for <strong><c:out value="${req.ticketsRequested}"/></strong>
        tickets has been added. To review or change your registration click on
        "<a href="javascript:launchWindow('showRegistrations.do','1000')">view my list</a>".
        <input type="button" value="ok" id="okbutton" onclick="window.location = 'init.do?href=${globals.href}&calsuite=${globals.currentCalsuite}'"/>
      </p>
    </div>

 <%@ include file="foot.jsp" %>