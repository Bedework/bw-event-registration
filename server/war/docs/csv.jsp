<%@ page session="false"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="/spring" %><%--
--%><c:if test="${sessMan.csvHeader}">event,date,time,location,registrationId,userid,email,tickets,numtickets,type,comment,created,lastmod
</c:if
><c:forEach var="reg" items="${regs}" varStatus="loopStatus"><%--
--%>"${reg.event.summary}","${reg.event.date}","${reg.event.time}","${reg.event.location}","${reg.registrationId}","${reg.authid}","${reg.email}","${reg.ticketsRequested}","${reg.numTickets}","${reg.type}","${reg.comment}","${reg.created}","${reg.lastmod}"
</c:forEach>
