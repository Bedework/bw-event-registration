<%@ page session="false"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%--
--%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%--
--%><%@ page contentType="application/vnd.ms-excel" %><%--
--%><c:if test="${req.csvHeader}"><c:out value="${csv.header}"/>
</c:if
        ><c:forEach var="rec" items="${csv}" varStatus="loopStatus"><%--
--%><c:out value="${rec}" escapeXml="false"/>
</c:forEach>
