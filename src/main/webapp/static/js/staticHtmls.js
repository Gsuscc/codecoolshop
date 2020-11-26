import {dom} from "./dom.js";

export let htmlParts = {

    addBody: function (element) {
        let modalBodyElement = document.querySelector("#modal-body");
        modalBodyElement.insertAdjacentHTML("beforeend", element);
    },

    getSuccessMessage: function () {
        return `<h4 id="loading-message">Successful Transaction, Thank you for your order. Sending You back to the Main Page</h4>`
    },

    getPleaseWaitMessage: function () {
        return `<h4>Please wait...</h4><br>`
    },

    buildPayPalFields: function () {
        let payPalElements = ""
        payPalElements += `<div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="cc-name">E-mail</label>
                                    <input type="email" class="form-control" name="pp-mail" id="pp-mail" placeholder="" required>
                                    <small class="text-muted">Your PayPal Username</small>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="cc-number">Password</label>
                                    <input type="password" class="form-control" name="pp-pass" id="pp-pass" placeholder=""
                                           required>
                                </div>
                            </div>`
        return payPalElements
    },

    buildCreditCardFields: function () {
        return `<div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="cc-name">Name on card</label>
                        <input type="text" class="form-control" name="cc-name" id="cc-name" placeholder="" required="">
                        <small class="text-muted">Full name as displayed on card</small>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="cc-number">Credit card number</label>
                        <input type="text" class="" name="cc-number" id="cc-number" placeholder=""
                               required="">
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-3 mb-3">
                        <label for="cc-expiration">Expiration</label>
                        <input type="text" class="form-control" name="cc-expiration" id="cc-expiration" placeholder=""
                               required="">
                    </div>
                    <div class="col-md-3 mb-3">
                        <label for="cc-cvv">CVV</label>
                        <input type="number" class="form-control" name="cc-cvv" maxlength="3" id="cc-cvv" placeholder="" required="">
                    </div>
                </div>`
    },

    getLoadingScreenForModal: function () {
        return `<div class="spinner-grow text-primary" role="status">
                    <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-secondary" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-success" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-danger" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-warning" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-info" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-light" role="status">
                  <span class="sr-only">Loading...</span>
                </div>
                <div class="spinner-grow text-dark" role="status">
                  <span class="sr-only">Loading...</span>
                </div>`
    },

    buildTotalLine: function () {
        let totalLineElement = "";
        totalLineElement += `
                    <table>
                         <tr>
                            <th class="product-name"></th>
                            <th class="product-quantity"></th>
                            <th class="product-price"></th>
                            <th class="product-subtotal"></th>
                            <th class="product-delete"></th>
                        </tr>
                         <tr>
                            <td class="product-price total-line" colspan="3"><h4>TOTAL:</h4></td>
                            <td class="product-subtotal" colspan="2"><h4>${dom.countCartTotal()} USD</h4></td>
                         </tr>
                     </table>`;
        htmlParts.addBody(totalLineElement);
    },

    buildTable: function (data) {
        let tableElement = ""
        if (!data.length) tableElement += `
                    <table> <tr>
                        <th id="empty-modal" rowspan="10">Cart is Empty    <img id="sad" src="/static/img/sad.png"></th>
                    </tr>`
        else {
            tableElement += `
                    <table class="cart-item-table">
                        <tr>
                            <th class="product-name">Product Name</th>
                            <th class="product-quantity">Quantity</th>
                            <th class="product-price">Price</th>
                            <th class="product-subtotal">Sub Total Price</th>
                        </tr>`;
        }
        for (let item of data) {
            tableElement += `
                    <tr class="line-item">
                        <td class="product-name" id="product-name" data-product-id="${item.productId}">${item.name}</td>
                        <td class="product-quantity"id="quantity"><input class="product-quantity" type="number" min="1" step="1" value="${item["quantity"]}"></td>
                        <td class="product-price">${item["unitPrice"]} USD</td>
                        <td class="product-subtotal" id="subtotal">${item["quantity"] * item["unitPrice"]} USD</td>
                        <td><img class="delete-icon" src="/static/img/delete.png"></td>
                    </tr>`;
        }
        tableElement += `</table>`;
        htmlParts.addBody(tableElement);
    },

    buildMyOrderTable: function (data) {
        let grandTotal = 0;
        let elem = ""
        elem +=
            `<table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th scope="col">Image</th>
                <th scope="col">Product Name</th>
                <th scope="col">Quantity</th>
                <th scope="col">Price</th>
                <th scope="col">Sub Total Price</th>
            </tr>
            </thead>
            <tbody>`;
        for (let item of data) {
            grandTotal += parseInt(item.total)
            elem +=
                `<tr>
                <td><img class="overview-image"
                         src="/static/img/product_${item.productId}.jpg" alt=""/></td>
                <td>${item.name}</td>
                <td>${item.quantity}</td>
                <td>${item.unitPrice} USD</td>
                <td>${item.total} USD</td>
            </tr>
            `
        }
        elem += `<td class="total-line" colspan="4"><h4 th:text="'TOTAL: '"></h4></td>
                <td><h4>TOTAL: ${grandTotal} USD</h4></td> 
                </tbody>`
        return elem
    },

    buildEditableBillingFields: function (billingData) {

        let div = `
     
        <div id="billing-form-container">
        <div class="col-md-10 order-md-1">
            <h4 class="billing-title">Billing address</h4>
            <form class="needs-validation" id="billing-data-form" name="billing-form">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="firstName">First name</label>
                        <input type="text" class="form-control" name="firstName" id="firstName" value='${billingData.firstName ? billingData.firstName : ""}'
                               required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="lastName">Last name</label>
                        <input type="text" class="form-control" name="lastName" id="lastName" value='${billingData.lastName ? billingData.lastName : ""}'
                               required>
                    </div>
                </div>
                <div class="mb-3">
                    <label for="email">Email <span class="text-muted">(Optional)</span></label>
                    <input type="email" class="form-control" name="email" id="email" value='${billingData.email ? billingData.email : ""}'>
                </div>
                <div class="mb-3">
                    <label for="address">Address</label>
                    <input type="text" class="form-control" name="address" id="address" value='${billingData.address ? billingData.address : ""}'
                           required>
                </div>
                <div class="row">
                    <div class="col-md-5 mb-3">
                        <label for="country">Country</label>
                        <select class="custom-select d-block w-100" name="country" id="country" required>
                            <option value='${billingData.country}'>${billingData.country ? billingData.country : "Choose.."}</option>
                            <option>Hungary</option>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="state">State</label>
                        <select class="custom-select d-block w-100" name="state" id="state" required>
                            <option value="${billingData.state ? billingData.state : "Choose.."}">${billingData.state ? billingData.state : "Choose.."}</option>
                            <option>Bacs-Kiskun</option>
                            <option>Baranya</option>
                            <option>Bekes</option>
                            <option>Borsod-Abauj-Zemplen</option>
                            <option>Budapest</option>
                            <option>Csongrad</option>
                            <option>Fejer</option>
                            <option>Gyor-Moson-Sopron</option>
                            <option>Hajdu-Bihar</option>
                            <option>Heves</option>
                            <option>Jasz-Nagykun-Szolnok</option>
                            <option>Komarom-Esztergom</option>
                            <option>Nograd</option>
                            <option>Pest</option>
                            <option>Somogy</option>
                            <option>Szabolcs-Szatmar-Bereg</option>
                            <option>Tolna</option>
                            <option>Vas</option>
                            <option>Veszprem</option>
                            <option>Zala</option>
                        </select>
                    </div>
                    <div class="col-md-3 mb-3">
                        <label for="zip">Zip</label>
                        <input type="number" class="form-control" name="zip" id="zip" value='${billingData.zip ? billingData.zip : ""}' required>
                    </div>
                </div>
                <a class="btn btn-primary" href="/myprofile">Back</a>
                <button class="btn btn-primary" id="update-billing-details" type="button">Save</button>
            </form>
        </div>
    </div>`
        return div
    },

    buildEditableShippingFields: function (shippingData, route) {
        let div = `

    <div id="shipping-form-container">
        <div class="col-md-10 order-md-1">
            <h4 class="shipping-title">Shipping address</h4>
            <form class="needs-validation different-shipping-data-form" id="shipping-data-form" name="shipping-form">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="firstName">First name</label>
                        <input type="text" class="form-control" name="firstName" id="firstName" value='${shippingData ? shippingData.firstName : ""}'
                               required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="lastName">Last name</label>
                        <input type="text" class="form-control" name="lastName" id="lastName" value='${shippingData ? shippingData.lastName : ""}'
                               required>
                    </div>
                </div>
                <div class="mb-3">
                    <label for="email">Email <span class="text-muted">(Optional)</span></label>
                    <input type="email" class="form-control" name="email" id="email" value='${shippingData? shippingData.email : ""}'>
                </div>
                <div class="mb-3">
                    <label for="address">Address</label>
                    <input type="text" class="form-control" name="address" id="address" value='${shippingData ? shippingData.address : ""}'
                           required>
                </div>
                <div class="row">
                    <div class="col-md-5 mb-3">
                        <label for="country">Country</label>
                        <select class="custom-select d-block w-100" name="country" id="country" required>
                            <option value='${shippingData ? shippingData.country : ""}'>${shippingData ? shippingData.country : "Choose.."}</option>
                            <option>Hungary</option>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label for="state">State</label>
                        <select class="custom-select d-block w-100" name="state" id="state" required>
                            <option value='${shippingData ? shippingData.state : ""}'>${shippingData ? shippingData.state : "Choose..."}</option>
                            <option>Bacs-Kiskun</option>
                            <option>Baranya</option>
                            <option>Bekes</option>
                            <option>Borsod-Abauj-Zemplen</option>
                            <option>Budapest</option>
                            <option>Csongrad</option>
                            <option>Fejer</option>
                            <option>Gyor-Moson-Sopron</option>
                            <option>Hajdu-Bihar</option>
                            <option>Heves</option>
                            <option>Jasz-Nagykun-Szolnok</option>
                            <option>Komarom-Esztergom</option>
                            <option>Nograd</option>
                            <option>Pest</option>
                            <option>Somogy</option>
                            <option>Szabolcs-Szatmar-Bereg</option>
                            <option>Tolna</option>
                            <option>Vas</option>
                            <option>Veszprem</option>
                            <option>Zala</option>
                        </select>
                    </div>
                    <div class="col-md-3 mb-3">
                        <label for="zip">Zip</label>
                        <input type="number" class="form-control" name="zip" id="zip" value='${shippingData ? shippingData.zip : ""}' required>
                    </div>
                </div>`

        if (route !== "checkout") {
            div += `<a class="btn btn-primary" href="/myprofile">Back</a>
                    <button class="btn btn-primary" id="update-shipping-details" type="button">Save</button>
                    </form>
                </div>
               </div>`
        } else {
        div +=`  </form>
                </div>
            </div>`
        }
        return div
    },


}