function sendOrder() {
    var xhr = new XMLHttpRequest();
    
    const isuNo = document.getElementById('isuNo').value,
        cano = document.getElementById('cano').value,
        acntPrdtCd = document.getElementById('acntPrdtCd').value,
        targetBuyPrc = parseInt(document.getElementById('targetBuyPrc').value),
        buyPrc = parseInt(document.getElementById('buyPrc').value),
        sellPrc = parseInt(document.getElementById('sellPrc').value),
        ordQty = parseInt(document.getElementById('ordQty').value),
        bnsTpCode = document.querySelector('input[name="bnsTpCode"]:checked').value,
        guboon = document.querySelector('$guboon').value
    var data = {
        isuNo: isuNo,
        cano: cano,
        acntPrdtCd: acntPrdtCd,
        targetBuyPrc: targetBuyPrc,
        buyPrc: buyPrc,
        sellPrc: sellPrc,
        ordQty: ordQty,
        bnsTpCode: bnsTpCode

    };
    const url = "/" + guboon + "/sendOrder.do";

    if (data.bnsTpCode == '3') {
        document.getElementById('alertMsg').innerHTML='처리중입니다.'
    }
    xhr.onload = function() {
        if (xhr.status >= 200 && xhr.status < 300) {
            // 요청이 성공적으로 완료된 경우
            console.log("응답 데이터Text: ", xhr.responseText);
            var responseData = JSON.parse(xhr.responseText); // JSON 형식의 데이터를 JavaScript 객체로 변환
            console.log("응답 데이터Data: ", responseData);
            document.getElementById('alertMsg').innerHTML=responseData.msg;
        } else {
            // 요청이 실패한 경우
            console.error("요청이 실패하였습니다. 상태 코드: ", xhr.status);
        }
    };
    
    xhr.onerror = function() {
        console.error("네트워크 오류가 발생하였습니다.");
    };

    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    var jsonData = JSON.stringify(data);
    xhr.send(jsonData);
}