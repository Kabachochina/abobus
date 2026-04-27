<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Оформление заказа</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Оформление заказа</h1>
    <p id="trip-info">Рейс ${trip.route.routeNumber}, отправление ${trip.departureAt}, свободно мест: ${availableSeats}</p>
    <c:if test="${not empty error}"><div id="error" class="error">${error}</div></c:if>
    <form id="order-form" action="/orders" method="post">
        <input type="hidden" name="tripId" value="${trip.id}">
        <label for="clientId">Клиент</label>
        <select id="clientId" name="clientId">
            <c:forEach var="client" items="${clients}">
                <option value="${client.id}">${client.fullName}</option>
            </c:forEach>
        </select>
        <label for="fromRouteStopId">Откуда</label>
        <select id="fromRouteStopId" name="fromRouteStopId">
            <c:forEach var="stop" items="${stops}">
                <option value="${stop.id}">${stop.seq}. ${stop.stop.city}</option>
            </c:forEach>
        </select>
        <label for="toRouteStopId">Куда</label>
        <select id="toRouteStopId" name="toRouteStopId">
            <c:forEach var="stop" items="${stops}">
                <option value="${stop.id}">${stop.seq}. ${stop.stop.city}</option>
            </c:forEach>
        </select>
        <button id="create-order" type="submit">Оформить заказ</button>
    </form>
    <h2>Время остановок</h2>
    <table id="times-table">
        <thead><tr><th>Остановка</th><th>Прибытие</th><th>Отправление</th></tr></thead>
        <tbody>
        <c:forEach var="time" items="${times}">
            <tr>
                <td>${time.routeStop.stop.name}</td>
                <td>${time.arrivalAt}</td>
                <td>${time.departureAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
