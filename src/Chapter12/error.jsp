<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:url value="ch12/login.jsp" var="login"/>
<html>
  <head><title>Error: Login Failure</title></head>
  <body>
    Login failed, please try
    <a href='${login}'>again</a>.
  </body>
</html>
