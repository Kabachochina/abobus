<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    body { font-family: Arial, sans-serif; margin: 0; color: #222; background: #f6f7f9; }
    header { background: #263238; padding: 14px 28px; }
    header a { color: white; margin-right: 18px; text-decoration: none; font-weight: 600; }
    main { max-width: 1100px; margin: 0 auto; padding: 28px; background: white; min-height: 80vh; }
    table { border-collapse: collapse; width: 100%; margin-top: 18px; }
    th, td { border: 1px solid #d8dde3; padding: 8px 10px; text-align: left; vertical-align: top; }
    th { background: #eef1f4; }
    form { margin: 14px 0; }
    label { display: block; margin: 10px 0 4px; font-weight: 600; }
    input, select, textarea { width: 100%; max-width: 520px; padding: 8px; box-sizing: border-box; }
    button, .button { display: inline-block; border: 0; background: #1565c0; color: white; padding: 8px 13px; margin: 6px 6px 6px 0; text-decoration: none; cursor: pointer; }
    .danger { background: #b71c1c; }
    .muted { color: #667; }
    .error { background: #ffebee; border: 1px solid #ef9a9a; padding: 10px; margin: 12px 0; }
    .ok { background: #e8f5e9; border: 1px solid #a5d6a7; padding: 10px; margin: 12px 0; }
</style>
<header>
    <a id="nav-home" href="/home">Главная</a>
    <a id="nav-routes" href="/routes">Рейсы</a>
    <a id="nav-clients" href="/clients">Клиенты</a>
</header>
