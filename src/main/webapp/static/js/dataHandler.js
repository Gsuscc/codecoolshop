export let dataHandler = {

    _data: {},

    _api_get: function (url, callback) {
        fetch(url, {
            method: 'GET',
            credentials: 'same-origin'
        })
            .then(response => response.json())
            .then(json_response => callback(json_response));
    },

    _api_post: function (url, data, callback) {
        fetch(url, {
            method: 'POST',
            credentials: "include",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)

        })
            .then(response => response.json())
            .then(json_response => callback(json_response))
    },

    init: function () {
    },

    loadCartData: function (callback) {
        let url = `/api/cart`
        this._api_get(url, (response) => {
            this._data = response;
            callback(response)
        });
    },

    delete: function (callback, id) {
        let url = `api/cart/delete?prodId=${id}`
        this._api_get(url, (response) => {
            this._data = response;
            callback(response)
        });
    },

    updateQuantity: function (callback, quantity, productId) {
        let url = `api/cart/update?prodId=${productId}&quantity=${quantity}`
        this._api_get(url, (response) => {
            this._data = response;
            callback(response)
        });
    },

    sendPaymentDetails: function (data, callback) {
        this._api_post(`/checkout`, data, (response) => {
            callback(response)
        });
    },
    getOrderById: function (callback, orderId) {
        this._api_get(`/myorder?orderId=${orderId}`, (response) => {
            callback(response)
        })
    },
    saveCart: function (callback, data) {
        this._api_post(`/api/cart/save`,
            data,
            (response) => {
                callback(response)
            })
    },
    updateBillingAddress: function (callback, data) {
        this._api_post(`/myprofile/update?type=billing`,
            data,
            (response) => {
                callback(response)
            })
    },


    updateShippingAddress: function (callback, data) {
        this._api_post(`/myprofile/update?type=shipping`,
            data,
            (response) => {
                callback(response)
            })
    },
    emptyCart: function(callback) {
        this._api_get(`/cart?delete`, (response) => {
            this._data = response;
            callback(response)
        });
    },

    getShippingInfoForCheckout: function(callback) {
        this._api_get(`/order?type=shipping`, (response) => {
            this._data = response;
            callback(response)
        });
    }
}
