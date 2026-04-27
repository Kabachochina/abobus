<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Отмена заказа</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Отмена заказа ${order.id}</h1>
    <p>Действие изменит статус заказа на canceled.</p>
    <form id="cancel-form" action="/orders/${order.id}/cancel" method="post">
        <label for="reason">Причина</label>
        <input id="reason" name="reason" value="Отмена клиентом">
        <button id="confirm-cancel" class="danger" type="submit">Подтвердить отмену</button>
        <a class="button" href="/orders/${order.id}">Назад</a>
    </form>
</main>
</body>
</html>
