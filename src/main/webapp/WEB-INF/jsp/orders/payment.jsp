<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Оплата заказа</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Оплата заказа ${order.id}</h1>
    <p>Сумма: <span id="order-price">${order.price}</span></p>
    <form id="payment-form" action="/orders/${order.id}/payment" method="post">
        <label for="cardNumber">Номер карты</label>
        <input id="cardNumber" name="cardNumber" value="4111111111111111">
        <button id="pay-order" type="submit">Оплатить</button>
    </form>
</main>
</body>
</html>
