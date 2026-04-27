<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Карточка клиента</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1 id="client-name">${client.fullName}</h1>
    <p>Email: <span id="client-email">${client.email}</span></p>
    <p>Телефон: <span id="client-phone">${client.phone}</span></p>
    <p>Адрес: <span id="client-address">${client.address}</span></p>
    <a id="edit-client" class="button" href="/clients/${client.id}/edit">Изменить</a>
    <a id="client-orders" class="button" href="/clients/${client.id}/orders">История заказов клиента</a>
</main>
</body>
</html>
