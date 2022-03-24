<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <title>${form.displayName}</title>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/assets/fontawesome-free/css/all.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<@spring.url '/assets/bootstrap/css/bootstrap.min.css'/>">
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-12 col-md-6 offset-md-3 text-center">
            <img alt="logo" class="img-fluid w-50" src="/form/${form.eventGroup.name}/${form.name}/logo">
        </div>
        <div class="col-12 text-center mb-4">
            <h1>${form.displayName}</h1>
        </div>
        <div class="col-lg-6 offset-lg-3 col-12 mb-5">
            <form action="/form/${form.eventGroup.name}/${form.name}/save" method="post" class="needs-validation"
                  novalidate>
                <#assign currentInputGroupId=-1>
                <#list form.inputList as input>
                    <#if input.inputGroup?? && input.inputGroup.id != currentInputGroupId>
                        <#assign currentInputGroupId=input.inputGroup.id>
                        <h3 class="mt-5">${input.inputGroup.title}</h3>
                    </#if>
                    <#switch input.type>
                        <#case 'radio'>
                            <#include 'inputs/radio.ftlh'>
                            <#break>
                        <#case 'checkbox'>
                            <#include 'inputs/checkbox.ftlh'>
                            <#break>
                        <#case 'select'>
                            <#include 'inputs/select.ftlh'>
                            <#break>
                        <#case 'textarea'>
                            <#include 'inputs/textarea.ftlh'>
                            <#break>
                        <#default>
                            <#include 'inputs/default.ftlh'>
                    </#switch>
                </#list>
                <#list confirmationInputs as input>
                    <#include 'inputs/checkbox.ftlh'>
                </#list>
                <p class="text-danger d-none mt-1" id="notvalidtext">Bitte fülle alle benötigten Felder aus.</p>
                <button class="btn btn-success" type="submit" id="submitButton">
                    <i class="fas fa-circle-notch fa-spin d-none mr-2" id="submitSpinner"></i>Absenden</button>
            </form>
        </div>
    </div>
</div>


</body>
<script type="application/javascript" src="<@spring.url '/assets/jquery/jquery-3.3.1.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/popper/popper-1.14.7.min.js'/>"></script>
<script type="application/javascript" src="<@spring.url '/assets/bootstrap/js/bootstrap.min.js'/>"></script>

<script type="application/javascript">
    (function () {
        'use strict';
        window.addEventListener('load', function () {
            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            var forms = document.getElementsByClassName('needs-validation');
            // Loop over them and prevent submission
            var validation = Array.prototype.filter.call(forms, function (form) {
                form.addEventListener('submit', function (event) {
                    if (form.checkValidity() === false) {
                        document.getElementById("notvalidtext").classList.remove("d-none");
                        event.preventDefault();
                        event.stopPropagation();
                    }else {
                        document.getElementById("submitButton").setAttribute("disabled", "");
                        document.getElementById("submitSpinner").classList.remove("d-none");
                    }
                    form.classList.add('was-validated');

                }, false);
            });
        }, false);
    })();
</script>
</html>