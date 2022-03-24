<#if formPermission.bankingPermitted>
    <div class="modal fade" id="bankingModal" tabindex="-1" role="dialog" aria-labelledby="bankingTitle" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="bankingTitle">Online-Banking</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="bankingModalForm">
                        <div class="form-group">
                            <label for="bankingInput" id="bankingLabel"></label>
                            <input class="form-control" type="text" id="bankingInput">
                        </div>
                        <button type="submit" class="btn btn-primary" id="bankingSubmitButton">Abschicken</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script type="text/javascript" src="<@spring.url '/assets/stomp/stomp.min.js'/>"></script>

    <script>
        function connectBankingWebSocket(connectionCallback, statusCallback) {
            let socket = new SockJS('/socket/banking');
            let ws = Stomp.over(socket);

            ws.connect({}, function(frame) {
                subscribeStatus(ws, statusCallback);
                ws.send("/socket/banking/replies/${form.eventGroup.name}/status", {});
                connectionCallback(ws);

                initListener(ws);
            });
        }

        function initListener(ws) {
            $("#bankingModalForm").on("submit", function() {
                let val = $("#bankingInput").val();
                ws.send("/socket/banking/replies/${form.eventGroup.name}/fulfill", {}, val);
                $("#bankingModal").modal("hide");
                return false;
            });
        }

        function subscribeStatus(ws, statusCallback){
            ws.subscribe('/private/socket/banking/status/${form.eventGroup.name}/message', function(message) {
                let wsMessage = message.body.substring(1, message.body.length-1);
                statusCallback(wsMessage);
                switch (wsMessage) {
                    case "USERID":
                        prepareModalAndShow("text", "Nutzername oder -kennung");
                        break;
                    case "PIN":
                        prepareModalAndShow("password", "Pin");
                        break;
                    case "TAN":
                        prepareModalAndShow("text", "Tan");
                        break;
                    default:
                        break;
                }
            });
        }

        function prepareModalAndShow(type, label) {
            $("#bankingLabel").text(label);

            let $bankingInput = $("#bankingInput");
            $bankingInput.val("");
            $bankingInput.attr("type", type);

            $("#bankingModal").modal("show");
        }

    </script>
</#if>
