<%--********************************************************************
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.

/**
 * Ajax end-point for Bedework admininstrative web client;
 * Provides a list of form names for a calendar suite.
 */
--%>
<%@ page session="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="/spring" %>


{
  "bwforms" : [
    <c:if test="${not empty forms}">
      <c:forEach var="f" items="${forms}" varStatus="loopStatus">
        {
          "name" : "${f.formName}",
          "status" :
          <c:choose>
            <c:when test="${f.disabled}">
              "disabled"
            </c:when>
            <c:when test="${f.committed}">
              "published"
            </c:when>
            <c:otherwise>
              "unpublished"
            </c:otherwise>
          </c:choose>
        }<c:if test="${!loopStatus.last}">,</c:if>
      </c:forEach>
    </c:if>
  ]
}