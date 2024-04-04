package page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import data.CardInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {

    private final ElementsCollection fields = $$(".input__box input");
    private final SelenideElement cardNumberInput = fields.get(0);
    private final SelenideElement monthInput = fields.get(1);
    private final SelenideElement yearInput = fields.get(2);
    private final SelenideElement ownerInput = fields.get(3);
    private final SelenideElement cvcInput = fields.get(4);
    private final SelenideElement btnSend = $("form button");
    private final ElementsCollection collectionInvalidMsg = $$(".input_invalid .input__sub");
    private final SelenideElement notificationMsg = $(".notification__title");

    public PaymentPage() {
        SelenideElement header = $("button ~ h3.heading");
        header.shouldBe(visible);
    }

    public void fillCardData(CardInfo info) {
        cardNumberInput.setValue(info.getNumber());
        monthInput.setValue(info.getMonth());
        yearInput.setValue(info.getYear());
        ownerInput.setValue(info.getHolder());
        cvcInput.setValue(info.getCvc());
        sendForm();
    }

    public void sendForm() {
        btnSend.click();
    }

    public ElementsCollection getCollectionInvalidMsg(){
        return collectionInvalidMsg;
    }
    private SelenideElement getInvalidMsg(int indexField){
        return fields.get(indexField).parent().parent().find(".input_invalid .input__sub");
    }

    public SelenideElement getInvalidMsgCardNumber(){
        return getInvalidMsg(0);
    }

    public SelenideElement getInvalidMsgMonth(){
        return getInvalidMsg(1);
    }

    public SelenideElement getInvalidMsgYear(){
        return getInvalidMsg(2);
    }

    public SelenideElement getInvalidMsgOwner(){
        return getInvalidMsg(3);
    }

    public SelenideElement getInvalidMsgCVC(){
        return getInvalidMsg(4);
    }
    public String validPayment(CardInfo info) {
        fillCardData(info);
        return notificationMsg
                .shouldBe(visible, Duration.ofSeconds(15))
                .getText();
    }

    private String getFieldValue(int fieldIndex){
        return fields.get(fieldIndex).getValue();
    }

    public String getFieldValueCardNumber(){
        return getFieldValue(0);
    }

    public String getFieldValueCVC(){
        return getFieldValue(4);
    }
}
