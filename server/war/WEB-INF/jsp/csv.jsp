<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ page contentType="application/vnd.ms-excel" %>
<%response.setHeader("Content-disposition","attachment; filename=empacOpeningReg.csv"); %>
event,date,time,location,ticketid,empacid,email,numtickets,type,comment,eventcreated,userid,rcsid,rin,fname,lname,street1,street2,city,state,zip,phone,country,usertype,rpi
<c:forEach var="reg" items="${registrations}" varStatus="loopStatus"><%--
--%>"<c:out value="${reg['event']}"/>","<c:out value="${reg['date']}"/>","<c:out value="${reg['time']}"/>","<c:out value="${reg['location']}"/>","<c:out value="${reg['ticketid']}"/>","<c:out value="${reg['empacid']}"/>","<c:out value="${reg['email']}"/>","<c:out value="${reg['numtickets']}"/>","<c:out value="${reg['type']}"/>","<c:out value="${reg['comment']}"/>","<c:out value="${reg['eventcreated']}"/>","<c:out value="${reg['userid']}"/>","<c:out value="${reg['rcsid']}"/>","<c:out value="${reg['rin']}"/>","<c:out value="${reg['fname']}"/>","<c:out value="${reg['lname']}"/>","<c:out value="${reg['street1']}"/>","<c:out value="${reg['street2']}"/>","<c:out value="${reg['city']}"/>","<c:out value="${reg['state']}"/>","<c:out value="${reg['zip']}"/>","<c:out value="${reg['phone']}"/>","<c:out value="${reg['country']}"/>","<c:out value="${reg['usertype']}"/>","<c:out value="${reg['rpi']}"/>"
</c:forEach>
