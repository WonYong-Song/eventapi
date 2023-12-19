function login() {
    const guboon = document.querySelector('input[name="securitiesCompany"]:checked').value;
    const appKey = document.querySelector('#appKey').value;
    const secretKey = document.querySelector('#secretKey').value;
    const param = {
        guboon: guboon,
        appKey: appKey,
        secretKey: secretKey
    };
    if (param.appKey === '') {
        alert('appKey가 비어있습니다.');
        document.querySelector('#appKey').focus();
        return;
    } else if (param.secretKey === '') {
        alert('secretKey가 비어있습니다.');
        document.querySelector('#secretKey').focus();
        return;
    }

    let xhr = new XMLHttpRequest();
    const url = "/login.do";
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                const resopnseData = JSON.parse(xhr.responseText);
                if (resopnseData.result) {
                    window.location.href = resopnseData.redirectUri;
                } else {
                    const msg = resopnseData.msg
                    document.querySelector('.alertMsg').innerHTML = msg;
                }
            } else {
                document.querySelector('.alertMsg').innerHTML = '서버와 통신 에러 발생 잠시 후 다시 시도해주세요';
            }
        }
    };

    // 데이터를 JSON 문자열로 변환하여 전송
    const jsonData = JSON.stringify(param);

    // 요청 전송
    xhr.send(jsonData);

}