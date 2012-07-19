// places the cursor in the first available form element when the page is loaded
// (if a form exists on the page)
function focusFirstElement() {
  if (window.document.forms[0]) {
    if (window.document.forms[0].elements[0]) {
      window.document.forms[0].elements[0].focus();
    }
  }
}
// print icon is clicked
function launchWindow(URL,width) {
  paramString = "width=" + width + ",height=700,scrollbars=yes,resizable=yes,alwaysRaised=yes,menubar=yes,toolbar=yes";
  eventregWindow = window.open("", "eventregWindow", paramString);
  eventregWindow.focus();
  eventregWindow.location.href=URL;
}

// need to determine provenance and need for these functions from 
// here ....
function formelement(id) {
  var el = document.getElementById(id);
  el.onfocus = inputfocus;
  el.onblur = inputblur;
  el.onmouseover = inputmin;
  el.onmouseout = inputmout;
}
function inputfocus() {
  document.getElementById(this.id).className="focus";
}
function inputblur() {
  document.getElementById(this.id).className="normal";
}
function inputmin() {
 if(document.getElementById(this.id).className == "focus") {
   document.getElementById(this.id).className="focus";
 } else {
   document.getElementById(this.id).className="hover";
 }
}
function inputmout() {
  if(document.getElementById(this.id).className == "focus") {
    document.getElementById(this.id).className="focus";
  } else {
    document.getElementById(this.id).className="normal";
  }
}
// .... to here

function confirmRemoveTicket(eventTitle) {
  return confirm("You have chosen to remove \"" + eventTitle + "\" from your agenda.\n\nProceed?");
}
function confirmRemoveAdminTicket(email) {
  return confirm("You have chosen to remove \"" + email + "\" from this event.\n\nProceed?");
}
function confirmUpdateTicket(ticketId, eventTitle) {
  var ticketCssId = "tickets" + ticketId;
  var selectBox = document.getElementById(ticketCssId);
  var qty = selectBox.options[selectBox.selectedIndex].value;
  return confirm("The number of tickets for \"" + eventTitle + "\"\nwill be set to " + qty + ".\n\nProceed?");
}
function doUpdateTicket(regId,eventHref) {
  var ticketCssId = "tickets" + regId;
  var selectBox = document.getElementById(ticketCssId);
  var numtickets = selectBox.options[selectBox.selectedIndex].value;
  // formObj.getElementById("comment" + regId).value;
  //alert("updateReg.do?id=" + regId + "&qty=" + qty + "&comment=");
  location.replace("updateReg.do?regid=" + regId + "&href=" + eventHref + "&numtickets=" + numtickets + "&comment=");
}
function confirmUpdateAdminTicket() {
  return confirm("The ticket will be updated.\n\nProceed?");
}
function doUpdateAdminTicket(regId,eventHref,token) {
  var typeCssId = "type" + regId;
  var typeSelectBox = document.getElementById(typeCssId);
  //var type = typeSelectBox.options[typeSelectBox.selectedIndex].value;
  //var type = $("#type" + regId + " option:selected").val();
  //var comment = escape(document.getElementById("comment" + regId).value);
  var comment = $("#comment" + regId).val();
  var numtickets = escape(document.getElementById("numtickets" + regId).value);
  location.replace("updateAdminReg.do?regid=" + regId + "&href=" + eventHref + "&numtickets=" + numtickets + "&comment=" + comment + "&atkn=" + token );
}
function validate(formObj) {
  if (!echeck(formObj.email.value)) {
    alert("This email address does not appear to be valid.\nWithout a valid email address,\nwe will not be able to confirm your registration.");
    formObj.email.focus();
    return false;
  }
}
function trim(str) {
  if (str.length < 1) {
    return"";
  }
  str = rightTrim(str);
  str = leftTrim(str);

  if(str == "") {
    return "";
  } else {
    return str;
  }
}

function rightTrim(str) {
  var w_space = String.fromCharCode(32);
  var v_length = str.length;
  var strTemp = "";

  if(v_length < 0) {
    return "";
  }
  var iTemp = v_length - 1;
  while(iTemp > -1){
    if(str.charAt(iTemp) != w_space) {
      strTemp = str.substring(0,iTemp +1);
      break;
    }
    iTemp = iTemp-1;
  }
  return strTemp;
}

function leftTrim(str) {
  var w_space = String.fromCharCode(32);
  if(v_length < 1) {
    return "";
  }
  var v_length = str.length;
  var strTemp = "";
  var iTemp = 0;

  while(iTemp < v_length) {
    if(str.charAt(iTemp) != w_space) {
      strTemp = str.substring(iTemp,v_length);
      break;
    }
    iTemp = iTemp + 1;
  }
  return strTemp;
}

// DHTML email validation script. Courtesy of SmartWebby.com (http://www.smartwebby.com/dhtml/)
  function echeck(str) {
    var at="@"
    var dot="."
    var lat=str.indexOf(at)
    var lstr=str.length
    var ldot=str.indexOf(dot)
    if (str.indexOf(at)==-1) {
       return false
    }
    if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr) {
       return false
    }
    if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr) {
        return false
    }
    if (str.indexOf(at,(lat+1))!=-1) {
      return false
    }
    if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot) {
      return false
    }
    if (str.indexOf(dot,(lat+2))==-1) {
      return false
    }
    if (str.indexOf(" ")!=-1) {
      return false
    }
   return true
 }