<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>История заказов</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>История заказов клиента</h1>
    <p id="client-name">${client.fullName}</p>
    <table id="orders-table">
        <thead><tr><th>ID</th><th>Дата</th><th>Рейс</th><th>Статус</th><th>Оплата</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="order" items="${orders}">
            <tr>
                <td>${order.id}</td>
                <td>${order.createdAt}</td>
                <td>${order.trip.route.routeNumber}</td>
                <td>${order.status}</td>
                <td>${order.paymentStatus}</td>
                <td><a class="button order-details" href="/orders/${order.id}">Подробнее</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
