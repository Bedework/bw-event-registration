<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html;charset=ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="globals" value="${sessionScope.globals}" />

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
    <title>Error Page</title>
    <link rel="stylesheet" href="/approots/evreg/bweventreg.css" type="text/css" media="all"/>
  </head>
  <body>
    <div class="box">
      <h4>Event Registration Administration Error:</h4>
      <p>${globals.message}</p>
      <p><a href="javascript:window.history.back();">Back</a></p>
      <!--p>
        <a class="quietLink" href="javascript:window.top.location = window.top.location;"> 
        < ! - - set the location explicitly back to itself, rather than .reload() which causes prompts, etc. - -  >
          Please reload the page.
        </a>
      </p-->
    </div>
  </body>
</html>


