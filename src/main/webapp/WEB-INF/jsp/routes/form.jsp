<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Рейс</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Рейс</h1>
    <c:if test="${not empty error}"><div id="error" class="error">${error}</div></c:if>
    <form id="route-form" action="${action}" method="post">
        <label for="companyId">Компания</label>
        <select id="companyId" name="companyId">
            <c:forEach var="company" items="${companies}">
                <option value="${company.id}">${company.name}</option>
            </c:forEach>
        </select>
        <label for="routeNumber">Номер</label>
        <input id="routeNumber" name="routeNumber" value="${route.routeNumber}">
        <label for="name">Название</label>
        <input id="name" name="name" value="${route.name}">
        <button id="save-route" type="submit">Сохранить</button>
        <a class="button" href="/routes">Отмена</a>
    </form>
    <c:if test="${not empty route.id}">
        <form id="delete-route-form" action="/routes/${route.id}/delete" method="post">
            <button id="delete-route" class="danger" type="submit">Удалить</button>
        </form>
    </c:if>
</main>
</body>
</html>
