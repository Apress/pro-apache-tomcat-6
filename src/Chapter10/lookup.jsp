<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jstl/sql_rt" %>

<sql:setDataSource dataSource="jdbc/CatalogDB"/>

<sql:query var="products">
  SELECT * FROM product 
</sql:query>

<html>
  <head>
    <title>Online Products</title>
  </head>

  <body>
    <center>
      <h1>Products</h1>
    </center>

    <table border="1" align="center">
      <tr>
        <th>Name</th><th>Description</th><th>Price</th>
      </tr>
      <c:forEach items="${products.rows}" var="row">
        <tr>
          <td><c:out value="${row.prodname}" /></td>
          <td><c:out value="${row.proddesc}" /></td>
          <td><c:out value="${row.price}" /></td>
        </tr>
      </c:forEach> 
    </table>
  </body>
</html>