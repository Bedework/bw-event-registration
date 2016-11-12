<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page contentType="text/html;charset=ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="/spring" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"></meta>
    <title>Bedework Event Registration Logout</title>
    <link rel="stylesheet" href="/evreg/bweventreg.css" type="text/css" media="all"/>
    <script type="text/javascript">
      window.history.forward();
      function stop() {
        window.history.forward();
      }
    </script>
  </head>
  <body onload="stop();" onpageshow="if (event.persisted) stop();" onunload="">
    <div class="box">
      <h4>Logged Out</h4>
      <p>You have been logged out.</p>
    </div>

  </body>
</html>


