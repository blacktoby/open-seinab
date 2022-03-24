<div class="form-group mt-4">
    <label for="${input.htmlId}" class="font-weight-bold">${input.name}</label>
    <#if input.description?has_content>
        <p class="small text-secondary mb-1">${input.description}</p>
    </#if>
    <textarea type="${input.type}" class="form-control" id="${input.htmlId}" rows="3"
           name="${input.name}"
            ${input.required?then('required', '')}>${input.defaultValue!''}</textarea>
    <div class="invalid-feedback">
        Bitte fülle dieses Feld aus
    </div>
</div>