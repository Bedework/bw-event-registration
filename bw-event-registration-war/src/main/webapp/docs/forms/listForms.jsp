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
 * Admin listing of all custom forms
 */
--%><%@ include file="/docs/forms/fdefHead.jspf" %>

<c:if test="${sessMan.adminUser}">
  <div id="content">
    <div id="showDisabledSwitchHolder">
      <%--input type="checkbox" id="hidePendingSwitch" onclick="hidePending(this.checked);"/>
      <label for="hidePendingSwitch">hide pending</label--%>
      <input type="checkbox" id="showDisabledSwitch" onclick="showDisabled(this.checked);"/>
      <label for="showDisabledSwitch">show disabled</label>
    </div>
    <a href="#newCustomCollectionNameDialog" class="button" id="addNewCustomCollection">Add New Custom Field Collection</a>

    <table id="customFields" class="commonTable">
      <thead>
      <tr>
        <th>
          Name
        </th>
        <th>
          Description
        </th>
        <th>
          Status
        </th>
        <th class="actions">
          Actions
        </th>
      </tr>
      </thead>
      <tbody>
        <c:choose>
          <c:when test="${empty forms}">
            <tr class="b">
              <td colspan="5">
                You have no custom field collections
              </td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach var="f" items="${forms}" varStatus="loopStatus">
              <tr class="disabled-${f.disabled} committed-${f.committed}">
                <td>
                  <a href="editForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${f.formName}"><c:out value="${f.formName}"/></a>
                </td>
                <td>
                  ${f.comment}
                </td>
                <td>
                  <c:if test="${f.committed}">
                    published
                  </c:if>
                  <c:if test="${f.disabled}">
                    disabled
                  </c:if>
                </td>
                <td class="actions">
                  <c:choose>
                    <c:when test="${f.committed}">
                      <a href="editForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${f.formName}">
                        <span class="ui-icon ui-icon-document"></span>
                        view
                      </a>
                    </c:when>
                    <c:otherwise>
                      <a href="editForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${f.formName}">
                        <span class="ui-icon ui-icon-pencil"></span>
                        update
                      </a>
                    </c:otherwise>
                  </c:choose>
                  <!-- duplicate-->
                  <c:choose>
                    <c:when test="${f.disabled}">
                      <a href="disableForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${f.formName}&disable=false"
                         id="enableDisable" onclick="checkDisable('enable')">
                        <span class="ui-icon ui-icon-arrowthick-1-n"></span>
                        enable
                      </a>
                    </c:when>
                    <c:otherwise>
                      <a href="disableForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=${f.formName}&disable=true"
                         id="enableDisable" onclick="checkDisable('disable')">
                        <span class="ui-icon ui-icon-close"></span>disable
                      </a>
                    </c:otherwise>
                  </c:choose>
                </td>
              </tr>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </tbody>
    </table>
  </div>

  <script type="text/javascript">
    $(function() {
      $("#addNewCustomCollection").magnificPopup({
        type: 'inline',
        midClick: true,
        focus: '#newFieldCollectionName'
      });
    });

    function doAddFieldCollection() {
      var newCollectionName = $("#newFieldCollectionName").val();
      var newCollectionDesc = $("#newFieldCollectionDesc").val();
      if (!isValid(newCollectionName,'alphanumeric')) {
        $(".validationError").text('Please enter a valid name.');
        $("#newFieldCollectionName").addClass("invalid");
        $("#newFieldCollectionName").focus();
        return false;
      }
      if (newCollectionName != "") {
        $.magnificPopup.close();
        $("#newFieldCollectionName").val(""); // empty the field
        location.href = "addForm.do?calsuite=${req.calsuite}&atkn=${req.adminToken}&formName=" + newCollectionName + "&comment=" + newCollectionDesc;
      }
      return false;
    }
  </script>

  <div id="newCustomCollectionNameDialog" class="bwPopup mfp-hide">
    <form action="#" onsubmit="return doAddFieldCollection()">
      <label for="newFieldCollectionName">Custom Field Collection Name:</label>
      <span class="validationError"></span><br/>
      <input type="text" value="" size="65" id="newFieldCollectionName" tabindex="1"/>
      <button type="submit" class="button" tabindex="3">add</button><br/>
      <p class="note">
        a short name identifying this set of fields for events administration<br/>
        may not include spaces or special characters
      </p>
      <label for="newFieldCollectionDesc">Description: <span class="note">(optional)</span></label><br/>
      <input type="text" value="" size="65" id="newFieldCollectionDesc" tabindex="2"/>
      <p class="note">
        the description will appear in the list of field collections
      </p>
    </form>
  </div>

</c:if>

<%@ include file="/docs/forms/fdefFoot.jspf" %>