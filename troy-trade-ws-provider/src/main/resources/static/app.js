var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

var klineSubscribe = null;
var tickerSubscribe = null;
var tradesSubscribe = null;
var depthSubscribe = null;
var depthChartSubscribe = null;
var orderSubscribe = null;
var monitorSubscribe = null;

var enalbe_klineSubscribe = false;
var enalbe_tickerSubscribe = false;
var enalbe_tradesSubscribe = false;
var enalbe_depthSubscribe = true;
var enalbe_depthChartSubscribe = false;
var enalbe_monitorSubscribe = false;


var enalbe_orderSubscribe = false;
var enalbe_balanceSubscribe = false;

var exchCode = 'okex';
//var exch_acct_id = 'binance_symbol_btc_usdt';


//request_currency_pair = "BTC/USDT";
//sub_currency_pair = "btc_usdt";

var exch_acct_id = '3';
request_currency_pair = "TRX/USDT";
sub_currency_pair = "trx_usdt";
alias = "THIS_WEEK";

var exchAcctSymbolId = "3";

// var enalbe_orderSubscribe = true;
// var exchCode = 'gateio';
// var exch_acct_id = 'exch_acct_symbol_gateio';


//request_currency_pair = "CS/USDT";
//sub_currency_pair = "cs_usdt";

var accountId = '1';
var headers = {
    exchCode: exchCode,
    //'accountId': accountId,
    'accept-version':"1.1,1.0",
    'heart-beat':"10000,10000",
    "pair":request_currency_pair,
    "Authorization":"sdfsfd"
};

var num = 0;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);


    stompClient.connect(headers, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        var sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
        sessionId = exchCode + '_' + sessionId + '_' + sub_currency_pair;
        console.log("connected, session id: " + sessionId);

        if (enalbe_klineSubscribe) {
            //k线 订阅请求
            var klineRequest = {
                "accountId": accountId,
                "exchCode": exchCode,
                "method": "kline.subscribe",
                "params":
                    {
                        "symbol": request_currency_pair,   //交易对
                        "period": "1m"                //K线周期: 1m
                    },
                "id": 111
            };
            // k线 订阅请求
            stompClient.send("/app/topic/kline", {}, JSON.stringify(klineRequest));
            //k线 消息处理
            klineSubscribe = stompClient.subscribe('/user/' + sessionId + '/topic/kline/' + sub_currency_pair + '/1m', function (message) {
                console.log("topic/kline:");
                console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(body.payload);
            });
        }


        if (enalbe_tickerSubscribe) {
            //实时行情 订阅请求
            var tickerRequest = {
                "accountId": accountId,
                "exchCode": exchCode,
                "method": "ticker.subscribe",
                "params":
                    [request_currency_pair],
                "id": 111
            };
            stompClient.send("/app/topic/ticker", {}, JSON.stringify(tickerRequest));
            //实时行情 消息处理
            tickerSubscribe = stompClient.subscribe('/user/' + sessionId + '/topic/ticker/' + sub_currency_pair, function (message) {
                console.log("topic/ticker:");
                console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(body.payload);
            });
        }

        if (enalbe_tradesSubscribe) {
            //市场最新成交
            var marketTradeRequest = {
                "method": "trades.subscribe",
                "params":{
                    "symbol":request_currency_pair,
                    "exchCode":exchCode,
                    "alias":alias
                },
                "id": 111
            };
            //市场成交记录 订阅请求
            stompClient.send("/app/topic/xxxxxs", {}, JSON.stringify(marketTradeRequest));
            //市场成交记录 消息处理
            var path = '/user/' + sessionId + '/topic/xxxxxs/' + sub_currency_pair;
            if(null != alias&&''!=alias){
                path = path+"/"+alias;
            }
            tradesSubscribe = stompClient.subscribe(path, function (message) {
                console.log("=========================topic/xxxxxs:");
                console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(JSON.stringify(body.result));
            });
        }

        if (enalbe_depthSubscribe) {
            // 买卖挂单 订阅请求
            var tradeRequest = {
                "method": "depth.subscribe",
                "params":
                    {
                        "exchCode": exchCode,
                        "symbol": request_currency_pair,   //交易对
                        "alias":alias,
                        "limit": 30,                  //limit, legal limits: 1, 5, 10, 20, 30
                        "interval": "step0"           //合并深度（step0-5） step0:不合并
                    }
                ,
                "id": "111"
            };
            stompClient.send("/app/topic/depth", {}, JSON.stringify(tradeRequest));
            var path = '/user/' + sessionId + '/topic/depth/' + sub_currency_pair
            if(null != alias&&''!=alias){
                path = path+"/"+alias;
            }
            path = path+ '/30/step0';
            depthSubscribe = stompClient.subscribe(path, function (message) {
                 console.log("=========================topic/depth:");
                 console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(JSON.stringify(body.result));
            });
            if (enalbe_depthChartSubscribe) {
                //买卖挂单 消息处理
                depthChartSubscribe = stompClient.subscribe('/user/' + sessionId + '/topic/depthchart/' + sub_currency_pair + '/30/step0', function (message) {
                    console.log("=========================topic/depthchart:");
                    console.log("message:" + message);
                    var body = JSON.parse(message.body);
                    showGreeting(JSON.stringify(body.result));
                });
            }
        }
        if (enalbe_orderSubscribe) {

            var orderRequest = {
                "method": "order.subscribe",
                "params":
                    {
                        "exchCode": exchCode,
                        "accountId": accountId,
                        "symbol":request_currency_pair,   //交易对
                        "alias":alias,
                        "authorization":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJ1c2VyX25hbWUiOiIxNzYwMDc3MDg1NCIsInNjb3BlIjpbImRlZmF1bHQiXSwiZXhwIjoxNTY4MDg4MDQzLCJ1c2VySWQiOiIxMTYxODQxMjA2NTkwODU3MjE3IiwianRpIjoiMWE1MTBkYmYtODdkOC00MjhhLThiZTktYmVmYWEwODIwYTVhIiwiY2xpZW50X2lkIjoidHJveS11c2VyIn0.MgZhYqB924P6aC4ZaqmaL_hK4r36TpiwEZVqcK2sXrfqfFCHPtAZ57A8Uav8YURGpsGcbNI62vLdLus87PI769uu_reD6BVdfmt2WAoad67h4rG7eOKZbz_GdS1Rf-pjXi7bX9S2Ij5Tpj4s50nkPeUy5opgi5s5_53BMO2FK0A"
                    }
                ,
                "id": 111
            };
            //当前委托 订阅请求
            stompClient.send("/app/topic/order", {}, JSON.stringify(orderRequest));
            //当前委托 消息处理
            var path = '/user/' + sessionId + '/topic/order/' + sub_currency_pair ;
            if(null != alias&&''!=alias){
                path = path+"/"+alias;
            }
            path = path+ '/' + accountId;
            orderSubscribe = stompClient.subscribe(path, function (message) {
                console.log("topic/order:");
                // console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(JSON.parse(body.result));
            });
        }

        if (enalbe_balanceSubscribe) {

            var balanceRequest = {
                "method": "balance.subscribe",
                "params":
                    {
                        "exchCode": exchCode,
                        "accountId": accountId,
                        "symbol":request_currency_pair,   //交易对
                        "alias":alias,
                        "authorization":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJncmFudF90eXBlIjoicGFzc3dvcmQiLCJ1c2VyX25hbWUiOiIxNzYwMDc3MDg1NCIsInNjb3BlIjpbImRlZmF1bHQiXSwiZXhwIjoxNTY4MDg4MDQzLCJ1c2VySWQiOiIxMTYxODQxMjA2NTkwODU3MjE3IiwianRpIjoiMWE1MTBkYmYtODdkOC00MjhhLThiZTktYmVmYWEwODIwYTVhIiwiY2xpZW50X2lkIjoidHJveS11c2VyIn0.MgZhYqB924P6aC4ZaqmaL_hK4r36TpiwEZVqcK2sXrfqfFCHPtAZ57A8Uav8YURGpsGcbNI62vLdLus87PI769uu_reD6BVdfmt2WAoad67h4rG7eOKZbz_GdS1Rf-pjXi7bX9S2Ij5Tpj4s50nkPeUy5opgi5s5_53BMO2FK0A"
                    }
                ,
                "id": 111
            };
            //当前委托 订阅请求
            stompClient.send("/app/topic/balance", {}, JSON.stringify(balanceRequest));
            //当前委托 消息处理
            var path = '/user/' + sessionId + '/topic/balance/' + sub_currency_pair ;
            if(null != alias&&''!=alias){
                path = path+"/"+alias;
            }
            path = path+ '/' + accountId;
            orderSubscribe = stompClient.subscribe(path, function (message) {
                console.log("topic/balance:");
                // console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(JSON.parse(body.result));
            });
        }

        if (enalbe_monitorSubscribe) {

            var orderRequest = {
                "exchCode": exchCode,
                "accountId": accountId,
                "method": "monitor.robot.subscribe",
                "params":
                    {
                        "symbol": request_currency_pair,   //交易对
                        "exchAcctSymbolId": exchAcctSymbolId,       //交易对id
                    }
                ,
                "id": 111
            };
            //当前委托 订阅请求
            stompClient.send("/app/topic/monitor/robot/forIndex", {}, JSON.stringify(orderRequest));
            //当前委托 消息处理
            monitorSubscribe = stompClient.subscribe('/user/' + sessionId + '/topic/monitor/robot/forIndex', function (message) {
                console.log("topic/monitor/robot/forIndex:");
                // console.log("message:" + message);
                var body = JSON.parse(message.body);
                showGreeting(body.payload);
            });
        }

    });
}

function disconnect() {
    if (stompClient !== null) {
        if (enalbe_klineSubscribe) {
            klineSubscribe.unsubscribe(headers);
        }
        if (enalbe_tickerSubscribe) {
            tickerSubscribe.unsubscribe(headers);
        }
        if (enalbe_tradesSubscribe) {
            tradesSubscribe.unsubscribe(headers);
        }
        if (enalbe_depthSubscribe) {
            depthSubscribe.unsubscribe(headers);
        }
        if (enalbe_depthChartSubscribe) {
            depthChartSubscribe.unsubscribe(headers);
        }
        if (enalbe_orderSubscribe) {
            orderSubscribe.unsubscribe(headers);
        }
        if (enalbe_monitorSubscribe) {
            monitorSubscribe.unsubscribe(headers);
        }

        stompClient.disconnect({}, headers);
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    num = num + 1;
    console.log("===============收到服务器新消息=============");
    $("#greetings").prepend("<tr><td>" + message + "</td></tr>");
    $("#greetings").prepend("<tr><td>" + "收到服务器推送新消息:" + num + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});