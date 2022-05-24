let Crypto = {
    AES: {
        enc: function(originalText) {
            let key = CryptoJS.enc.Utf8.parse("bWFsbHB3ZA==WNST");
            let srcs = CryptoJS.enc.Utf8.parse(originalText);
            let encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return encrypted.toString();
        },
        dec: function (data) {
            let key = CryptoJS.enc.Utf8.parse("bWFsbHB3ZA==WNST");
            let decrypt = CryptoJS.AES.decrypt(data, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
            return CryptoJS.enc.Utf8.stringify(decrypt).toString();
        }
    },
    DES: {
        enc: function() {

        },
        dec: function () {

        }
    }
}