<div class="form-group mt-4">
    <label for="${input.htmlId}" class="font-weight-bold">${input.name}</label>
    <#if input.description?has_content>
        <p class="small text-secondary mb-1">${input.description}</p>
    </#if>
    <select class="custom-select" id="${input.htmlId}" ${input.required?then('required', '')}
            name="${input.name}" <#if input.optionsKeyInput??>disabled</#if>>
        <option class="defaultOption" value="" selected>${input.defaultText}</option>
        <#if !input.optionsKeyInput??>
            <#list input.inputOptions as option>
                <option class="validOption" value="${option.value}" <#if option.disabled>disabled</#if>>${option.text}</option>
            </#list>
        </#if>
    </select>
    <div class="invalid-feedback">
        Bitte wähle eine Option
    </div>

    <#if input.optionsKeyInput??>
        <script type="application/javascript">
            <#assign optionKeyMap = input.getInputOptionsByKey()>
            let optionsDict = {
                <#list optionKeyMap?keys as key>
                    "${key}": [
                        <#list optionKeyMap[key] as option>
                            {
                                "text": "${option.text}",
                                "value": "${option.value}",
                                "disabled": "${option.disabled?c}"
                            }<#sep>, </#sep>
                        </#list>
                    ]<#sep>, </#sep>
                </#list>
            };



            (function () {
                'use strict';
                window.addEventListener('load', function () {
                    function loadInputOptions() {
                        let input = document.getElementById("${input.htmlId}");

                        // get keyValue from changed input
                        let key = document.querySelector("#${input.optionsKeyInput.htmlId}").value;

                        // if default is select, disable dynamic select
                        if (key === "") {
                            input.disabled = true;
                            return;
                        }

                        // create and append new options
                        for (let option of optionsDict[key]) {
                            let newOption = document.createElement("option");
                            newOption.classList.add("validOption");
                            newOption.value = option.value;
                            newOption.text = option.text;
                            newOption.disabled = option.disabled == "true";
                            input.appendChild(newOption);
                        }

                        // remove disabled
                        input.disabled = false;
                    }

                    function checkAndloadInputOptions() {
                        let defaultOptions = document.querySelectorAll("#${input.optionsKeyInput.htmlId} .defaultOption");
                        if(defaultOptions.length > 0 && defaultOptions[0].selected === false) {
                            loadInputOptions();
                        }
                    }


                    document.querySelector("#${input.optionsKeyInput.htmlId}").addEventListener("change", function () {
                        // select default option
                        let defaultOption = document.querySelectorAll("#${input.htmlId} .defaultOption");
                        defaultOption.selected = true;

                        // remove all old options if present
                        let oldOptions = document.querySelectorAll("#${input.htmlId} .validOption");
                        if(oldOptions != null) {
                            Array.prototype.forEach.call( oldOptions, function( node ) {
                                node.parentNode.removeChild( node );
                            });
                        }

                        loadInputOptions();
                    });
                    checkAndloadInputOptions();
                });
            })();


        </script>
    </#if>
</div>