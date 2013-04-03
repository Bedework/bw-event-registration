<%@ page session="false"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="/spring" %><%--
--%><%@ page contentType="application/vnd.ms-excel" %><%--
--%><c:if test="${req.csvHeader}">event,date,time,location,registrationId,userid,ticketsRequested,ticketsAllocated,type,comment,created,lastmod
</c:if
><c:forEach var="reg" items="${regs}" varStatus="loopStatus"><%--
--%>"${reg.event.summary}","${reg.event.date}","${reg.event.time}","${reg.event.location}","${reg.registrationId}","${reg.authid}","${reg.ticketsRequested}","${reg.numTickets}","${reg.type}","${reg.comment}","${reg.created}","${reg.lastmod}"
</c:forEach>
