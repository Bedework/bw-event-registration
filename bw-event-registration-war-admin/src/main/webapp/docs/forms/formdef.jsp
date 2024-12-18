<%--******************************************************
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
 * Custom field collection definition (form definiton)
 * Lists all fields for the collection
 */
--%><%@ include file="/docs/forms/fdefHead.jspf" %>

<div id="content">
  <c:if test="${not empty form}">
    <div id="formNameDesc">
      <h2 class="formName">
        <c:out value="${form.formName}"/>
        <span class="descriptor">(field collection<c:if test="${form.committed}">, published</c:if>)</span>
      </h2>
      <div id="formDescContainer">
        ${form.comment}
      </div>
    </div>
    <table id="customFields" class="commonTable">
      <thead>
      <tr>
        <th>
          Name
        </th>
        <th>
          Label
        </th>
        <th>
          Type
        </th>
        <th>
          Value
        </th>
        <th>
          Required
        </th>
        <th>
          Order
        </th>
        <c:if test="${not form.committed}">
          <th class="actions">
            Actions
          </th>
        </c:if>
      </tr>
      </thead>
      <tbody>
      <c:choose>
        <c:when test="${empty fields}">
          <tr class="b">
            <td colspan="7">
              You have no custom fields
            </td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach var="fld" items="${fields}" varStatus="loopStatus">
            <tr id="${fld.name}">
              <td class="fldName">
                <span class="ui-icon ui-icon-arrow-4"></span>
                <c:out value="${fld.name}"/>
              </td>
              <td class="fldLabel">
                <c:out value="${fld.label}"/>
              </td>
              <c:choose>
                <c:when test="${(fld.type eq 'select') or (fld.type eq 'radio')}">
                  <td class="fldType">
                    options list
                    <span class="note">(<c:out value="${fld.type}"/>)</span>
                  </td>
                  <td class="fldValue optionsList">
                    <div class="optionsDisplay">
                      <table>
                        <tr>
                          <th>values</th>
                          <th>labels</th>
                        </tr>
                        <c:choose>
                          <c:when test="${empty fields.options}">
                            <tr><td colspan="2">no options</td></tr>
                          </c:when>
                          <c:otherwise>
                            <c:forEach var="opt" items="${fields.options}" varStatus="optLoopStatus">
                              <tr>
                                <td>
                                  <c:out value="${opt.value}"/>
                                  <c:if test="${opt.value eq fld.value}">
                                    <span class="note">(default)</span><br/>
                                  </c:if>
                                </td>
                                <td><c:out value="${opt.label}"/></td>
                              </tr>
                            </c:forEach>
                          </c:otherwise>
                        </c:choose>
                      </table>
                    </div>
                  </td>
                </c:when>
                <c:otherwise>
                  <td class="fldType">
                    <c:out value="${fld.type}"/>
                  </td>
                  <td class="fldValue">
                    <c:out value="${fld.value}"/>
                  </td>
                </c:otherwise>
              </c:choose>
              <td>
                <c:out value="${fld.required}"/>
              </td>
              <td>
                <c:out value="${fld.order}"/>
                <%-- if this is the last record, grab the order and throw it in the new field form + 100 --%>
                <%-- XXX when we stop refreshing the page, we'll need to set this in the setupNewFieldForm() function as well --%>
                <c:if test="${loopStatus.last}">
                  <c:set var="nextOrder" value="${fld.order}+100"/>
                  <script type="text/javascript">
                    $(function() {
                      $("#fieldOrder").val(${nextOrder});
                    });
                  </script>
                </c:if>
              </td>
              <c:if test="${not form.committed}">
                <td class="actions">
                  <!--update -->
                  <a href="deleteField.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${form.formName}&name=${fld.name}" data-fldName="${fld.name}" class="deleteLink">
                    <span class="ui-icon ui-icon-close"></span>
                    delete
                  </a>
                </td>
              </c:if>
            </tr>
          </c:forEach>
        </c:otherwise>
      </c:choose>
      </tbody>
    </table>

    <c:choose>
      <c:when test="${form.committed}">
        <div class="actionButtons"><a class="button" href="listForms.do?calsuite=${globals.calsuite}">List Custom Field Collections</a></div>
      </c:when>
      <c:otherwise>
        <div class="actionButtons">
          <a id="bwPublishAndLock" class="button"
           href="commitForm.do?calsuite=${globals.calsuite}&formName=${form.formName}"
           onclick="return confirmPublish();">Publish &amp; Lock</a>

          <a class="button" href="listForms.do?calsuite=${globals.calsuite}">List Custom Field Collections</a>
        </div>
        <div id="bwAddNewFld">
          Add new:
          <select id="addNewFieldType">
            <option value="textinput">text input</option>
            <option value="radio">options list</option>
            <option value="checkbox">checkbox</option>
          </select>
          <a href="#modFieldContainer" id="addNewFieldButton" class="button">add</a>
        </div>
      </c:otherwise>
    </c:choose>

    <%@ include file="/docs/forms/formdefJavaScript.jspf" %>

    <%@ include file="/docs/forms/formdefFieldForm.jspf" %>

  </c:if>

</div>

<%@ include file="/docs/forms/fdefFoot.jspf" %>