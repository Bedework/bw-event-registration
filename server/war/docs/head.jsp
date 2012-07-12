<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Bedework Event Registration System</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <c:if test="${regs != null}">
      <script src="/evreg/jquery-1.7.2.min.js" type="text/javascript"></script>
      <script src="/evreg/tablesorter-2.0.5/jquery.tablesorter.min.js" type="text/javascript"></script>
      <link rel="stylesheet" href="/evreg/tablesorter-2.0.5/style.css" type="text/css" media="all"/>
      <script type="text/javascript">
        $(document).ready(function() { 
          $("#agenda").tablesorter({
            widgets: ['zebra'],
            sortMultiSortKey: 'ctrlKey',
            textExtraction: function(node) {
              if ($(node).find('option:selected').text() != "") {
                return $(node).find('option:selected').text();
              } else if ($(node).find('input[type=text]').val() != undefined && $(node).find('input[type=text]').val() != "") {
                return $(node).find('input[type=text]').val();
              }
              return $(node).text();
            }
          }); 
        });
      </script>
    </c:if>
    <script src="/evreg/includes.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/evreg/bweventreg.css" type="text/css" media="all"/>
  </head>
  <body onload="javascript:focusFirstElement();">
