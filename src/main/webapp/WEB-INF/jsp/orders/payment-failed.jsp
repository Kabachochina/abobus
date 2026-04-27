<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="ru">
<head><title>Оплата не прошла</title></head>
<body>
<%@ include file="../common/header.jsp" %>
<main>
    <h1 id="failed">Оплата не прошла</h1>
    <p>Причина ошибки: платеж отклонен</p>
    <a id="retry-payment" class="button" href="/orders/${order.id}/payment">Повторить</a>
</main>
</body>
</html>
