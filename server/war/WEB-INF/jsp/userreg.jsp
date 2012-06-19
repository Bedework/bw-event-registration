
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%@ include file="head.jsp" %>

    <div id="userReg">
      <h2>EMPAC Registration</h2>
      <p>Please enter your registration information:</p>

      <form name="userregForm" method="post" action="" onsubmit="return validate(this)">
        <table>
          <tr>
            <td>
              *Email address:
            </td>
            <td>
              <spring:bind path="user.email">
                <input type="text" name="email" id="email" value="<c:out value="${status.value}"/>" />
                <c:if test="${status.error}">
                  <ul class="errors">
                    <c:forEach var="errorMessage" items="${status.errorMessages}">
                      <li>${errorMessage}</li>
                    </c:forEach>
                  </ul>
                </c:if>
              </spring:bind>
              <script type="text/javascript">
                formelement('email');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              *Password:
            </td>
            <td>
              <spring:bind path="user.password">
                <input type="password" name="password" id="password" value="<c:out value="${status.value}"/>" />
                <c:if test="${status.error}">
                  <ul class="errors">
                    <c:forEach var="errorMessage" items="${status.errorMessages}">
                      <li>${errorMessage}</li>
                    </c:forEach>
                  </ul>
                </c:if>
              </spring:bind>
              <script type="text/javascript">
                formelement('password');
              </script>
            </td>
          </tr>
          <tr>
            <td colspan="2">&#160;</td>
          </tr>
          <tr>
            <td>
              *First Name:
            </td>
            <td>
              <spring:bind path="user.fname">
                <input type="text" name="fname" id="fname" value="<c:out value="${status.value}"/>" />
                <c:if test="${status.error}">
                  <ul class="errors">
                    <c:forEach var="errorMessage" items="${status.errorMessages}">
                      <li>${errorMessage}</li>
                    </c:forEach>
                  </ul>
                </c:if>
              </spring:bind>
              <script type="text/javascript">
                formelement('fname');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              *Last Name:
            </td>
            <td>
              <spring:bind path="user.lname">
                <input type="text" name="lname" id="lname" value="<c:out value="${status.value}"/>" />
                <c:if test="${status.error}">
                  <ul class="errors">
                    <c:forEach var="errorMessage" items="${status.errorMessages}">
                      <li>${errorMessage}</li>
                    </c:forEach>
                  </ul>
                </c:if>
              </spring:bind>
              <script type="text/javascript">
                formelement('lname');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              &#160;
            </td>
            <td>
              <table id="rpiTable" border="0" cellspacing="0">
                <tr>
                  <td>
                    <spring:bind path="user.RPI">
                      <input type="hidden" name="_<c:out value="${status.expression}"/>">
                      <input type="checkbox" id="rpi" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>/>
                    </spring:bind>
                    <script type="text/javascript">
                      formelement('rpi');
                    </script>
                  </td>
                  <td>
                    I am a Rensselaer student, faculty, or staff member
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td colspan="2">&#160;</td>
          </tr>
          <tr>
            <td>
              Phone:
            </td>
            <td>
              <spring:bind path="user.phone">
                <input type="text" name="phone" id="phone" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('phone');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              Street Address 1:
            </td>
            <td>
              <spring:bind path="user.street1">
                <input type="text" name="street1" id="street1" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('street1');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              Street Address 2:
            </td>
            <td>
              <spring:bind path="user.street2">
                <input type="text" name="street2" id="street2" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('street2');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              City:
            </td>
            <td>
              <spring:bind path="user.city">
                <input type="text" name="city" id="city" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('city');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              State:
            </td>
            <td>
              <spring:bind path="user.state">
                <input type="text" name="state" id="state" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('state');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              Postal Code:
            </td>
            <td>
              <spring:bind path="user.zip">
                <input type="text" name="zip" id="zip" value="<c:out value="${status.value}"/>" />
              </spring:bind>
              <script type="text/javascript">
                formelement('zip');
              </script>
            </td>
          </tr>
          <tr>
            <td>
              Country:
            </td>
            <td>
              <spring:bind path="user.country">
                <select name="country" id="country">
                  <option value="US United States">US   United States</option>
                  <option value="AD Andorra">AD   Andorra</option>
                  <option value="AE United Arab Emirates">AE   United Arab Emirates</option>
                  <option value="AF Afghanistan">AF   Afghanistan</option>
                  <option value="AG Antigua and Barbuda">AG   Antigua and Barbuda</option>
                  <option value="AI Anguilla">AI   Anguilla</option>
                  <option value="AL Albania">AL   Albania</option>
                  <option value="AM Armenia">AM   Armenia</option>
                  <option value="AN Netherlands Antilles">AN   Netherlands Antilles</option>
                  <option value="AO Angola">AO   Angola</option>
                  <option value="AQ Antarctica">AQ   Antarctica</option>
                  <option value="AR Argentina">AR   Argentina</option>
                  <option value="AS American Samoa">AS   American Samoa</option>
                  <option value="AT Austria">AT   Austria</option>
                  <option value="AU Australia">AU   Australia</option>
                  <option value="AW Aruba">AW   Aruba</option>
                  <option value="AX Aland Islands">AX   Aland Islands</option>
                  <option value="AZ Azerbaijan">AZ   Azerbaijan</option>
                  <option value="BA Bosnia and Herzegovina">BA   Bosnia and Herzegovina</option>
                  <option value="BB Barbados">BB   Barbados</option>
                  <option value="BD Bangladesh">BD   Bangladesh</option>
                  <option value="BE Belgium">BE   Belgium</option>
                  <option value="BF Burkina Faso">BF   Burkina Faso</option>
                  <option value="BG Bulgaria">BG   Bulgaria</option>
                  <option value="BH Bahrain">BH   Bahrain</option>
                  <option value="BI Burundi">BI   Burundi</option>
                  <option value="BJ Benin">BJ   Benin</option>
                  <option value="BM Bermuda">BM   Bermuda</option>
                  <option value="BN Brunei Darussalam">BN   Brunei Darussalam</option>
                  <option value="BO Bolivia">BO   Bolivia</option>
                  <option value="BR Brazil">BR   Brazil</option>
                  <option value="BS Bahamas">BS   Bahamas</option>
                  <option value="BT Bhutan">BT   Bhutan</option>
                  <option value="BV Bouvet Island">BV   Bouvet Island</option>
                  <option value="BW Botswana">BW   Botswana</option>
                  <option value="BY Belarus">BY   Belarus</option>
                  <option value="BZ Belize">BZ   Belize</option>
                  <option value="CA Canada">CA   Canada</option>
                  <option value="CC Cocos (Keeling) Islands">CC   Cocos (Keeling) Islands</option>
                  <option value="CD Democratic Republic of the Congo">CD   Democratic Republic of the Congo</option>
                  <option value="CF Central African Republic">CF   Central African Republic</option>
                  <option value="CG Congo">CG   Congo</option>
                  <option value="CH Switzerland">CH   Switzerland</option>
                  <option value="CI Cote D'Ivoire (Ivory Coast)">CI   Cote D'Ivoire (Ivory Coast)</option>
                  <option value="CK Cook Islands">CK   Cook Islands</option>
                  <option value="CL Chile">CL   Chile</option>
                  <option value="CM Cameroon">CM   Cameroon</option>
                  <option value="CN China">CN   China</option>
                  <option value="CO Colombia">CO   Colombia</option>
                  <option value="CR Costa Rica">CR   Costa Rica</option>
                  <option value="CS Serbia and Montenegro">CS   Serbia and Montenegro</option>
                  <option value="CU Cuba">CU   Cuba</option>
                  <option value="CV Cape Verde">CV   Cape Verde</option>
                  <option value="CX Christmas Island">CX   Christmas Island</option>
                  <option value="CY Cyprus">CY   Cyprus</option>
                  <option value="CZ Czech Republic">CZ   Czech Republic</option>
                  <option value="DE Germany">DE   Germany</option>
                  <option value="DJ Djibouti">DJ   Djibouti</option>
                  <option value="DK Denmark">DK   Denmark</option>
                  <option value="DM Dominica">DM   Dominica</option>
                  <option value="DO Dominican Republic">DO   Dominican Republic</option>
                  <option value="DZ Algeria">DZ   Algeria</option>
                  <option value="EC Ecuador">EC   Ecuador</option>
                  <option value="EE Estonia">EE   Estonia</option>
                  <option value="EG Egypt">EG   Egypt</option>
                  <option value="EH Western Sahara">EH   Western Sahara</option>
                  <option value="ER Eritrea">ER   Eritrea</option>
                  <option value="ES Spain">ES   Spain</option>
                  <option value="ET Ethiopia">ET   Ethiopia</option>
                  <option value="FI Finland">FI   Finland</option>
                  <option value="FJ Fiji">FJ   Fiji</option>
                  <option value="FK Falkland Islands (Malvinas)">FK   Falkland Islands (Malvinas)</option>
                  <option value="FM Federated States of Micronesia">FM   Federated States of Micronesia</option>
                  <option value="FO Faroe Islands">FO   Faroe Islands</option>
                  <option value="FR France">FR   France</option>
                  <option value="FX France, Metropolitan">FX   France, Metropolitan</option>
                  <option value="GA Gabon">GA   Gabon</option>
                  <option value="GB Great Britain (UK)">GB   Great Britain (UK)</option>
                  <option value="GD Grenada">GD   Grenada</option>
                  <option value="GE Georgia">GE   Georgia</option>
                  <option value="GF French Guiana">GF   French Guiana</option>
                  <option value="GH Ghana">GH   Ghana</option>
                  <option value="GI Gibraltar">GI   Gibraltar</option>
                  <option value="GL Greenland">GL   Greenland</option>
                  <option value="GM Gambia">GM   Gambia</option>
                  <option value="GN Guinea">GN   Guinea</option>
                  <option value="GP Guadeloupe">GP   Guadeloupe</option>
                  <option value="GQ Equatorial Guinea">GQ   Equatorial Guinea</option>
                  <option value="GR Greece">GR   Greece</option>
                  <option value="GS S. Georgia and S. Sandwich Islands">GS   S. Georgia and S. Sandwich Islands</option>
                  <option value="GT Guatemala">GT   Guatemala</option>
                  <option value="GU Guam">GU   Guam</option>
                  <option value="GW Guinea-Bissau">GW   Guinea-Bissau</option>
                  <option value="GY Guyana">GY   Guyana</option>
                  <option value="HK Hong Kong">HK   Hong Kong</option>
                  <option value="HM Heard Island and McDonald Islands">HM   Heard Island and McDonald Islands</option>
                  <option value="HN Honduras">HN   Honduras</option>
                  <option value="HR Croatia (Hrvatska)">HR   Croatia (Hrvatska)</option>
                  <option value="HT Haiti">HT   Haiti</option>
                  <option value="HU Hungary">HU   Hungary</option>
                  <option value="ID Indonesia">ID   Indonesia</option>
                  <option value="IE Ireland">IE   Ireland</option>
                  <option value="IL Israel">IL   Israel</option>
                  <option value="IN India">IN   India</option>
                  <option value="IO British Indian Ocean Territory">IO   British Indian Ocean Territory</option>
                  <option value="IQ Iraq">IQ   Iraq</option>
                  <option value="IR Iran">IR   Iran</option>
                  <option value="IS Iceland">IS   Iceland</option>
                  <option value="IT Italy">IT   Italy</option>
                  <option value="JM Jamaica">JM   Jamaica</option>
                  <option value="JO Jordan">JO   Jordan</option>
                  <option value="JP Japan">JP   Japan</option>
                  <option value="KE Kenya">KE   Kenya</option>
                  <option value="KG Kyrgyzstan">KG   Kyrgyzstan</option>
                  <option value="KH Cambodia">KH   Cambodia</option>
                  <option value="KI Kiribati">KI   Kiribati</option>
                  <option value="KM Comoros">KM   Comoros</option>
                  <option value="KN Saint Kitts and Nevis">KN   Saint Kitts and Nevis</option>
                  <option value="KP Korea (North)">KP   Korea (North)</option>
                  <option value="KR Korea (South)">KR   Korea (South)</option>
                  <option value="KW Kuwait">KW   Kuwait</option>
                  <option value="KY Cayman Islands">KY   Cayman Islands</option>
                  <option value="KZ Kazakhstan">KZ   Kazakhstan</option>
                  <option value="LA Laos">LA   Laos</option>
                  <option value="LB Lebanon">LB   Lebanon</option>
                  <option value="LC Saint Lucia">LC   Saint Lucia</option>
                  <option value="LI Liechtenstein">LI   Liechtenstein</option>
                  <option value="LK Sri Lanka">LK   Sri Lanka</option>
                  <option value="LR Liberia">LR   Liberia</option>
                  <option value="LS Lesotho">LS   Lesotho</option>
                  <option value="LT Lithuania">LT   Lithuania</option>
                  <option value="LU Luxembourg">LU   Luxembourg</option>
                  <option value="LV Latvia">LV   Latvia</option>
                  <option value="LY Libya">LY   Libya</option>
                  <option value="MA Morocco">MA   Morocco</option>
                  <option value="MC Monaco">MC   Monaco</option>
                  <option value="MD Moldova">MD   Moldova</option>
                  <option value="MG Madagascar">MG   Madagascar</option>
                  <option value="MH Marshall Islands">MH   Marshall Islands</option>
                  <option value="MK Macedonia">MK   Macedonia</option>
                  <option value="ML Mali">ML   Mali</option>
                  <option value="MM Myanmar">MM   Myanmar</option>
                  <option value="MN Mongolia">MN   Mongolia</option>
                  <option value="MO Macao">MO   Macao</option>
                  <option value="MP Northern Mariana Islands">MP   Northern Mariana Islands</option>
                  <option value="MQ Martinique">MQ   Martinique</option>
                  <option value="MR Mauritania">MR   Mauritania</option>
                  <option value="MS Montserrat">MS   Montserrat</option>
                  <option value="MT Malta">MT   Malta</option>
                  <option value="MU Mauritius">MU   Mauritius</option>
                  <option value="MV Maldives">MV   Maldives</option>
                  <option value="MW Malawi">MW   Malawi</option>
                  <option value="MX Mexico">MX   Mexico</option>
                  <option value="MY Malaysia">MY   Malaysia</option>
                  <option value="MZ Mozambique">MZ   Mozambique</option>
                  <option value="NA Namibia">NA   Namibia</option>
                  <option value="NC New Caledonia">NC   New Caledonia</option>
                  <option value="NE Niger">NE   Niger</option>
                  <option value="NF Norfolk Island">NF   Norfolk Island</option>
                  <option value="NG Nigeria">NG   Nigeria</option>
                  <option value="NI Nicaragua">NI   Nicaragua</option>
                  <option value="NL Netherlands">NL   Netherlands</option>
                  <option value="NO Norway">NO   Norway</option>
                  <option value="NP Nepal">NP   Nepal</option>
                  <option value="NR Nauru">NR   Nauru</option>
                  <option value="NU Niue">NU   Niue</option>
                  <option value="NZ New Zealand (Aotearoa)">NZ   New Zealand (Aotearoa)</option>
                  <option value="OM Oman">OM   Oman</option>
                  <option value="PA Panama">PA   Panama</option>
                  <option value="PE Peru">PE   Peru</option>
                  <option value="PF French Polynesia">PF   French Polynesia</option>
                  <option value="PG Papua New Guinea">PG   Papua New Guinea</option>
                  <option value="PH Philippines">PH   Philippines</option>
                  <option value="PK Pakistan">PK   Pakistan</option>
                  <option value="PL Poland">PL   Poland</option>
                  <option value="PM Saint Pierre and Miquelon">PM   Saint Pierre and Miquelon</option>
                  <option value="PN Pitcairn">PN   Pitcairn</option>
                  <option value="PR Puerto Rico">PR   Puerto Rico</option>
                  <option value="PS Palestinian Territory">PS   Palestinian Territory</option>
                  <option value="PT Portugal">PT   Portugal</option>
                  <option value="PW Palau">PW   Palau</option>
                  <option value="PY Paraguay">PY   Paraguay</option>
                  <option value="QA Qatar">QA   Qatar</option>
                  <option value="RE Reunion">RE   Reunion</option>
                  <option value="RO Romania">RO   Romania</option>
                  <option value="RU Russian Federation">RU   Russian Federation</option>
                  <option value="RW Rwanda">RW   Rwanda</option>
                  <option value="SA Saudi Arabia">SA   Saudi Arabia</option>
                  <option value="SB Solomon Islands">SB   Solomon Islands</option>
                  <option value="SC Seychelles">SC   Seychelles</option>
                  <option value="SD Sudan">SD   Sudan</option>
                  <option value="SE Sweden">SE   Sweden</option>
                  <option value="SG Singapore">SG   Singapore</option>
                  <option value="SH Saint Helena">SH   Saint Helena</option>
                  <option value="SI Slovenia">SI   Slovenia</option>
                  <option value="SJ Svalbard and Jan Mayen">SJ   Svalbard and Jan Mayen</option>
                  <option value="SK Slovakia">SK   Slovakia</option>
                  <option value="SL Sierra Leone">SL   Sierra Leone</option>
                  <option value="SM San Marino">SM   San Marino</option>
                  <option value="SN Senegal">SN   Senegal</option>
                  <option value="SO Somalia">SO   Somalia</option>
                  <option value="SR Suriname">SR   Suriname</option>
                  <option value="ST Sao Tome and Principe">ST   Sao Tome and Principe</option>
                  <option value="SU USSR (former)">SU   USSR (former)</option>
                  <option value="SV El Salvador">SV   El Salvador</option>
                  <option value="SY Syria">SY   Syria</option>
                  <option value="SZ Swaziland">SZ   Swaziland</option>
                  <option value="TC Turks and Caicos Islands">TC   Turks and Caicos Islands</option>
                  <option value="TD Chad">TD   Chad</option>
                  <option value="TF French Southern Territories">TF   French Southern Territories</option>
                  <option value="TG Togo">TG   Togo</option>
                  <option value="TH Thailand">TH   Thailand</option>
                  <option value="TJ Tajikistan">TJ   Tajikistan</option>
                  <option value="TK Tokelau">TK   Tokelau</option>
                  <option value="TL Timor-Leste">TL   Timor-Leste</option>
                  <option value="TM Turkmenistan">TM   Turkmenistan</option>
                  <option value="TN Tunisia">TN   Tunisia</option>
                  <option value="TO Tonga">TO   Tonga</option>
                  <option value="TP East Timor">TP   East Timor</option>
                  <option value="TR Turkey">TR   Turkey</option>
                  <option value="TT Trinidad and Tobago">TT   Trinidad and Tobago</option>
                  <option value="TV Tuvalu">TV   Tuvalu</option>
                  <option value="TW Taiwan">TW   Taiwan</option>
                  <option value="TZ Tanzania">TZ   Tanzania</option>
                  <option value="UA Ukraine">UA   Ukraine</option>
                  <option value="UG Uganda">UG   Uganda</option>
                  <option value="UK United Kingdom">UK   United Kingdom</option>
                  <option value="UM United States Minor Outlying Islands">UM   United States Minor Outlying Islands</option>
                  <option value="UY Uruguay">UY   Uruguay</option>
                  <option value="UZ Uzbekistan">UZ   Uzbekistan</option>
                  <option value="VA Vatican City State (Holy See)">VA   Vatican City State (Holy See)</option>
                  <option value="VC Saint Vincent and the Grenadines">VC   Saint Vincent and the Grenadines</option>
                  <option value="VE Venezuela">VE   Venezuela</option>
                  <option value="VG Virgin Islands (British)">VG   Virgin Islands (British)</option>
                  <option value="VI Virgin Islands (U.S.)">VI   Virgin Islands (U.S.)</option>
                  <option value="VN Viet Nam">VN   Viet Nam</option>
                  <option value="VU Vanuatu">VU   Vanuatu</option>
                  <option value="WF Wallis and Futuna">WF   Wallis and Futuna</option>
                  <option value="WS Samoa">WS   Samoa</option>
                  <option value="YE Yemen">YE   Yemen</option>
                  <option value="YT Mayotte">YT   Mayotte</option>
                  <option value="YU Yugoslavia (former)">YU   Yugoslavia (former)</option>
                  <option value="ZA South Africa">ZA   South Africa</option>
                  <option value="ZM Zambia">ZM   Zambia</option>
                  <option value="ZR Zaire (former)">ZR   Zaire (former)</option>
                  <option value="ZW Zimbabwe">ZW   Zimbabwe</option>
                </select>
              </spring:bind>
              <script type="text/javascript">
                formelement('country');
              </script>
            </td>
          </tr>
          <tr>
            <td>
            </td>
            <td>
              <input class="submit" type="submit" name="submit" value="Register" id="submit"/>
            </td>
          </tr>
        </table>
      </form>
    </div>

<%@ include file="foot.jsp" %>
