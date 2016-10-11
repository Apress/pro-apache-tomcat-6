<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<c:url value="j_security_check" var="j_security_check"/>
<html>
  <head><title>Please Log In</title>
  <body>
    <form method="POST" 
          action='${j_security_check}' >
      <table border="0" cellspacing="5">
        <tr>
          <th align="right">Username:</th>
          <td align="left"><input type="text" name="j_username"></td>
        </tr>
        <tr>
          <th align="right">Password:</th>
          <td align="left"><input type="password" name="j_password"></td>
        </tr>
        <tr>
          <td align="right"><input type="submit" value="Log In"></td>
          <td align="left"><input type="reset"></td>
        </tr>
      </table>
    </form>
  </body>
</html>
