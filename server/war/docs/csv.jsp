<%@ page session="false"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="/spring" %><%--
--%><%@ page contentType="application/vnd.ms-excel" %><%--
--%><% response.setHeader("Content-disposition","attachment; filename=EventReg-" + "1" +  ".csv"); %><%-- // fix this to include something meaningful
--%>event,date,time,location,registrationId,userid,ticketsRequested,ticketsAllocated,type,comment,created,lastmod
<c:forEach var="reg" items="${regs}" varStatus="loopStatus"><%--
--%>"<c:out value="${reg.event.summary}"/>","<c:out value="${reg.event.date}"/>","<c:out value="${reg.event.time}"/>","<c:out value="${reg.event.location}"/>","<c:out value="${reg.registrationId}"/>","<c:out value="${reg.authid}"/>","<c:out value="${reg.ticketsRequested}"/>","<c:out value="${reg.numTickets}"/>","<c:out value="${reg.type}"/>","<c:out value="${reg.comment}"/>","<c:out value="${reg.created}"/>","<c:out value="${reg.lastmod}"/>"
</c:forEach>
