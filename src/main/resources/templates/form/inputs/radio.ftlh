<div class="form-group mt-4">
    <#if input.title?has_content><span class="font-weight-bold">${input.title}</span></#if>
    <#if input.description?has_content>
        <p class="small text-secondary mb-1">${input.description}</p>
    </#if>
    <#list input.inputOptions as option>
        <div class="custom-control custom-radio">
            <input class="custom-control-input" type="${input.type}"
                   name="${input.name}" id="${input.htmlId}${option.position}" value="${option.value}" ${input.required?then('required', '')}
                    <#if option.disabled>disabled</#if>>
            <label class="custom-control-label" for="${input.htmlId}${option.position}">
                ${option.text}
            </label>
            <#if option?is_last>
                <div class="invalid-feedback">
                    Bitte wähle eine Option
                </div>
            </#if>
        </div>
    </#list>
</div>
