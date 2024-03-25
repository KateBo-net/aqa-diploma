package page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.CardInfo;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {

    private final ElementsCollection fields = $$(".input__control");
    private final SelenideElement cardNumberInput = fields.get(0);
    private final SelenideElement monthInput = fields.get(1);
    private final SelenideElement yearInput = fields.get(2);
    private final SelenideElement ownerInput = fields.get(3);
    private final SelenideElement cvcInput = fields.get(4);
    private final SelenideElement btnSend = $("form button");
    private final ElementsCollection inputInvalidMsg = $$(".input_invalid .input__sub");
    private final SelenideElement notificationMsg = $(".notification__title");

    public PaymentPage() {
        SelenideElement header = $("button ~ h3.heading");
        header.shouldBe(visible);
    }

    public void fillCardData(CardInfo info) {
        cardNumberInput.setValue(info.getCardNumber());
        monthInput.setValue(info.getMonth());
        yearInput.setValue(info.getYear());
        ownerInput.setValue(info.getOwner());
        cvcInput.setValue(info.getCvc());
        sendForm();
    }

    public void sendForm() {
        btnSend.click();
    }

    public SelenideElement validPayment(CardInfo info) {
        fillCardData(info);
        return notificationMsg;
    }

    public ElementsCollection invalidPayment(CardInfo info) {
        fillCardData(info);
        return inputInvalidMsg;
    }

    public ElementsCollection sendEmptyFields(){
        sendForm();
        return inputInvalidMsg;
    }
}
