<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ include file="head.jsp" %>

  <div class="box">
    <h2>Please confirm your registration</h2>
    <p>You are almost ready to select tickets.  Please check your
    email for our confirmation notice and click the
    link within it to complete the registration process.</p>
    <p><a href="resend.do?userid=<c:out value="${sessMan.userInfo.userid}"/>">Resend email</a>.</p>
  </div>
 <%@ include file="foot.jsp" %>