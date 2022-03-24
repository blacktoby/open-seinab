<#import "/spring.ftl" as spring/>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<@security.authentication var="user" property="principal"/>
<!-- Sidebar -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- Sidebar - Brand -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="<@spring.url '/backend'/>">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-clipboard"></i>
        </div>
        <div class="sidebar-brand-text mx-3">Seinab</div>
    </a>

    <!-- Divider -->
    <hr class="sidebar-divider my-0">

    <!-- Nav Item - Dashboard -->
    <li class="nav-item active">
        <a class="nav-link" href="<@spring.url '/backend'/>">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span></a>
    </li>

    <!-- Divider -->
    <hr class="sidebar-divider">



    <!-- Nav Item - Pages Collapse Menu -->
    <#assign eventGroupMap = user.getFormPermissionsByEventGroupName()/>
    <#list eventGroupMap?keys as eventGroupName>
        <!-- Heading -->
        <div class="sidebar-heading">
            ${eventGroupName}
        </div>

        <#list eventGroupMap[eventGroupName] as formPermission>
            <#assign sideForm = formPermission.form/>
            <li class="nav-item">
                <a class="nav-link <#if springMacroRequestContext.requestUri?contains(sideForm.name)>show<#else> collapsed</#if> "
                   href="#" data-toggle="collapse" data-target="#collapse${sideForm.name}"
                   aria-expanded="true" aria-controls="collapse${sideForm.name}">
                    <i class="fas fa-fw fa-users"></i>
                    <span>${sideForm.displayName}</span>
                </a>
                <div id="collapse${sideForm.name}" class="collapse <#if springMacroRequestContext.requestUri?contains(sideForm.name)>show<#else>collapsed</#if>" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
                    <div class="bg-white collapse-inner rounded">
                        <a class="collapse-item <#if springMacroRequestContext.requestUri?contains(sideForm.name) && springMacroRequestContext.requestUri?contains('submissions')>active</#if>"
                           href="<@spring.url '/backend/${eventGroupName}/${sideForm.name}/submissions'/>">
                            Anmeldungen
                        </a>
                        <#if formPermission.formfeePermitted>
                            <a class="collapse-item <#if springMacroRequestContext.requestUri?contains(sideForm.name) && springMacroRequestContext.requestUri?contains('formfee')>active</#if>"
                               href="<@spring.url '/backend/${eventGroupName}/${sideForm.name}/formfee'/>">
                                Teilnehmerbeiträge
                            </a>
                        </#if>
                    </div>
                </div>
            </li>
        </#list>

        <!-- Divider -->
        <hr class="sidebar-divider d-none d-md-block">
    </#list>

    <!-- Sidebar Toggler (Sidebar) -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>

</ul>
<!-- End of Sidebar -->