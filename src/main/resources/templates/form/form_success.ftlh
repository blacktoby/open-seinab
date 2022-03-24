<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>${form.displayName}</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/assets/bootstrap/css/bootstrap.min.css'/>">
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-12 col-md-6 offset-md-3 text-center">
            <img alt="logo" class="img-fluid w-50" src="/form/${form.eventGroup.name}/${form.name}/logo">
        </div>
        <div class="col-12 text-center">
            <h1>${form.displayName}</h1>
        </div>
        <div class="col-lg-6 offset-lg-3 col-12 text-center">
            <p>
                Deine Anmeldung für die Aktion: "${form.displayName}" wurde gespeichert.
                <br>In Kürze erhältst du eine
                E-Mail mit allen Einzelheiten!
            </p>
        </div>
    </div>
</div>


</body>
<script type="application/javascript" src="<@spring.url '/assets/jquery/jquery-3.3.1.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/popper/popper-1.14.7.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/bootstrap/js/bootstrap.min.js'/>"></script>
</html>