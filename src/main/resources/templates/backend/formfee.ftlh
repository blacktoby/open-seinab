<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html lang="de">
<head>
    <#include 'includes/meta.ftlh'>
    <#assign form=formPermission.form>
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
                <h1 class="h3 mb-4 text-gray-800">${form.displayName} - Teilnehmerbeiträge</h1>

                <#if formPermission.bankingPermitted>
                    <button class="btn btn-primary mb-2" id="formfeeButton" disabled>
                        <i id="process-spin" class="fas fa-sync fa-spin mr-2 d-none"></i>
                        <span id="formfeeButtonText">Teilnehmerbeiträge prüfen</span></button>
                </#if>

                <div class="card shadow mb-4">
<#--                    <div class="card-header py-3">-->
<#--                        <h6 class="m-0 font-weight-bold text-primary"></h6>-->
<#--                    </div>-->
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="turnoverTable" class="table table-bordered dt-responsive nowrap" style="width:100%">
                                <thead>
                                    <tr>
                                        <#list inputList as input>
                                            <td>${input.name}</td>
                                        </#list>
                                        <td>Überwiesener Betrag in Euro</td>
                                        <td>Buchungsdatum</td>
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
<#include 'includes/banking-modal.ftlh'>
<script type="text/javascript" src="<@spring.url '/assets/momentjs/moment-with-locales.min.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/assets/datatables/datatables.min.js'/>"></script>

<script>
    let table = null;

    $(function() {
        <#if formPermission.writePermitted>
            let dateFormat = 'DD.MM.YYYY';
            var editor = new $.fn.dataTable.Editor( {
                ajax:  '/backend/api/${eventGroupName}/${formName}/formfee/datatable',
                table: '#turnoverTable',
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
                        }
                    }
                }
            } );

        editor.add( {
            label: 'Betrag in Euro',
            name: 'value',
            type: 'text',
            id: 'value',
            data: 'value',
            attr: {
                required: true
            }
        });

        editor.add( {
            label: 'Buchungsdatum',
            name: 'date',
            type: 'datetime',
            id: 'date',
            data: 'date',
            def: function () { return new Date(); },
            displayFormat: dateFormat,
            attr: {
                required: true
            }
        });



        editor.on( 'preSubmit', function ( e, data, action ) {
            $.each( data.data, function ( key, values ) {
                console.log("date",moment(values["date"], dateFormat).valueOf());

                data.data[ key ][ 'date' ] = moment(values["date"], dateFormat).valueOf();
                data.data[ key ][ 'value' ] = parseInt(values["value"]*100, 10);
            } );
        } );


        editor.on('initEdit', function(e, node, data, times, type) {
            editor.one('open', function() {
                $("input[id^=date]").val(moment(data["date"]).format(dateFormat));
                $("input[id^=value]").val((data["value"]/100).toFixed(2));
            });
        })
        </#if>

        table = $('#turnoverTable').DataTable( {
            dom: 'Bfrtip',
            select: true,
            pageLength: 50,
            ajax: {
                url: '/backend/api/${eventGroupName}/${formName}/formfee'
            },
            buttons: [
                <#if formPermission.writePermitted>
                // { extend: 'create', editor: editor },
                { extend: 'edit',   editor: editor },
                // { extend: 'remove', editor: editor },
                </#if>
                 { extend: 'excelHtml5',
                    text: 'Excel',
                    exportOptions: {
                        columns: ':not(.no-export)'
                    }}
            ],
            columns: [
                <#list inputList as input>
                    {data: '${input.name}'},
                </#list>
                {
                    data: 'value',
                    render: function(data, type, row) {
                        if(data.length !== 0) {
                            return (data / 100).toFixed(2)
                        }
                        return "";
                    }
                },
                {data: 'date',
                    render: function(data, type, row) {
                        if(!isNaN(data) && data.length !== 0) {
                            let date = new Date(Number(data));
                            let options =
                                { year: 'numeric', month: '2-digit', day: '2-digit'};
                            return date.toLocaleString("de-DE", options);
                        }
                        return "";
                    }
                }
            ],
            columnDefs: [
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
<#if formPermission.bankingPermitted>
    <script>

        $(function() {
            let ws = undefined;

            function connectionSuccessful(socket) {
                ws = socket;
                $("#formfeeButton").prop("disabled", false);
            }

            function statusCallback(message) {
                switch(message) {
                    case "PROCESSING":
                        $("#formfeeButton").prop("disabled", true);
                        $("#process-spin").removeClass("d-none");
                        $("#formfeeButtonText").text("Prüfe Teilnehmerbeiträge...");
                        break;
                    case "IDLE":
                        $("#formfeeButton").prop("disabled", false);
                        $("#process-spin").addClass("d-none");
                        $("#formfeeButtonText").text("Teilnehmerbeiträge prüfen");
                        break;
                    case "SUCCESS":
                        if(table != null) {
                            table.ajax.reload();
                        }
                        break;
                    default:
                        break;
                }
            }

            connectBankingWebSocket(connectionSuccessful, statusCallback);

            $("#formfeeButton").on("click", function () {
                ws.send("/socket/banking/replies/${form.eventGroup.name}/${form.name}/feematching", {})
            });

        });
    </script>
</#if>
</body>
</html>