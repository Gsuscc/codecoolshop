import {dataHandler} from "./dataHandler.js";
import {htmlParts} from "./staticHtmls.js";
import {validator} from "./validation.js";


export let dom = {

    init: function () {

        dom.addEventHandlerOnCart()
        if (window.location.pathname === "/order") {
            dom.addEventHandlerCheckBox()
            dom.addEventHandlerToPaymentButton()
        }
        dom.addEventHandlerOnMyOrderIdButton()
        if (window.location.pathname === "/myprofile") {
            dom.addEventHandlerOnAddBillingDetailsButton()
            dom.addEventHandlerOnAddShippingDetailsButton()
            dom.addEventHandlerOnEditBillingInfoButton()
            dom.addEventHandlerOnEditShippingInfoButton()
        }
    },

    addEventHandlerOnCart: function () {
        let cart = document.querySelector(".shopping-cart-img")
        if(cart !== null){
            cart.addEventListener("click", function () {
                dom.loadCart()
            })
        }
    },


    addEventOnDeleteIcon: function () {
        let deleteIcons = document.querySelectorAll(".delete-icon")
        for (let deleteIcon of deleteIcons) {
            deleteIcon.addEventListener("click", function (event) {
                dom.deleteFromCart(event)
            })
        }
    },

    addEventHandlerOnEmptyCartButton: function () {
        let button = document.querySelector("#empty-cart")
        if (button !== null) {
            button.addEventListener("click", function () {
                dataHandler.emptyCart(function (data) {
                    button.setAttribute('disabled', 'true')
                    dom.refreshCart(data)
                })
            })
        }
    },

    addEventHandlerOnSaveCartButton: function () {
        let button = document.querySelector("#save-cart")
        button.addEventListener("click", function () {
            dataHandler.saveCart(function () {
                    document.querySelector("#save-cart").setAttribute("disabled", "true")
                    document.querySelector("#save-cart").innerText = 'Update Cart'
                }, dom.getCartData()
            )
        })
    },

    addEventHandlerOnUpdateBillingDetailsButton: function () {
        let button = document.querySelector("#update-billing-details")
        if(button !== null)button.addEventListener("click", function () {
            dataHandler.updateBillingAddress(function (data) {
                window.location = `/myprofile?id=${data}`
            }, dom.getUpdatedBillingAddress())
        })
    },

    addEventHandlerOnUpdateShippingDetailsButton: function () {
        let button = document.querySelector("#update-shipping-details")
        if(button !== null) button.addEventListener("click", function () {
            dataHandler.updateShippingAddress(function (data) {
                window.location = `/myprofile?id=${data}`
            }, dom.getUpdatedShippingAddress())
        })
    },

    openModal: function (data) {
        dom.clearModal()
        dom.refreshCart(data)
        $("#modal").modal("show")
    },
    clearModal: function () {
        $("#modal-header").empty()
        $("#modal-body").empty()
    },

    showPaymentModal: function (data) {
        dom.addEventHandlerOnCreditCard()
        dom.addEventHandlerOnPayPal()
        this.getShippingFormData()
        this.getConfirmationFormData()
        dom.addEventHandlerOnCCfieldToFormat()
        dom.addEventHandlerOnExpiryDateToFormat()
        if (validator.validateShippingData(formData)) {
            dom.addEventHandlerToConfirmButton();
            $("#payment-modal").modal("show");
        } else {
            alert("Fill in all fields")
        }
    },

    loadCart: function () {
        dataHandler.loadCartData(function (data) {
            dom.openModal(data)
        })
    },

    addEventHandlerOnInputField: function () {
        let inputs = document.querySelectorAll("#quantity")
        for (let input of inputs) {
            input.addEventListener("input", (event) => {
                dom.changeQuantity(event)
            })
        }
    },
    countCartTotal: function () {
        let subTotals = document.querySelectorAll("#subtotal");
        let cartTotal = 0;
        for (let subTotal of subTotals) {
            cartTotal += parseFloat(subTotal.innerText);
        }
        return cartTotal;
    },


    deleteFromCart: function (event) {
        let prodId = event.target.parentNode.parentNode.firstElementChild.getAttribute("data-product-id")
        dataHandler.delete(function (data) {
            dom.refreshCart(data)
        }, prodId)
    },

    refreshCart: function (data) {
        dom.clearModal()
        dom.updateNotifyBadge(data);
        $("#modal-header").append(`<h4 class="modal-title" id="exampleModalLabel">Items in Cart</h4>`)
        $("#modal-body").append(htmlParts.buildTable(data))
        dom.addEventOnDeleteIcon()
        dom.addEventHandlerOnInputField()
        $("#modal-body").append(htmlParts.buildTotalLine())
        if (data.length !== 0){
            dom.addEventHandlerOnSaveCartButton()
            dom.addEventHandlerOnEmptyCartButton()
        }
        if (data.length === 0) {
            document.querySelector("#checkout-button").disabled = true
            let saveCartButton = document.querySelector("#save-cart");
            let emptyButton = document.querySelector("#empty-cart");
            saveCartButton !== null? saveCartButton.disabled = true: false
            emptyButton !== null? emptyButton.disabled = true: false
        }
        // dom.refreshCartBadge()
    },

    changeQuantity: function (event) {
        let quantity = event.target.value
        let prodId = event.target.parentNode.parentNode.firstElementChild.getAttribute("data-product-id")
        dataHandler.updateQuantity(function (data) {
            dom.refreshCart(data)
        }, quantity, prodId)
    },


    changePaymentToPayPal: function () {
        $("#payment-container").empty()
        $("#visa-master").css({opacity: 0.3});
        $("#paypal-img").css({opacity: 1});
        $("#payment-container").append(htmlParts.buildPayPalFields())
    },
    changeToPaymentCreditCard: function () {
        $("#payment-container").empty()
        $("#paypal-img").css({opacity: 0.3});
        $("#visa-master").css({opacity: 1});
        $("#payment-container").append(htmlParts.buildCreditCardFields())
    },

    addEventHandlerOnPayPal: function () {
        let paymentField = document.querySelector("#paypal");
        paymentField.addEventListener("click", dom.changePaymentToPayPal)
    },

    addEventHandlerOnCreditCard: function () {
        let paymentField = document.querySelector("#credit");
        paymentField.addEventListener("click", dom.changeToPaymentCreditCard)
    },

    addEventHandlerOnEditBillingInfoButton: function () {
        let button = document.querySelector("#edit-billing-details")
        if(button !== null) button.addEventListener("click", dom.getEditableBillingFields)
    },

    addEventHandlerOnEditShippingInfoButton: function () {
        let button = document.querySelector("#edit-shipping-details")
        if(button !== null) button.addEventListener("click", function (){
            dom.getEditableShippingFields()
        })
    },


    getConfirmationFormData: function () {
        let confirmationForm = document.getElementById('confirmation-form');
        let confirmationFormData = new FormData(confirmationForm);
        let confirmationObject = {};
        confirmationFormData.forEach(function (value, key) {
            confirmationObject[key] = value;
        });
        formData.confirmationData = confirmationObject;
        if (formData.shippingData === null) {
            formData.shippingData = confirmationObject;
        }
    },

    getShippingFormData: function () {
        if (document.getElementById('different-shipping').checked !== true) {
            let confirmationForm = document.querySelector('.different-shipping-data-form');
            let confirmationFormData = new FormData(confirmationForm);
            let confirmationObject = {};
            confirmationFormData.forEach(function (value, key) {
                confirmationObject[key] = value;
            });
            formData.shippingData = confirmationObject;
        }
    },


    getPaymentFormData: function () {
        let paymentForm = document.getElementById('payment-form');
        let paymentFormData = new FormData(paymentForm);
        let paymentObject = {};
        paymentFormData.forEach(function (value, key) {
            paymentObject[key] = value;
        });
        formData.paymentData = paymentObject;
    },

    addEventHandlerToPaymentButton: function () {
        let paymentButton = document.querySelector("#payment");
        paymentButton.addEventListener('click', () => {
            dom.showPaymentModal();
        })
    },

    addEventHandlerToConfirmButton: function () {
        let confirmButton = document.querySelector("#confirm");
        confirmButton.addEventListener('click', dom.sendPaymentData)
    },

    setModalAfterConfirmation: function () {
        let confirmButton = document.getElementById("confirm")
        $("#payment-modal-body").empty()
        setTimeout(function () {
            $("#payment-modal-body").empty()
            $("#payment-modal-body").css({padding: 70})
            $("#payment-modal-body").append(htmlParts.getSuccessMessage);
            confirmButton.removeEventListener("click", dom.addEventHandlerToConfirmButton)
            confirmButton.disabled = false;
            confirmButton.addEventListener("click", function () {
                dom.redirectToMainPage()
            })
            dom.redirectToMainPage()
        }, 3000)
        confirmButton.innerText = "Close"
        confirmButton.disabled = true
        $("#payment-modal-body").css('text-align', 'center');
        $("#payment-modal-body").css({padding: 70})
        $("#payment-modal-body").append(htmlParts.getPleaseWaitMessage())
        $("#payment-modal-body").append(htmlParts.getLoadingScreenForModal())
        document.querySelector("#payment-modal-back-button").remove()
    },

    redirectToMainPage: function () {
        setTimeout(function () {
            window.location = "/"
        }, 10000)
    },

    sendPaymentData: function () {
        dom.getPaymentFormData();
        if (validator.validatePaymentData(formData)) {
            dataHandler.sendPaymentDetails(formData, function (response) {
                dom.setModalAfterConfirmation();
            })
        }
    },

    addEventHandlerOnMyOrderIdButton: function () {
        let buttons = document.querySelectorAll(".my-order-id")
        for (let button of buttons) {
            button.addEventListener("click", function (event) {
                let orderId = event.target.getAttribute("data")
                dom.getOrderDetails(orderId);
            })
        }
    },

    getOrderDetails: function (orderId) {
        dataHandler.getOrderById(function (data) {
            dom.showOrderDetails(data)
        }, orderId)
    },

    showOrderDetails: function (data) {
        $(".my-order-container").empty()
        $(".my-order-container").append(htmlParts.buildMyOrderTable(data))
        $(".my-order-container").append(`<button class="btn btn-primary" onclick="window.location.reload()" >Back</button>
`)
    },

    getCartData: function () {
        let cartDataList = [];
        let cartTableData = document.querySelectorAll(".line-item")
        console.log(cartTableData)
        for (let line of cartTableData) {
            let cartData = {
                productId: line.children[0].getAttribute("data-product-id"),
                name: line.children[0].innerHTML,
                quantity: line.children[1].firstChild.value,
                unitPrice: line.children[2].innerHTML.split(" ")[0],
                currency: line.children[2].innerHTML.split(" ")[1]
            }
            cartDataList.push(cartData);
        }
        return cartDataList
    },

    getBillingAddress: function () {
        let billingAddressData = document.querySelector("#billing-info-container") !== null? document.querySelector("#billing-info-container").children: null
        return billingAddressData !== null?{
            firstName: billingAddressData[0].lastElementChild.innerHTML,
            lastName: billingAddressData[1].lastElementChild.innerHTML,
            email: billingAddressData[2].lastElementChild.innerHTML,
            address: billingAddressData[3].lastElementChild.innerHTML,
            country: billingAddressData[4].lastElementChild.innerHTML,
            state: billingAddressData[5].lastElementChild.innerHTML,
            zip: billingAddressData[6].lastElementChild.innerHTML,
        }:0;
    },

    getShippingAddress: function () {
        let shippingAddressData = document.querySelector("#shipping-info-container") !== null? document.querySelector("#shipping-info-container").children: null
        return shippingAddressData !== null? {
            firstName: shippingAddressData[0].lastElementChild.innerHTML,
            lastName: shippingAddressData[1].lastElementChild.innerHTML,
            email: shippingAddressData[2].lastElementChild.innerHTML,
            address: shippingAddressData[3].lastElementChild.innerHTML,
            country: shippingAddressData[4].lastElementChild.innerHTML,
            state: shippingAddressData[5].lastElementChild.innerHTML,
            zip: shippingAddressData[6].lastElementChild.innerHTML,
        }: 0;
    },


    getEditableBillingFields: function () {
        let billingData = dom.getBillingAddress()
        $('.my-billing-profile-container').empty()
        $('.my-billing-profile-container').append(htmlParts.buildEditableBillingFields(billingData))
        dom.addEventHandlerOnUpdateBillingDetailsButton();

    },

    getEditableShippingFields: function (data) {
        let shippingDate = data !== undefined ? data[0]: dom.getShippingAddress()
        $('.my-shipping-profile-container').empty()
        $('.my-shipping-profile-container').append(htmlParts.buildEditableShippingFields(shippingDate))
        dom.addEventHandlerOnUpdateShippingDetailsButton();

    },

    getUpdatedBillingAddress() {
        let billingForm = document.getElementById('billing-data-form');
        let billingFormData = new FormData(billingForm);
        let billingObject = {};
        billingFormData.forEach(function (value, key) {
            billingObject[key] = value;
        });
        return billingObject;
    },

    getUpdatedShippingAddress: function () {
        let shippingForm = document.getElementById('shipping-data-form');
        let shippingFormData = new FormData(shippingForm);
        let shippingObject = {};
        shippingFormData.forEach(function (value, key) {
            shippingObject[key] = value;
        });
        return shippingObject;
    },


    addEventHandlerCheckBox() {
        let checkbox = document.querySelector("#different-shipping")
        checkbox.addEventListener("change", function () {
            dataHandler.getShippingInfoForCheckout(function (data) {
                if (checkbox.checked != true) {
                    $('#shipping-info-container').empty();
                    $('#shipping-info-container').append(htmlParts.buildEditableShippingFields(data === null? false: data[0], "checkout"));
                } else {
                    $('#shipping-info-container').empty();
                }
            })
        })
    },
    addEventHandlerOnCCfieldToFormat: function (){
        $('#cc-number').keyup(function() {
            let field = $(this).val().split("-").join("");
            if (field.length > 0) {
                field = field.match(new RegExp('.{1,4}', 'g')).join("-");
            }
            $(this).keydown( function(e){
                if ($(this).val().length >= 18) {
                    $(this).val($(this).val().substr(0, 18));
                }
            });
            $(this).val(field);
        });
    },

    addEventHandlerOnExpiryDateToFormat: function () {
        $('#cc-expiration').keyup(function() {
            let field = $(this).val().split("/").join("");
            if (field.length > 0) {
                field = field.match(new RegExp('.{1,2}', 'g')).join("/");
            }
            $(this).keydown( function(e){
                if ($(this).val().length >= 4) {
                    $(this).val($(this).val().substr(0, 4));
                }
            });
            $(this).val(field);
        });
    },

    updateNotifyBadge(data) {
        let itemCounterSpan = document.querySelector(".notify-badge");
        itemCounterSpan.innerText = data? dom.getTotalCartQuantity(data): 0
    },

    getTotalCartQuantity(data) {
        let count = 0
        for (let item of data) {
            count += item.quantity;
        }
        return count
    },

    addEventHandlerOnAddBillingDetailsButton(){
        let button = document.querySelector(".add-billing-details")
        if (button) button.addEventListener("click", function () {
                dom.getEmptyBillingFields()
        })
    },

    addEventHandlerOnAddShippingDetailsButton(){
        let button = document.querySelector(".add-shipping-details")
        if (button) button.addEventListener("click", function () {
                dom.getEmptyShippingFields();
        })
    },

    getEmptyBillingFields: function(){
        $('.my-billing-profile-container').empty()
        $('.my-billing-profile-container').append(htmlParts.buildEditableBillingFields(""))
        dom.addEventHandlerOnUpdateBillingDetailsButton();
    },

    getEmptyShippingFields: function(){
        $('.my-shipping-profile-container').empty()
        $('.my-shipping-profile-container').append(htmlParts.buildEditableShippingFields(""))
        dom.addEventHandlerOnUpdateShippingDetailsButton();
    }


}

let formData = {
    confirmationData: null,
    paymentData: null,
    shippingData: null
}
