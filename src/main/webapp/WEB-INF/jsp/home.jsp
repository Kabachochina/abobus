<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Abobus</title></head>
<body>
<%@ include file="common/header.jsp" %>
<main>
    <h1>Система информации об автобусных рейсах и билетах</h1>
    <p>Поиск рейсов, управление клиентами и оформление заказов.</p>
    <a id="home-routes" class="button" href="/routes">Рейсы</a>
    <a id="home-clients" class="button" href="/clients">Клиенты</a>

    <h2>Быстрый поиск рейсов</h2>
    <form id="quick-search-form" action="/routes" method="get">
        <label for="date">Дата</label>
        <input id="date" name="date" type="date">
        <label for="name">Направление или остановка</label>
        <input id="name" name="name" type="text">
        <button id="quick-search" type="submit">Поиск</button>
    </form>
</main>
</body>
</html>
