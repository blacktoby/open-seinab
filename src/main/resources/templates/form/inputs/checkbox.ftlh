<div class="form-check form-group mt-4">
    <input class="form-check-input" type="${input.type}"
           name="${input.name}" id="${input.htmlId}" value="${input.defaultValue!''}" ${input.required?then('required', '')}>
    <label class="form-check-label" for="${input.htmlId}">
            ${input.description!input.name}
    </label>
    <div class="invalid-feedback">
        Bitte stimme zu
    </div>
</div>