<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <title>Bedework Event Registration System</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <script src="/approots/evreg/jquery-1.11.3.min.js" type="text/javascript"></script>
    <script src="/approots/evreg/jquery-ui-1.11.4/jquery-ui.min.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/approots/evreg/jquery-ui-1.11.4/jquery-ui.min.css" type="text/css" media="all"/>
    <link rel="stylesheet" href="/approots/evreg/jquery-ui-1.11.4/jquery-ui.theme.min.css" type="text/css" media="all"/>
    <c:if test="${regs != null}">
      <script src="/approots/evreg/tablesorter-2.0.5/jquery.tablesorter.min.js" type="text/javascript"></script>
      <link rel="stylesheet" href="/approots/evreg/tablesorter-2.0.5/style.css" type="text/css" media="all"/>
      <script type="text/javascript">
        $(document).ready(function() {
          <c:if test="${pageContext.request.servletPath == '/docs/agenda.jsp'}">
            // column 2 = dates: sort as text
            $("#userAgenda").tablesorter({
              widgets: ['zebra'],
              sortMultiSortKey: 'ctrlKey',
              headers: { 
                2: { sorter: 'text'},
                6: { sorter: false }
              }
            });
          </c:if> 
        });
      </script>
    </c:if>
    <script src="/approots/evreg/includes.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/approots/evreg/bweventreg.css" type="text/css" media="all"/>
  </head>
  <body>
