<#import "/spring.ftl" as spring/>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<@security.authentication var="user" property="principal"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Seinab</title>
</head>
<body>
<h1>Willkommen!</h1>
</body>
</html>