/* ********************************************************************
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
 */
/**
 * Simple javascript validator for input fields
 * @param {string} str  - The string to validate
 * @param {string} type - Any special type of validation to be applied:
 *                        "alphanumeric" - alphanumeric, dash, and underscore; no spaces.
 *                        "plaintext"    - alphanumeric, dash, underscore, and spaces
 *                        defaults to plaintext
 * @returns {boolean}
 * @author johnsa
 */
function isValid(str,type) {
  var inputString = $.trim(str); // we trim onblur, so this should never be needed, but here for last check
  var inputType = "plaintext";
  if (type != undefined) {
    inputType = type;
  }
  if (inputString == "") {
    return false; // never allow empty for required fields
  }
  var reg =  /^[\w :\\$\\?-]+$/; // plaintext: alphanumeric + spaces (not tabs), and some others: ":-?$"
  switch (type) {
    case 'alphanumeric':
      reg = /^[\w-]+$/; // alphanumeric + dashes and underscores
      break;
  }
  return reg.test(inputString);
}

/**
 * Test string against an array for a match - return false for match
 * @param {string} str    - The string to look for
 * @param {array} strings - Array of strings to match against
 * @returns {boolean}
 * @author johnsa
 */
function isUnique(str,strings) {
  var inputString = $.trim(str); // we trim onblur, so this should never be needed, but here for last check
  return $.inArray(str,strings) == -1; // -1 means not there, so unique.  Otherwise, not unique.
}
/**
 * Let user confirm enabling and disabling a form
 */
function checkDisable(type) {
  if (type == 'disable') {
    return confirm("Disabling a form will make it unavailable to all events.\nAre you sure you wish to proceed?");
  } else {
    return confirm("Enable a form will make it available to all events in your calendar suite.\n\nProceed?");
  }
}
/**
 * Let user confirm a form publish
 */
function confirmPublish() {
  return confirm("Publishing this form will make it available to all \nadministrators in your calendar suite.\n\nOnce published, you will not be allowed to make \nupdates to the form.\n\nProceed??");
}
/**
 * Reveal / hide the disabled forms
 */
function showDisabled(show) {
  if(show) {
    $("tr.disabled-true").show();
  } else {
    $("tr.disabled-true").hide();
  }
}
/**
 * Hide / reveal the pending forms
 */
function hidePending(hide) {
  if(hide) {
    $("tr.committed-false").hide();
  } else {
    $("tr.committed-false").show();
  }
}
