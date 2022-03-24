<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <title>${form.displayName}</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/assets/bootstrap/css/bootstrap.min.css'/>">
</head>
<body>
<div class="container">
    <div class="row mt-5">
        <div class="col-12 text-center">
            <h1>${form.displayName}</h1>
        </div>
        <div class="col-lg-4 offset-lg-4 col-12">
            <form action="/form/${form.eventGroup.name}/${form.name}/login" method="post">
                <div class="form-group text-center">
                    <label for="password">Passwort</label>
                    <input type="password" class="form-control" name="password" id="password">
                </div>
                <#if RequestParameters.error??>
                    <div class="text-danger text-center mb-2">
                        Falsches Passwort!
                    </div>
                </#if>
                <div class="text-center w-100">
                    <button type="submit" class="btn btn-success">Fortfahren</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>

<script type="application/javascript" src="<@spring.url '/assets/jquery/jquery-3.3.1.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/popper/popper-1.14.7.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/bootstrap/js/bootstrap.min.js'/>"></script>
</html>