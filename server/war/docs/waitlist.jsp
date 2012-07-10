<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

    <div class="box">
      <%@ include file="/docs/userNav.jsp" %>
      <p>
        Thank you! Your request has been added to the waiting list. To review or change your registration click on
        "<a href="javascript:launchWindow('agenda.do','1000')">view my list</a>".
        <input type="button" value="ok" id="okbutton" onclick="window.location = 'init.do?href=${sessMan.href}'"/>
      </p>
    </div>

 <%@ include file="foot.jsp" %>