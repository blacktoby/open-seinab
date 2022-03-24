<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <#include 'includes/meta.ftlh'>

    <title>Seinab - ${form.displayName}</title>

    <#include 'includes/style.ftlh'>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/assets/datatables/datatables.min.css'/>"/>
</head>
<body id="page-top">
<!-- Page Wrapper -->
<div id="wrapper">

    <#include 'includes/sidebar.ftlh'>

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

        <!-- Main Content -->
        <div id="content">

            <#include 'includes/topbar.ftlh'>

            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <h1 class="h3 mb-4 text-gray-800">${form.displayName} - Anmeldungen</h1>

                <#if countStatistics?has_content>
                    <div class="row">
                        <#list countStatistics as countStats>
                            <div class="col-xl-3 col-md-6 mb-4">
                                <div class="card border-left-primary shadow h-100 py-2">
                                    <div class="card-body">
                                        <div class="row no-gutters align-items-center">
                                            <div class="col mr-2">
                                                <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">${countStats.name}</div>
                                                <div class="h5 mb-0 font-weight-bold text-gray-800">${countStats.count}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </div>
                </#if>

                <div class="card shadow mb-4">
<#--                    <div class="card-header py-3">-->
<#--                        <h6 class="m-0 font-weight-bold text-primary"></h6>-->
<#--                    </div>-->
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="submissionTable" class="table table-bordered dt-responsive nowrap" style="width:100%">
                                <thead>
                                    <tr>
                                        <#list formPermission.getFilteredInputList() as input>
                                            <td>${input.name}</td>
                                        </#list>
                                        <#list abbreviatedInputList as abInput>
                                            <td>${abInput.name}</td>
                                        </#list>
                                        <#if form.consent??>
                                            <td>Einverständniserklärung</td>
                                        </#if>
                                    </tr>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.container-fluid -->
        </div>
        <!-- End of Main Content -->

        <#include 'includes/footer.ftlh'>

    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<!-- Scroll to Top Button-->
<a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
</a>

<#include 'includes/logout.ftlh'>

<#include 'includes/scripts.ftlh'>
<script type="text/javascript" src="<@spring.url '/assets/datatables/datatables.min.js'/>"></script>

<script>
    $(function() {
        <#if formPermission.writePermitted>
            var editor = new $.fn.dataTable.Editor( {
                ajax:  '/backend/api/${eventGroupName}/${formName}/datatable',
                table: '#submissionTable',
                idSrc: 'submissionId',
                fields: [],
                i18n: {
                    create: {
                        button: "Neuer Eintrag",
                        title: "Erstelle einen neuen Eintrag",
                        submit: "Erstellen"
                    },
                    edit: {
                        button: "Bearbeiten",
                        title: "Eintrag bearbeiten",
                        submit: "Aktualisieren"
                    },
                    remove: {
                        button: "Löschen",
                        title: "Löschen",
                        confirm: {
                            _: "Möchtest du wirklich %d Einträge löschen?",
                            1: "Möchtest du diesen Eintrag wirklich löschen?"
                        },
                        submit: "Löschen"
                    }
                }
            } );


            <#list formPermission.getFilteredInputList() as input>
                <#assign inputType = input.type>
                <#if inputType?starts_with('email') || inputType?starts_with('number')>
                    <#assign inputType = 'text'>
                </#if>

                editor.add( {
                    label: '${input.name}',
                    name: '${input.name}',
                    type: '${inputType}',
                    id: '${input.htmlId}',
                    data: '${input.name}.value'
                    <#if input.type == 'select' && !input.optionsKeyInput?? || input.type == 'radio'>
                    ,options: [
                        <#if input.defaultText??>
                            {label: '${input.defaultText}', value: ''},
                        </#if>
                        <#list input.inputOptions as option>
                        {label: '${option.text}', value: '${option.value}'}<#sep>,</#sep>
                        </#list>
                    ]
                    <#elseif input.type = 'checkbox'>
                    ,options: [
                        [""]
                    ],
                    unselectedValue: "false"
                    </#if>
                });

                <#if input.optionsKeyInput??>
                    editor.on('open', function() {
                        let optionsDict = {
                            <#assign optionKeyMap = input.getInputOptionsByKey()>
                            <#list optionKeyMap?keys as key>
                            "${key}": [
                                <#list optionKeyMap[key] as option>
                                {
                                    "label": "${option.text}",
                                    "value": "${option.value}"
                                }<#sep>, </#sep>
                                </#list>
                            ]<#sep>, </#sep>
                            </#list>
                        };
                        <#assign filteredInputKey = formPermission.getFilteredInputData(input.optionsKeyInput.name)>
                        <#if filteredInputKey?has_content>
                            editor.field('${input.name}').update(optionsDict['${filteredInputKey}']);
                        <#else>
                            $("#${input.optionsKeyInput.htmlId}").off("change");
                            $("#${input.optionsKeyInput.htmlId}").change(function() {
                                let key = $(this).val();
                                editor.field('${input.name}').update(optionsDict[key]);
                            })
                        </#if>
                    });
                </#if>
                <#if input.type =='checkbox'>
                    editor.on('initEdit', function(e, node, data, times, type) {
                        let selected = data['${input.name}'].value === "true";
                        if(selected) {
                            editor.one('open', function() {
                                $("input[id^=${input.htmlId}]").prop('checked', true);
                            });
                        }
                    })
                </#if>
            </#list>
        </#if>

        $('#submissionTable').DataTable( {
            dom: 'Bfrtip',
            select: true,
            ajax: {
                url: '/backend/api/${eventGroupName}/${formName}/submissions'
            },
            buttons: [
                <#if formPermission.writePermitted>
                { extend: 'create', editor: editor },
                { extend: 'edit',   editor: editor },
                { extend: 'remove', editor: editor }
                </#if>
                , { extend: 'excelHtml5',
                    text: 'Excel',
                    exportOptions: {
                        columns: ':not(.no-export)'
                    }}
            ],
            columns: [
                <#list formPermission.getFilteredInputList() as input>
                    {data: '${input.name}'}<#sep>,</#sep>
                </#list>
                <#list abbreviatedInputList as abInput>
                    , {data: '${abInput.name}'}<#sep>,</#sep>
                </#list>
                <#if form.consent??>
                    ,{
                        data: "",
                        render: function(data, type, row) {
                            if(row.hasOwnProperty("submissionId")) {
                                return "<a class='btn btn-outline-success' target='_blank' href='<@spring.url '/backend/${form.getEventGroup().getName()}/${form.getName()}/"+row.submissionId+"/consent'/>'>Herunterladen</a>";
                            }
                            return "#";
                        },
                        className: "no-export"
                    },
                </#if>
            ],
            columnDefs: [
                        {
                            "render": function(data, type, row) {
                                if(data.text === 'true') {
                                    return "<i class='fas fa-fw text-success fa-check'></i>"
                                }
                                return "<i class='fas fa-fw text-danger fa-times'></i>"
                            },
                            targets: [
                                <#list formPermission.getFilteredInputList() as input>
                                    <#if input.type == 'checkbox'>
                                        ${input?index},
                                    </#if>
                                </#list>
                            ]
                        },
                        {
                            targets: "_all",
                            render: function(data, type, row) {
                                if(data.hasOwnProperty("text")) {
                                    return data.text;
                                }
                                return data;
                            }
                        }
            ],
            language: {
                url: "<@spring.url '/assets/datatables/german.json'/>"
            }
        } );
    })
</script>
</body>
</html>