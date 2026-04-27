<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Оплата прошла</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1 id="success">Оплата прошла</h1>
    <p>Заказ создан: ${order.id}</p>
    <a id="order-details" class="button" href="/orders/${order.id}">Подробнее</a>
</main>
</body>
</html>
