<%@ include file="/docs/include.jsp" %>

<%@ include file="/docs/head.jsp" %>

  <c:choose>
    <c:when test="${sessMan.deadlinePassed}">
      <div class="box" id="deadlinePassed">
        <%@ include file="/docs/userNav.jsp" %>
        Online registration is closed for this event.
        We encourage you to come to the EMPAC Box Office a half hour before the event.
        Unclaimed tickets will be distributed
        on a first-come first-served basis. For more information about ticket
        availability, please call 518-276-3921.
      </div>
    </c:when>
    <c:when test="${sessMan.registrationFull}">
      <div class="box" id="registrationFull">
        <%@ include file="/docs/userNav.jsp" %>
        This event is reserved to capacity!  Additional tickets may
            be available through the box office on the day of the show.
            Starting a half hour before the event, unclaimed tickets will be distributed
            on a first-come first-served basis.
        Questions? 518-276-3921
        or <a href="mailto:empacboxoffice@rpi.edu">empacboxoffice@rpi.edu</a>.
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
            <input type="submit" value="Request Tickets"/>
          </form>
        </div>
      </form>
    </c:otherwise>
  </c:choose>

 <%@ include file="foot.jsp" %>