<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Детали заказа</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Заказ <span id="order-id">${order.id}</span></h1>
    <p>Клиент: <span id="order-client">${order.client.fullName}</span></p>
    <p>Рейс: <span id="order-route">${order.trip.route.routeNumber}</span></p>
    <p>Откуда: ${order.fromRouteStop.stop.city}</p>
    <p>Куда: ${order.toRouteStop.stop.city}</p>
    <p>Цена: <span id="order-price">${order.price}</span></p>
    <p>Статус: <span id="order-status">${order.status}</span></p>
    <p>Оплата: <span id="payment-status">${order.paymentStatus}</span></p>
    <a id="cancel-order" class="button danger" href="/orders/${order.id}/cancel">Отменить заказ</a>
</main>
</body>
</html>
