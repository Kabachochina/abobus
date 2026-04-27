<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="ru">
<head><title>Клиенты</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1>Клиенты</h1>
    <form id="client-filter-form" action="/clients" method="get">
        <label for="name">ФИО</label>
        <input id="name" name="name" value="${name}">
        <label for="contact">Email или телефон</label>
        <input id="contact" name="contact" value="${contact}">
        <label for="tripId">ID поездки</label>
        <input id="tripId" name="tripId" value="${tripId}">
        <button id="filter-clients" type="submit">Найти</button>
    </form>
    <a id="add-client" class="button" href="/clients/new">Добавить клиента</a>
    <table id="clients-table">
        <thead><tr><th>ФИО</th><th>Email</th><th>Телефон</th><th>Адрес</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="client" items="${clients}">
            <tr>
                <td>${client.fullName}</td>
                <td>${client.email}</td>
                <td>${client.phone}</td>
                <td>${client.address}</td>
                <td><a class="button client-details" href="/clients/${client.id}">Подробнее</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
</body>
</html>
