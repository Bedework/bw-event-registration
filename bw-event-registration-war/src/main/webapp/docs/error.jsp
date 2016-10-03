<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html;charset=ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="/spring" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
    <title>Error Page</title>
    <link rel="stylesheet" href="/evreg/bweventreg.css" type="text/css" media="all"/>
  </head>
  <body>
    <div class="box">
      <h4>Event Registration Error:</h4>
      <p>${sessMan.message}</p>
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


