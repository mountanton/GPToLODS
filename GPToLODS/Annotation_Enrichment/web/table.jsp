<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
	

	<body>
		
		<div class="table-wrapper">
                    <table class="alt" border="1">
		<c:forEach var="weather" items="${results}" varStatus="status">
			<c:choose>
				<c:when test="${status.index==0}">
					<tr class="simple"  style="font-weight: bold;">
				</c:when>
				<c:otherwise>
					<tr class="blueTr">
				</c:otherwise>
			</c:choose>
                                            <c:set var="count" value="0" scope="page" />
				<c:forEach var="weather2" items="${results[status.index]}" varStatus="status3">
                                    <td align="center">${weather2}</td>
			</c:forEach>
		</tr>
    </c:forEach>
</table>
                </div>

</body>
</html>