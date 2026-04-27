<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Клиент</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Клиент</h1>
    <c:if test="${not empty error}"><div id="error" class="error">${error}</div></c:if>
    <form id="client-form" action="${action}" method="post">
        <label for="fullName">ФИО</label>
        <input id="fullName" name="fullName" value="${client.fullName}">
        <label for="email">Email</label>
        <input id="email" name="email" value="${client.email}">
        <label for="phone">Телефон</label>
        <input id="phone" name="phone" value="${client.phone}">
        <label for="address">Адрес</label>
        <textarea id="address" name="address">${client.address}</textarea>
        <button id="save-client" type="submit">Сохранить</button>
        <a class="button" href="/clients">Отмена</a>
    </form>
    <c:if test="${not empty client.id}">
        <form id="delete-client-form" action="/clients/${client.id}/delete" method="post">
            <button id="delete-client" class="danger" type="submit">Удалить</button>
        </form>
    </c:if>
</main>
</body>
</html>
