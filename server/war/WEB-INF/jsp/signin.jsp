<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="head.jsp" %>

    <form action="signin.do" id="signin" method="POST">

      <div class="box">
        <%--
        <div class="rightLinks">
          <a href="javascript:launchWindow('userreg.do','600')">register</a>
        </div>
        --%>

        <c:if test="${sessMan.message != null and sessMan.message != ''}">
          <div id="message">
            <c:out value="${sessMan.message}" escapeXml="false"/>
          </div>
        </c:if>
        <table>
          <tr>
            <td>
              RCS ID:
            </td>
            <td>
              <input type="text" name="rcsid" id="rcsid" value="" />
              <script type="text/javascript">
                formelement('rcsid');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              Password:
            </td>
            <td>
              <input type="password" name="password" id="password" value="" />
              <script type="text/javascript">
                formelement('password');
              </script>
            </td>
          </tr>
          <tr>
            <td>
            </td>
            <td>
              <input type="submit" value="Sign In" class="submit"/>
            </td>
          </tr>
        </table>
      </div>

      <c:if test="${!empty signonForwardAction}">
        <input type="hidden" name="forwardAction" value="<c:url value="${signonForwardAction}"/>"/>
      </c:if>

    </form>


<%@ include file="foot.jsp" %>



