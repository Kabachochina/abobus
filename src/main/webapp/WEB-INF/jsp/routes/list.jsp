<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Рейсы</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Рейсы</h1>
    <form id="route-filter-form" action="/routes" method="get">
        <label for="number">Номер</label>
        <input id="number" name="number" value="${number}">
        <label for="name">Направление или остановка</label>
        <input id="name" name="name" value="${name}">
        <label for="date">Дата</label>
        <input id="date" name="date" type="date" value="${date}">
        <button id="filter-routes" type="submit">Найти</button>
    </form>
    <a id="add-route" class="button" href="/routes/new">Добавить рейс</a>
    <table id="routes-table">
        <thead><tr><th>Компания</th><th>Номер</th><th>Название</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="route" items="${routes}">
            <tr>
                <td>${route.company.name}</td>
                <td>${route.routeNumber}</td>
                <td>${route.name}</td>
                <td><a class="button route-details" href="/routes/${route.id}">Подробнее</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
