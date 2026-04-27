<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Карточка рейса</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1 id="route-number">${route.routeNumber}</h1>
    <p>Компания: <span id="company-name">${route.company.name}</span></p>
    <p>Название: <span id="route-name">${route.name}</span></p>
    <a id="edit-route" class="button" href="/routes/${route.id}/edit">Редактировать</a>

    <h2>Остановки</h2>
    <table id="stops-table">
        <thead><tr><th>Порядок</th><th>Остановка</th><th>Город</th><th>Стоянка</th></tr></thead>
        <tbody>
        <c:forEach var="stop" items="${stops}">
            <tr>
                <td>${stop.seq}</td>
                <td>${stop.stop.name}</td>
                <td>${stop.stop.city}</td>
                <td><c:choose><c:when test="${empty stop.dwellMin}">-</c:when><c:otherwise>${stop.dwellMin} мин</c:otherwise></c:choose></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <h2>Поездки</h2>
    <table id="trips-table">
        <thead><tr><th>ID</th><th>Отправление</th><th>Мест</th><th>Статус</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="trip" items="${trips}">
            <tr>
                <td>${trip.id}</td>
                <td>${trip.departureAt}</td>
                <td>${trip.capacity}</td>
                <td>${trip.status}</td>
                <td><a class="button order-ticket" href="/orders/new?tripId=${trip.id}">Оформить заказ</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
