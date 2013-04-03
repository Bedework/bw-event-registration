<%@ page session="false"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ taglib prefix="spring" uri="/spring" %><%--
--%><c:if test="${req.csvHeader}">registrationId,account,lastmod,type,name,value
</c:if
><c:forEach var="c" items="${changes}" varStatus="loopStatus"><%--
--%>"${c.registrationId}","${c.authid}","${c.lastmod}","${c.type}","${c.name}","${c.value}"
</c:forEach>
