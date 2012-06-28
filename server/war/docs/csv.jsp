<%@ include file="/docs/include.jsp" %>
<%@ page contentType="application/vnd.ms-excel" %>
<%response.setHeader("Content-disposition","attachment; filename=empacOpeningReg.csv"); %>
event,date,time,location,ticketid,email,numtickets,type,comment,eventcreated,userid,fname,lname,street1,street2,city,state,zip,phone,country,usertype
<c:forEach var="reg" items="${registrations}" varStatus="loopStatus"><%--
--%>"<c:out value="${reg.event.summary}"/>","<c:out value="${reg.event.date}"/>","<c:out value="${reg.event.time}"/>","<c:out value="${reg.event.location}"/>","<c:out value="${reg.ticketId}"/>","<c:out value="${reg.email}"/>","<c:out value="${reg.numtickets}"/>","<c:out value="${reg.type}"/>","<c:out value="${reg.comment}"/>","<c:out value="${reg.created}"/>","<c:out value="${reg.authUser}"/>","<c:out value="${reg['fname']}"/>","<c:out value="${reg['lname']}"/>","<c:out value="${reg['street1']}"/>","<c:out value="${reg['street2']}"/>","<c:out value="${reg['city']}"/>","<c:out value="${reg['state']}"/>","<c:out value="${reg['zip']}"/>","<c:out value="${reg['phone']}"/>","<c:out value="${reg['country']}"/>","<c:out value="${reg['usertype']}"/>"
</c:forEach>
