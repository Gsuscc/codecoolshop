export let validator = {
    validateShippingData: function (formData) {
        let mailFormat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/;
        for (let item of Object.keys(formData.confirmationData)) {
            if (formData.confirmationData[item] === "") {
                this.highlightFields(item, 'invalid')
                return false
            } else {
                if (item === "email" && !(formData.confirmationData['email'].match(mailFormat))) {
                    this.highlightFields(item, 'invalid')
                    return false
                }
                this.highlightFields(item)
            }
        }
        return true
    },


    highlightFields: function (item, state) {
        let fieldToHighlight = document.querySelector(`#${item}`)
        if (state === 'invalid') {
            fieldToHighlight.style.borderColor = "red"
        } else {
            fieldToHighlight.style.borderColor = "green"
        }
    },

    validatePaymentData: function (formData) {
        if (document.getElementById('paypal').checked) {
            return this.validatePayPalFields(formData.paymentData)
        } else {
            return this.validateCcFields(formData.paymentData)
        }
    },

    validatePayPalFields(paymentData) {
        let mailFormat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,})+$/;
        for (let field of Object.keys(paymentData)) {
            if (paymentData[field] === 'on') continue
            if (field === 'pp-mail' && !(paymentData['pp-mail'].match(mailFormat))) {
                this.highlightFields(field, "invalid")
                return false
            } else if (paymentData[field] === "" && paymentData[field] !== "on") {
                this.highlightFields(field, "invalid")
                return false
            } else {
                this.highlightFields(field)
            }
        }
        return true
    },

    validateCcFields(paymentData) {
        for (let field of Object.keys(paymentData)) {
            if (paymentData[field] === "") {
                this.highlightFields(field, "invalid")
                return false
            } else {
                if (paymentData[field] !== "on") {
                    this.highlightFields(field)
                }
            }
        }
        return true
    }
}